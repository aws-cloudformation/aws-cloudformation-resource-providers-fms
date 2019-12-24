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
                .policyId(desiredResourceState.getPolicyId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(getPolicyRequest, client::getPolicy);
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(final GetPolicyResponse response) {

        ResourceModel resourceModel = CfnHelper.convertFMSPolicyToCFNResourceModel(response.policy());
        resourceModel.setPolicyArn(response.policyArn());
        return resourceModel;
    }
}
