package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.DeletePolicyRequest;
import software.amazon.awssdk.services.fms.model.PutPolicyRequest;
import software.amazon.awssdk.services.fms.model.PutPolicyResponse;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.policy.helpers.CfnHelper;
import software.amazon.fms.policy.helpers.FmsHelper;

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
        final PutPolicyRequest putPolicyRequest = PutPolicyRequest.builder()
                .policy(FmsHelper.convertCFNResourceModelToFMSPolicy(request.getDesiredResourceState()))
                .tagList(FmsHelper.convertCFNTagMapToFMSTagSet(request.getDesiredResourceTags()))
                .build();
        final PutPolicyResponse response = proxy.injectCredentialsAndInvokeV2(
                putPolicyRequest,
                client::putPolicy);
        logRequest(response, logger);
        return response;
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(
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
