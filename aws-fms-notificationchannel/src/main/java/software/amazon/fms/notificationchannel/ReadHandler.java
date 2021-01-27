package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends NotificationChannelHandler {

    ReadHandler() {
        super();
    }

    ReadHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected boolean throwNotFoundException() {
        return true;
    }

    @Override
    protected GetNotificationChannelResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse,
            final Logger logger) {

        // the read request has already been made as a check in the NotificationChannelHandler, use its results
        return getNotificationChannelResponse;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final GetNotificationChannelResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy) {
        return ProgressEvent.defaultSuccessHandler(
                ResourceModel.builder()
                        .snsRoleName(response.snsRoleName())
                        .snsTopicArn(response.snsTopicArn())
                        .build()
        );
    }
}
