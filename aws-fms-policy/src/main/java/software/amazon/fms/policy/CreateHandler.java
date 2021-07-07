package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.DeletePolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.awssdk.services.fms.model.PutPolicyRequest;
import software.amazon.awssdk.services.fms.model.PutPolicyResponse;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.policy.helpers.CfnHelper;
import software.amazon.fms.policy.helpers.FmsHelper;

import java.util.List;

public class CreateHandler extends PolicyHandler<PutPolicyResponse> {

    CreateHandler() {
        super();
    }

    CreateHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected PutPolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger) {

        // make the create request
        final PutPolicyRequest.Builder putPolicyRequestBuilder = PutPolicyRequest.builder()
                .policy(FmsHelper.convertCFNResourceModelToFMSPolicy(request.getDesiredResourceState()));
        final List<Tag> tags = FmsHelper.convertCFNTagMapToFMSTagSet(request.getDesiredResourceTags());
        if (!tags.isEmpty()) {
            putPolicyRequestBuilder.tagList(tags);
        }
        final PutPolicyResponse response = proxy.injectCredentialsAndInvokeV2(
                putPolicyRequestBuilder.build(),
                client::putPolicy);
        logRequest(response, logger);
        return response;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final PutPolicyResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy) {
        return ProgressEvent.defaultSuccessHandler(constructSuccessResourceModel(response, request, proxy));
    }

    private ResourceModel constructSuccessResourceModel(
            final PutPolicyResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy) {

        try {
            // convert the create request response to a resource model and add the tags in
            return CfnHelper.convertFMSPolicyToCFNResourceModel(
                    response.policy(),
                    response.policyArn(),
                    FmsHelper.convertCFNTagMapToFMSTagSet(request.getDesiredResourceTags()));
        } catch (Exception e) {
            // if any code fails, delete the policy since CloudFormation is unaware of it
            DeletePolicyRequest deletePolicyRequest = DeletePolicyRequest.builder()
                    .policyId(response.policy().policyId())
                    .build();
            proxy.injectCredentialsAndInvokeV2(deletePolicyRequest, client::deletePolicy);

            // raise an internal exception so CloudFormation knows policy creation failed
            throw new CfnInternalFailureException(e);
        }
    }
}
