package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.PutNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.PutNotificationChannelResponse;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends NotificationChannelHandler {

    UpdateHandler() {
        super();
    }

    UpdateHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected boolean throwNotFoundException() {
        return true;
    }

    @Override
    protected PutNotificationChannelResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse,
            final Logger logger) {
        final PutNotificationChannelRequest putNotificationChannelRequest = PutNotificationChannelRequest.builder()
                .snsTopicArn(desiredResourceState.getSnsTopicArn())
                .snsRoleName(desiredResourceState.getSnsRoleName())
                .build();
        final PutNotificationChannelResponse response =
                proxy.injectCredentialsAndInvokeV2(putNotificationChannelRequest, client::putNotificationChannel);
        logRequest(response, logger);
        return response;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final GetNotificationChannelResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy) {
        return ProgressEvent.defaultSuccessHandler(request.getDesiredResourceState());
    }
}
