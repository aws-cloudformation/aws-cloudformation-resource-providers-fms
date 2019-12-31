package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.FmsResponse;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public abstract class NotificationChannelHandler extends BaseHandler<CallbackContext> {

    /** FMS client instance to make requests on behalf of CloudFormation. */
    protected final FmsClient client;

    /** Standard read request to check pre-action resource state. */
    private final GetNotificationChannelRequest getNotificationChannelRequest;

    /** Constructor. */
    NotificationChannelHandler() {
        client = FmsClient.create();
        getNotificationChannelRequest = GetNotificationChannelRequest.builder().build();
    }

    /**
     * Flag to enable failure events if the notification channel already exists.
     * @return A flag indicating if this failure event is enabled.
     */
    protected boolean throwAlreadyExistsException() {
        return false;
    }

    /**
     * Flag to enable failure events if the notification channel does not exist.
     * @return A flag indicating if this failure event is enabled.
     */
    protected boolean throwNotFoundException() {
        return false;
    }

    /**
     * Hook called by handleRequest to make the primary action (create, read, etc..) request on the FMS API.
     * @param proxy AWS proxy to make requests.
     * @param desiredResourceState CloudFormation's desired resource state.
     * @param getNotificationChannelResponse Notification channel get request response.
     * @return Response from the FMS API.
     */
    protected abstract FmsResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse);

    /**
     * Hook called by handleRequest to build the resource's state after successful makeRequest call.
     * @param desiredResourceState CloudFormation's desired resource state.
     * @param getNotificationChannelResponse Notification channel get request response.
     * @return Post-action resource state.
     */
    protected abstract ResourceModel constructSuccessResourceState(
            final ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse);

    /**
     * Hook called by CloudFormation to run resource management actions.
     * @param proxy AWS proxy to make requests.
     * @param request CloudFormation's requested resource state.
     * @param callbackContext Resource's post-action context.
     * @param logger CloudWatch logger.
     * @return Success event with new resource state or failure event with handler error code.
     */
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        GetNotificationChannelResponse getNotificationChannelResponse;
        try {
            // attempt an existing notification channel
            getNotificationChannelResponse =
                    proxy.injectCredentialsAndInvokeV2(getNotificationChannelRequest, client::getNotificationChannel);

            // handlers fail differently based on the result of the notification channel get request
            // allow for failing based on notification channel existence or non-existence
            if (throwAlreadyExistsException() && getNotificationChannelResponse.snsTopicArn() != null) {
                return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.AlreadyExists, null);
            }
            if (throwNotFoundException() && getNotificationChannelResponse.snsTopicArn() == null) {
                return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
            }

            // make the primary handler request
            makeRequest(proxy, request.getDesiredResourceState(), getNotificationChannelResponse);
        } catch(ResourceNotFoundException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
        } catch(InvalidOperationException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.InvalidRequest, null);
        }

        // let each handler construct its own success resource model
        return ProgressEvent.success(constructSuccessResourceState(request.getDesiredResourceState(), getNotificationChannelResponse), callbackContext);
    }
}
