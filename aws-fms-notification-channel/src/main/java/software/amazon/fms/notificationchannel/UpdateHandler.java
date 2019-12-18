package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.FmsException;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.PutNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandler<CallbackContext> {

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
            // get the notification channel before attempting to update and fail if it does not exist
            final GetNotificationChannelResponse response =
                    proxy.injectCredentialsAndInvokeV2(getNotificationChannelRequest, client::getNotificationChannel);
            if (response.snsTopicArn() == null) {
                return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
            }

            // send update request if the notification channel does not exist
            proxy.injectCredentialsAndInvokeV2(putNotificationChannelRequest, client::putNotificationChannel);
        } catch(ResourceNotFoundException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
        } catch(InvalidOperationException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.InvalidRequest, null);
        } catch(FmsException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.ServiceInternalError, null);
        }

        // successful update request
        return ProgressEvent.success(model, callbackContext);
    }
}
