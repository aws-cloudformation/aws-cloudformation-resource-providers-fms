package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.*;
import software.amazon.cloudformation.proxy.*;

public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        // get the desired resource model and fms client
        final ResourceModel model = request.getDesiredResourceState();
        final FmsClient client = FmsClient.create();

        // build the GetNotificationChannelRequest
        final GetNotificationChannelRequest getNotificationChannelRequest =
                GetNotificationChannelRequest.builder().build();

        // build the PutNotificationChannelRequest based on desired resource state
        final PutNotificationChannelRequest putNotificationChannelRequest = PutNotificationChannelRequest.builder()
                .snsTopicArn(model.getSnsTopicArn())
                .snsRoleName(model.getSnsRoleName())
                .build();

        try {
            // get the notification channel before attempting to create and fail if it already exists
            final GetNotificationChannelResponse response =
                    proxy.injectCredentialsAndInvokeV2(getNotificationChannelRequest, client::getNotificationChannel);
            if (response.snsTopicArn() != null) {
                return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.AlreadyExists, null);
            }

            // send create request if the notification channel does not exist
            proxy.injectCredentialsAndInvokeV2(putNotificationChannelRequest, client::putNotificationChannel);
        } catch(ResourceNotFoundException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
        } catch(InvalidOperationException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.InvalidRequest, null);
        } catch(FmsException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.ServiceInternalError, null);
        }

        // successful creation request
        return ProgressEvent.success(model, callbackContext);
    }
}
