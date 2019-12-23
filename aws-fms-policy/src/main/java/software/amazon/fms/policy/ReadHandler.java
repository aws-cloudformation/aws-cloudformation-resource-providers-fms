package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.fms.policy.helpers.CfnHelper;

public class ReadHandler extends PolicyHandler<GetPolicyResponse> {

    @Override
    protected GetPolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState) {

        final GetPolicyRequest getPolicyRequest = GetPolicyRequest.builder()
                .policyId(desiredResourceState.getPolicy().getPolicyId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(getPolicyRequest, client::getPolicy);
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(final GetPolicyResponse response) {

        return ResourceModel.builder()
                .policy(CfnHelper.convertFMSPolicyToCFNPolicy(response.policy()))
                .policyArn(response.policyArn())
                .build();
    }
}
