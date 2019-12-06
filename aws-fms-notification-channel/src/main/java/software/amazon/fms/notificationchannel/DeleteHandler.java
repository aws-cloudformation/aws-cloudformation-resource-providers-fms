package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.*;
import software.amazon.cloudformation.proxy.*;

public class DeleteHandler extends BaseHandler<CallbackContext> {

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

        // build the DeleteNotificationChannelRequest
        final DeleteNotificationChannelRequest deleteNotificationChannelRequest =
                DeleteNotificationChannelRequest.builder().build();

        // inject creds from proxy and make request
        try {
            // get the notification channel before attempting to delete and fail if it does not exists
            final GetNotificationChannelResponse response =
                    proxy.injectCredentialsAndInvokeV2(getNotificationChannelRequest, client::getNotificationChannel);
            if (response.snsTopicArn() == null) {
                return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
            }

            proxy.injectCredentialsAndInvokeV2(deleteNotificationChannelRequest, client::deleteNotificationChannel);
        } catch(ResourceNotFoundException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
        } catch(InvalidOperationException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.InvalidRequest, null);
        } catch(FmsException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.ServiceInternalError, null);
        }

        // successful deletion request
        return ProgressEvent.success(model, callbackContext);
    }
}
