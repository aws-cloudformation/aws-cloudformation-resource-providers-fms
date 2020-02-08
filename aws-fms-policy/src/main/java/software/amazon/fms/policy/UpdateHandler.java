package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.fms.model.PutPolicyRequest;
import software.amazon.awssdk.services.fms.model.PutPolicyResponse;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.awssdk.services.fms.model.TagResourceRequest;
import software.amazon.awssdk.services.fms.model.TagResourceResponse;
import software.amazon.awssdk.services.fms.model.UntagResourceRequest;
import software.amazon.awssdk.services.fms.model.UntagResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.policy.helpers.CfnHelper;
import software.amazon.fms.policy.helpers.FmsHelper;

import java.util.List;

public class UpdateHandler extends PolicyHandler<PutPolicyResponse> {

    @Override
    protected PutPolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger) {

        // make a read request to retrieve an up-to-date PolicyUpdateToken
        logger.log("Retrieving existing policy");
        final GetPolicyRequest getPolicyRequest = GetPolicyRequest.builder()
                .policyId(request.getDesiredResourceState().getId())
                .build();
        final GetPolicyResponse getPolicyResponse = proxy.injectCredentialsAndInvokeV2(
                getPolicyRequest,
                client::getPolicy);
        logger.log("Policy retrieved successfully");
        logRequest(getPolicyResponse, logger);

        // make the update request
        logger.log("Updating existing policy");
        final PutPolicyRequest putPolicyRequest = PutPolicyRequest.builder()
                .policy(FmsHelper.convertCFNResourceModelToFMSPolicy(
                        request.getDesiredResourceState(),
                        getPolicyResponse.policy().policyUpdateToken()))
                .build();
        final PutPolicyResponse putPolicyResponse = proxy.injectCredentialsAndInvokeV2(
                putPolicyRequest,
                client::putPolicy);
        logger.log("Policy updated successfully");
        logRequest(putPolicyResponse, logger);

        // make a list request to get the current tags on the policy
        logger.log("Retrieving policy tags");
        final ListTagsForResourceRequest listTagsForResourceRequest = ListTagsForResourceRequest.builder()
                .resourceArn(getPolicyResponse.policyArn())
                .build();
        final ListTagsForResourceResponse listTagsForResourceResponse = proxy.injectCredentialsAndInvokeV2(
                listTagsForResourceRequest,
                client::listTagsForResource);
        logger.log("Policy tags retrieved successfully");
        logRequest(listTagsForResourceResponse, logger);

        // determine tags to remove and add
        final List<String> removeTags = FmsHelper.tagsToRemove(
                listTagsForResourceResponse.tagList(),
                request.getDesiredResourceTags());
        final List<Tag> addTags = FmsHelper.tagsToAdd(
                listTagsForResourceResponse.tagList(),
                request.getDesiredResourceTags());

        // make an untag request
        if (!removeTags.isEmpty()) {
            logger.log(String.format("Removing %d tag/s", removeTags.size()));
            final UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                    .resourceArn(getPolicyResponse.policyArn())
                    .tagKeys(removeTags)
                    .build();
            final UntagResourceResponse untagResourceResponse = proxy.injectCredentialsAndInvokeV2(
                    untagResourceRequest,
                    client::untagResource);
            logger.log("Tags removed successfully");
            logRequest(untagResourceResponse, logger);
        } else {
            logger.log("No tags to remove");
        }

        // make a tag request
        if (!addTags.isEmpty()) {
            logger.log(String.format("Adding %d tag/s", addTags.size()));
            final TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                    .resourceArn(getPolicyResponse.policyArn())
                    .tagList(addTags)
                    .build();
            final TagResourceResponse tagResourceResponse = proxy.injectCredentialsAndInvokeV2(
                    tagResourceRequest,
                    client::tagResource);
            logger.log("Tags added successfully");
            logRequest(tagResourceResponse, logger);
        } else {
            logger.log("No tags to add");
        }

        // return the status of the policy update
        return putPolicyResponse;
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(
            final PutPolicyResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy) {

        // convert the update request response to a resource model
        return CfnHelper.convertFMSPolicyToCFNResourceModel(
                response.policy(),
                response.policyArn(),
                FmsHelper.convertCFNTagMapToFMSTagSet(request.getDesiredResourceTags()));
    }
}
