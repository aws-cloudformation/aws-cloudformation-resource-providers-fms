package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.PutNotificationChannelResponse;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

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

        // there was a change to how Uluru handles stack updates that broke the update functionality
        // this is a temporary change to give customers a more descriptive error when they try to make a stack update
        throw new CfnAlreadyExistsException(new Exception("This resource type does no support update actions."));
    }

    @Override
    protected ResourceModel constructSuccessResourceState(
            final ResourceModel desiredResourceState,
            final GetNotificationChannelResponse getNotificationChannelResponse) {

        // use the desired resource state as the post-update resource state
        return desiredResourceState;
    }
}
