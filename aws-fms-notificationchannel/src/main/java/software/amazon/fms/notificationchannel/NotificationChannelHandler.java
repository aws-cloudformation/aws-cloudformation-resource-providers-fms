package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.FmsResponse;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.InternalErrorException;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

abstract class NotificationChannelHandler extends BaseHandler<CallbackContext> {

    /** FMS client instance to make requests on behalf of CloudFormation. */
    protected final FmsClient client;

    /** Standard read request to check pre-action resource state. */
    private final GetNotificationChannelRequest getNotificationChannelRequest;

    /** Constructor for use by CloudFormation, uses default FMS client. */
    NotificationChannelHandler() {
        client = FmsClient.create();
        getNotificationChannelRequest = GetNotificationChannelRequest.builder().build();
    }

    /**
     * Constructor for use in tests, allows for a mocked client.
     * @param client The FmsClient to use.
     */
    NotificationChannelHandler(final FmsClient client) {
        this.client = client;
        getNotificationChannelRequest = GetNotificationChannelRequest.builder().build();
    }

    /**
     * Flag to enable failure events if the notification channel already exists.
     * @return A flag indicating if this failure event is enabled.
     */
    boolean throwAlreadyExistsException() {
        return false;
    }

    /**
     * Flag to enable failure events if the notification channel does not exist.
     * @return A flag indicating if this failure event is enabled.
     */
    boolean throwNotFoundException() {
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
            final GetNotificationChannelResponse getNotificationChannelResponse,
            final Logger logger);

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
     * Logs the requestId of an FmsResponse.
     * @param response FmsResponse to get the requestId from.
     * @param logger CloudWatch logger.
     */
    static void logRequest(final FmsResponse response, Logger logger) {

        String requestId;
        try {
             requestId = response.responseMetadata().requestId();
        } catch (NullPointerException e) {
            requestId = "null";
        }
        logger.log(String.format("%s Id: %s", response.getClass().getSimpleName(), requestId));
    }

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
            // attempt to get an existing notification channel
            getNotificationChannelResponse =
                    proxy.injectCredentialsAndInvokeV2(getNotificationChannelRequest, client::getNotificationChannel);
            logRequest(getNotificationChannelResponse, logger);

            // handlers fail differently based on the result of the notification channel get request
            // allow for failing based on notification channel existence or non-existence
            if (throwAlreadyExistsException() && getNotificationChannelResponse.snsTopicArn() != null) {
                return ProgressEvent.failed(
                        null,
                        callbackContext,
                        HandlerErrorCode.AlreadyExists,
                        "Notification Channel already exists");
            }
            if (throwNotFoundException() && getNotificationChannelResponse.snsTopicArn() == null) {
                return ProgressEvent.failed(
                        null,
                        callbackContext,
                        HandlerErrorCode.NotFound,
                        "Notification Channel not found");
            }

            // make the primary handler request
            makeRequest(proxy, request.getDesiredResourceState(), getNotificationChannelResponse, logger);
        } catch(CfnAlreadyExistsException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.AlreadyExists,
                    "The resource cannot be updated. Please delete and recreate the CloudFormation resource.");
        } catch(ResourceNotFoundException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        } catch(InvalidOperationException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
        } catch(InternalErrorException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.ServiceInternalError);
        }

        // let each handler construct its own success resource model
        return ProgressEvent.defaultSuccessHandler(constructSuccessResourceState(
                request.getDesiredResourceState(), getNotificationChannelResponse
        ));
    }
}
