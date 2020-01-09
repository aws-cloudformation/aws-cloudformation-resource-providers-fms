package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.awssdk.services.fms.model.PutPolicyRequest;
import software.amazon.awssdk.services.fms.model.PutPolicyResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.policy.helpers.CfnHelper;
import software.amazon.fms.policy.helpers.FmsHelper;

public class UpdateHandler extends PolicyHandler<PutPolicyResponse> {

    @Override
    protected PutPolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request) {

        // make a read request to retrieve an up-to-date PolicyUpdateToken
        final GetPolicyRequest getPolicyRequest = GetPolicyRequest.builder()
                .policyId(request.getDesiredResourceState().getPolicyId())
                .build();
        GetPolicyResponse response = proxy.injectCredentialsAndInvokeV2(getPolicyRequest, client::getPolicy);

        // make the update request
        final PutPolicyRequest putPolicyRequest = PutPolicyRequest.builder()
                .policy(FmsHelper.convertCFNResourceModelToFMSPolicy(request.getDesiredResourceState(), response.policy().policyUpdateToken()))
                .build();
        return proxy.injectCredentialsAndInvokeV2(putPolicyRequest, client::putPolicy);
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(final PutPolicyResponse response) {

        // convert the update request response to a resource model
        return CfnHelper.convertFMSPolicyToCFNResourceModel(response.policy(), response.policyArn());
    }
}
