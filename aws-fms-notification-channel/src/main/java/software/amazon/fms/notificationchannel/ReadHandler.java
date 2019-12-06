package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.*;
import software.amazon.cloudformation.proxy.*;

public class ReadHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        // define the fms client
        final FmsClient client = FmsClient.create();

        // build the GetNotificationChannelRequest
        final GetNotificationChannelRequest getNotificationChannelRequest =
                GetNotificationChannelRequest.builder().build();

        // get the notification channel
        final GetNotificationChannelResponse response;
        try {
            response = proxy.injectCredentialsAndInvokeV2(getNotificationChannelRequest, client::getNotificationChannel);
            if (response.snsTopicArn() == null) {
                return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
            }
        } catch(ResourceNotFoundException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
        } catch(InvalidOperationException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.InvalidRequest, null);
        } catch(FmsException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.ServiceInternalError, null);
        }

        // assemble the resource model from the request
        final ResourceModel resourceModel = ResourceModel.builder()
                .snsRoleName(response.snsRoleName())
                .snsTopicArn(response.snsTopicArn())
                .build();

        // successful read request
        return ProgressEvent.success(resourceModel, callbackContext);
    }
}
