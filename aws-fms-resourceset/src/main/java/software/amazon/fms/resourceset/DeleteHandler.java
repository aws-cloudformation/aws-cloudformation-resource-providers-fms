package software.amazon.fms.resourceset;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.DeleteResourceSetRequest;
import software.amazon.awssdk.services.fms.model.DeleteResourceSetResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends ResourceSetHandler<DeleteResourceSetResponse> {

    DeleteHandler() {
        super();
    }

    DeleteHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected DeleteResourceSetResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger
    ) {

        // build the delete request
        final DeleteResourceSetRequest.Builder deletePolicyRequest = DeleteResourceSetRequest.builder()
                .identifier(request.getDesiredResourceState().getId());

        // make the delete request
        final DeleteResourceSetResponse response = proxy.injectCredentialsAndInvokeV2(
                deletePolicyRequest.build(),
                client::deleteResourceSet);
        logRequest(response, logger);
        return response;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final DeleteResourceSetResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy
    ) {
        return ProgressEvent.defaultSuccessHandler(null);
    }
}
