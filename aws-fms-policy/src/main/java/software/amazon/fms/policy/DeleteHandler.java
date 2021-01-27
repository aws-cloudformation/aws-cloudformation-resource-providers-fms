package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.DeletePolicyRequest;
import software.amazon.awssdk.services.fms.model.DeletePolicyResponse;
import software.amazon.awssdk.services.fms.model.PutPolicyResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends PolicyHandler<DeletePolicyResponse> {

    DeleteHandler() {
        super();
    }

    DeleteHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected DeletePolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger) {

        // build the delete request
        final DeletePolicyRequest.Builder deletePolicyRequest = DeletePolicyRequest.builder()
                .policyId(request.getDesiredResourceState().getId())
                .deleteAllPolicyResources(request.getDesiredResourceState().getDeleteAllPolicyResources());

        // determine if resource cleanup should be performed
        try {
            if (request.getDesiredResourceState().getDeleteAllPolicyResources()) {
                deletePolicyRequest.deleteAllPolicyResources(true);
                logger.log("DeleteAllPolicyResources is TRUE, performing appropriate resource cleanup on delete");
            } else {
                deletePolicyRequest.deleteAllPolicyResources(false);
                logger.log("DeleteAllPolicyResources is FALSE, no resource cleanup performed");
            }
        } catch (NullPointerException e) {
            logger.log("DeleteAllPolicyResources is not specified, no resource cleanup performed");
        }

        // make the delete request
        final DeletePolicyResponse response = proxy.injectCredentialsAndInvokeV2(
                deletePolicyRequest.build(),
                client::deletePolicy);
        logRequest(response, logger);
        return response;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final DeletePolicyResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy) {
        return ProgressEvent.defaultSuccessHandler(ResourceModel.builder().build());
    }
}
