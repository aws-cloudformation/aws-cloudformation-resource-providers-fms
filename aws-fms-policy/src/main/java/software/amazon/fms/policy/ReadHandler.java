package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.policy.helpers.CfnHelper;
import software.amazon.fms.policy.helpers.FmsHelper;

public class ReadHandler extends PolicyHandler<GetPolicyResponse> {

    @Override
    protected GetPolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request) {

        // make the read request
        final GetPolicyRequest getPolicyRequest = GetPolicyRequest.builder()
                .policyId(request.getDesiredResourceState().getPolicyId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(getPolicyRequest, client::getPolicy);
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(
            final GetPolicyResponse response,
            ResourceHandlerRequest<ResourceModel> request) {

        // convert the read request response to a resource model
        return CfnHelper.convertFMSPolicyToCFNResourceModel(
                response.policy(),
                response.policyArn(),
                FmsHelper.convertCFNTagMapToFMSTagSet(request.getDesiredResourceTags()));
    }
}
