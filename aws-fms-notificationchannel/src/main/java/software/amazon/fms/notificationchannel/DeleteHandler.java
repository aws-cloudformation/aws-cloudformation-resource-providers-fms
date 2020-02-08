package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.model.DeleteNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.DeleteNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

public class DeleteHandler extends NotificationChannelHandler {

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
    protected ResourceModel constructSuccessResourceState(
            final ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse) {

        // use the desired resource state as the post-delete resource state
        return desiredResourceState;
    }
}
