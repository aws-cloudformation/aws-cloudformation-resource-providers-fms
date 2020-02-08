package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.policy.helpers.CfnHelper;

public class ReadHandler extends PolicyHandler<GetPolicyResponse> {

    @Override
    protected GetPolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger) {

        // make the read request
        final GetPolicyRequest getPolicyRequest = GetPolicyRequest.builder()
                .policyId(request.getDesiredResourceState().getId())
                .build();
        final GetPolicyResponse response = proxy.injectCredentialsAndInvokeV2(
                getPolicyRequest,
                client::getPolicy);
        logRequest(response, logger);
        return response;
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(
            final GetPolicyResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy) {

        // list the tags for the policy and add them to the resource model
        final ListTagsForResourceRequest listTagsForResourceRequest = ListTagsForResourceRequest.builder()
                .resourceArn(response.policyArn())
                .build();
        final ListTagsForResourceResponse listTagsForResourceResponse = proxy.injectCredentialsAndInvokeV2(
                listTagsForResourceRequest,
                client::listTagsForResource);

        // convert the read request response to a resource model
        return CfnHelper.convertFMSPolicyToCFNResourceModel(
                response.policy(),
                response.policyArn(),
                listTagsForResourceResponse.tagList());
    }
}
