package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.DeleteNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.DeleteNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends NotificationChannelHandler {

    DeleteHandler() {
        super();
    }

    DeleteHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected boolean throwNotFoundException() {
        return true;
    }

    @Override
    protected DeleteNotificationChannelResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse,
            final Logger logger) {

        // send the delete request
        final DeleteNotificationChannelRequest deleteNotificationChannelRequest =
                DeleteNotificationChannelRequest.builder().build();
        final DeleteNotificationChannelResponse response =
                proxy.injectCredentialsAndInvokeV2(deleteNotificationChannelRequest, client::deleteNotificationChannel);
        logRequest(response, logger);
        return response;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final GetNotificationChannelResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy) {
        return ProgressEvent.defaultSuccessHandler(null);
    }
}
