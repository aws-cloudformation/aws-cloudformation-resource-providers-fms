package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.model.DeletePolicyRequest;
import software.amazon.awssdk.services.fms.model.DeletePolicyResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;

public class DeleteHandler extends PolicyHandler<DeletePolicyResponse> {

    @Override
    protected DeletePolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState) {

        final DeletePolicyRequest deletePolicyRequest = DeletePolicyRequest.builder()
                .policyId(desiredResourceState.getPolicyId())
                .deleteAllPolicyResources(true)
                .build();
        return proxy.injectCredentialsAndInvokeV2(deletePolicyRequest, client::deletePolicy);
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(final DeletePolicyResponse response) {

        return ResourceModel.builder().build();
    }
}
