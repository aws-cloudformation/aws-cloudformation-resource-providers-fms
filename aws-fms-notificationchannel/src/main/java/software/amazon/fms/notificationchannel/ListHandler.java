package software.amazon.fms.notificationchannel;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.FmsResponse;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class ListHandler extends NotificationChannelHandler {

    ListHandler() {
        super();
    }

    ListHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected boolean shouldReturnEmptyList() {
        return true;
    }

    @Override
    protected FmsResponse makeRequest(final AmazonWebServicesClientProxy proxy,
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
        List<ResourceModel> resourceModels = new ArrayList<>();

        if (response.snsTopicArn() != null) {
            // convert the read request response to resource models
            resourceModels.add(
                    ResourceModel.builder()
                            .snsRoleName(response.snsRoleName())
                            .snsTopicArn(response.snsTopicArn())
                            .build()
            );
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(resourceModels)
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
