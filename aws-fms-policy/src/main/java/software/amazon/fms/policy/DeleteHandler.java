package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.model.DeletePolicyRequest;
import software.amazon.awssdk.services.fms.model.DeletePolicyResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends PolicyHandler<DeletePolicyResponse> {

    @Override
    protected DeletePolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request) {

        // make the delete request
        final DeletePolicyRequest deletePolicyRequest = DeletePolicyRequest.builder()
                .policyId(request.getDesiredResourceState().getPolicyId())
                .deleteAllPolicyResources(true)
                .build();
        return proxy.injectCredentialsAndInvokeV2(deletePolicyRequest, client::deletePolicy);
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(
            final DeletePolicyResponse response,
            ResourceHandlerRequest<ResourceModel> request) {

        // create an empty resource model since the resource no longer exists
        return ResourceModel.builder().build();
    }
}
