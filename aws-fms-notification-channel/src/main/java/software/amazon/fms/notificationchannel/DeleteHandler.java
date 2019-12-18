package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.model.DeleteNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.DeleteNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;

public class DeleteHandler extends NotificationChannelHandler {

    @Override
    protected boolean throwNotFoundException() {
        return true;
    }

    @Override
    protected DeleteNotificationChannelResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse) {

        // send the delete request
        final DeleteNotificationChannelRequest deleteNotificationChannelRequest =
                DeleteNotificationChannelRequest.builder().build();
        return proxy.injectCredentialsAndInvokeV2(deleteNotificationChannelRequest, client::deleteNotificationChannel);
    }

    @Override
    protected ResourceModel constructSuccessResourceState(
            ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse) {

        // use the desired resource state as the post-delete resource state
        return desiredResourceState;
    }
}
