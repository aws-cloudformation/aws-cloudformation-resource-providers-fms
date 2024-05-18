package software.amazon.fms.resourceset;

import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.GetResourceSetRequest;
import software.amazon.awssdk.services.fms.model.GetResourceSetResponse;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.fms.model.PutResourceSetRequest;
import software.amazon.awssdk.services.fms.model.PutResourceSetResponse;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.awssdk.services.fms.model.TagResourceRequest;
import software.amazon.awssdk.services.fms.model.TagResourceResponse;
import software.amazon.awssdk.services.fms.model.UntagResourceRequest;
import software.amazon.awssdk.services.fms.model.UntagResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.resourceset.helpers.AssociationHelper;
import software.amazon.fms.resourceset.helpers.CfnHelper;
import software.amazon.fms.resourceset.helpers.FmsHelper;

import java.util.List;

public class UpdateHandler extends ResourceSetHandler<PutResourceSetResponse> {

    UpdateHandler() {
        super();
    }

    UpdateHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected PutResourceSetResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger
    ) {

        // make a read request to retrieve an up-to-date ResourceSetUpdateToken
        logger.log("Retrieving existing ResourceSet");
        if (StringUtils.isBlank(request.getDesiredResourceState().getId())) {
            throw ResourceNotFoundException.builder()
                    .message("Firewall manager ResourceSet with the provided reference ID does not exist").build();
        }
        final GetResourceSetRequest getResourceSetRequest = GetResourceSetRequest.builder()
                .identifier(request.getDesiredResourceState().getId())
                .build();
        final GetResourceSetResponse getResourceSetResponse = proxy.injectCredentialsAndInvokeV2(
                getResourceSetRequest,
                client::getResourceSet);
        logger.log("ResourceSet retrieved successfully");
        logRequest(getResourceSetResponse, logger);

        // make the update request
        logger.log("Updating existing ResourceSet");
        final PutResourceSetRequest putResourceSetRequest = PutResourceSetRequest.builder()
                .resourceSet(FmsHelper.convertCFNResourceModelToFMSResourceSet(
                        request.getDesiredResourceState(),
                        getResourceSetResponse.resourceSet().updateToken()))
                .build();
        final PutResourceSetResponse putResourceSetResponse = proxy.injectCredentialsAndInvokeV2(
                putResourceSetRequest,
                client::putResourceSet);
        logger.log("ResourceSet updated successfully");
        logRequest(putResourceSetResponse, logger);

        // make a list request to get the current tags on the ResourceSet
        logger.log("Retrieving ResourceSet tags");
        final ListTagsForResourceRequest listTagsForResourceRequest = ListTagsForResourceRequest.builder()
                .resourceArn(getResourceSetResponse.resourceSetArn())
                .build();
        final ListTagsForResourceResponse listTagsForResourceResponse = proxy.injectCredentialsAndInvokeV2(
                listTagsForResourceRequest,
                client::listTagsForResource);
        logger.log("ResourceSet tags retrieved successfully");
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
                    .resourceArn(getResourceSetResponse.resourceSetArn())
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
                    .resourceArn(getResourceSetResponse.resourceSetArn())
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

        AssociationHelper.updateResourceAssociations(
                getResourceSetResponse.resourceSet().id(),
                request.getDesiredResourceState().getResources(),
                client,
                proxy,
                logger
        );

        // return the status of the ResourceSet update
        return putResourceSetResponse;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final PutResourceSetResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy
    ) {
        return ProgressEvent.defaultSuccessHandler(constructSuccessResourceModel(response, request, proxy));
    }

    private ResourceModel constructSuccessResourceModel(
            final PutResourceSetResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy
    ) {

        // convert the update request response to a resource model
        return CfnHelper.convertResourceSetToCFNResourceModel(
                response.resourceSet(),
                request.getDesiredResourceState().getResources(),
                FmsHelper.convertCFNTagMapToFMSTagSet(request.getDesiredResourceTags()));
    }
}
