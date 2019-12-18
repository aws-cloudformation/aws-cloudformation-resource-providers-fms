package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;

public class ReadHandler extends NotificationChannelHandler {

    @Override
    protected boolean throwNotFoundException() {
        return true;
    }

    @Override
    protected GetNotificationChannelResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse) {

        // the read request has already been made as a check in the NotificationChannelHandler, use its results
        return getNotificationChannelResponse;
    }

    @Override
    protected ResourceModel constructSuccessResourceState(
            ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse) {

        // assemble the post-read resource state from the read request
        return ResourceModel.builder()
                .snsRoleName(getNotificationChannelResponse.snsRoleName())
                .snsTopicArn(getNotificationChannelResponse.snsTopicArn())
                .build();
    }
}
