package software.amazon.fms.resourceset.helpers;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.api.Fail;
import software.amazon.awssdk.services.fms.model.BatchAssociateResourceRequest;
import software.amazon.awssdk.services.fms.model.BatchAssociateResourceResponse;
import software.amazon.awssdk.services.fms.model.BatchDisassociateResourceRequest;
import software.amazon.awssdk.services.fms.model.BatchDisassociateResourceResponse;
import software.amazon.awssdk.services.fms.model.DeleteResourceSetRequest;
import software.amazon.awssdk.services.fms.model.DeleteResourceSetResponse;
import software.amazon.awssdk.services.fms.model.FailedItem;
import software.amazon.awssdk.services.fms.model.GetResourceSetResponse;
import software.amazon.awssdk.services.fms.model.GetResourceSetRequest;
import software.amazon.awssdk.services.fms.model.ListPoliciesResponse;
import software.amazon.awssdk.services.fms.model.ListResourceSetResourcesRequest;
import software.amazon.awssdk.services.fms.model.ListResourceSetResourcesResponse;
import software.amazon.awssdk.services.fms.model.ListResourceSetsResponse;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.fms.model.PutResourceSetRequest;
import software.amazon.awssdk.services.fms.model.PutResourceSetResponse;
import software.amazon.awssdk.services.fms.model.ResourceSetSummary;
import software.amazon.awssdk.services.fms.model.Resource;
import software.amazon.awssdk.services.fms.model.ResourceSet;
import software.amazon.awssdk.services.fms.model.ResourceTag;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.awssdk.services.fms.model.TagResourceRequest;
import software.amazon.awssdk.services.fms.model.TagResourceResponse;
import software.amazon.awssdk.services.fms.model.UntagResourceRequest;
import software.amazon.awssdk.services.fms.model.UntagResourceResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FmsSampleHelper extends BaseSampleHelper {

    /**
     * Assembles a sample FMS resourceSet with only the required readable parameters.
     * @param includeIdentifiers Should the resourceSet identifiers be included in the sample resourceSet.
     * @return The assembled resourceSet builder.
     */
    private static ResourceSet.Builder sampleRequiredParametersResourceSet(final boolean includeIdentifiers) {
        // assemble a sample resourceSet with only the required parameters
        final ResourceSet.Builder resourceSetBuilder = ResourceSet.builder()
                .name(sampleResourceSetName)
                .resourceTypeList(sampleResourceTypeListElement);

        // optionally include the resourceSet id
        if (includeIdentifiers) {
            resourceSetBuilder.id(sampleResourceSetId).updateToken(sampleResourceSetUpdateToken);
        }

        return resourceSetBuilder;
    }

    /**
     * Assembles a sample DeleteResourceSet response.
     * @return The assembled response.
     */
    public static DeleteResourceSetResponse sampleDeleteResourceSetResponse() {

        return DeleteResourceSetResponse.builder().build();
    }

    /**
     * Assembles a sample DeleteResourceSet request.
     * @return The assembled request.
     */
    public static DeleteResourceSetRequest sampleDeleteResourceSetRequest() {

        return DeleteResourceSetRequest.builder()
                .identifier(sampleResourceSetId)
                .build();
    }

    /**
     * Assembles a sample GetResourceSet response with only the required readable parameters.
     * @return The assembled response.
     */
    public static GetResourceSetResponse sampleGetResourceSetRequiredParametersResponse() {

        return GetResourceSetResponse.builder()
                .resourceSet(sampleRequiredParametersResourceSet(true).build())
                .resourceSetArn(sampleResourceSetArn)
                .build();
    }

    /**
     * Assembles a sample BatchAssociateResource response.
     * @param failed Should failed item be added.
     * @return The assembled response.
     */
    public static BatchAssociateResourceResponse sampleBatchAssociateResourceResponse(
            final boolean failed) {
        BatchAssociateResourceResponse response =
                BatchAssociateResourceResponse.builder().resourceSetIdentifier(sampleResourceSetId).build();

        // determine tags to list
        if (failed) {
            FailedItem failedItem = FailedItem.builder().uri(sampleResourceUri).reason(sampleFailedReason).build();
            response.failedItems().add(failedItem);
        }

        return response;
    }

    /**
     * Assembles a sample BatchDisassociateResource response.
     * @param failed Should failed item be added.
     * @return The assembled response.
     */
    public static BatchDisassociateResourceResponse sampleBatchDisassociateResourceResponse(
            final boolean failed) {
        BatchDisassociateResourceResponse response =
                BatchDisassociateResourceResponse.builder().resourceSetIdentifier(sampleResourceSetId).build();

        // determine tags to list
        if (failed) {
            FailedItem failedItem = FailedItem.builder().uri(sampleResourceUri).reason(sampleFailedReason).build();
            response.failedItems().add(failedItem);
        }

        return response;
    }

    /**
     * Assembles a sample ListTagsForResource response.
     * @param includeTag1 Should unique tag 1 be added.
     * @param includeTag2 Should unique tag 2 be added.
     * @return The assembled response.
     */
    public static ListTagsForResourceResponse sampleListTagsForResourceResponse(
            final boolean includeTag1,
            final boolean includeTag2) {

        // determine tags to list
        final List<Tag> listTags = new ArrayList<>();
        if (includeTag1) {
            listTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "1")).value(sampleTagValue).build());
        }
        if (includeTag2) {
            listTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "2")).value(sampleTagValue).build());
        }

        return ListTagsForResourceResponse.builder().tagList(listTags).build();
    }

    /***
     * Assembles a sample tag map in the CloudFormation resource state.
     * @param includeTag1 Should unique tag 1 be added.
     * @param includeTag2 Should unique tag 2 be added.
     * @return The assembled sample tag map.
     */

    public static Map<String, String> generateSampleResourceTags(final boolean includeTag1, final boolean includeTag2) {
        // determine tags to include in the map
        final Map<String, String> tags = new HashMap<>();
        if (includeTag1) {
            tags.put(String.format("%s%s", sampleTagKey, "1"), sampleTagValue);
        }
        if (includeTag2) {
            tags.put(String.format("%s%s", sampleTagKey, "2"), sampleTagValue);
        }
        return tags;
    }

    /**
     * Assembles a sample ListResourceSetResources response.
     * @return The assembled response.
     */
    public static ListResourceSetResourcesResponse sampleListResourceSetResourcesResponse() {
        // create resource
        Resource resource = Resource.builder().uri(sampleResourceUri).accountId(sampleAccountId).build();

        return ListResourceSetResourcesResponse.builder().items(resource).nextToken(null).build();
    }

    /**
     * Assembles a sample ListResourceSetResources response with empty resource.
     * @return The assembled response.
     */
    public static ListResourceSetResourcesResponse sampleListResourceSetResourcesResponseMultipleResources() {
        // create resources
        Resource resource1 = Resource.builder().uri(sampleResourceUri).accountId(sampleAccountId).build();
        Resource resource2 = Resource.builder().uri(sampleResourceUri2).accountId(sampleAccountId).build();

        return ListResourceSetResourcesResponse.builder().items(resource1, resource2).nextToken(null).build();
    }

    /**
     * Assembles a sample ListResourceSetResources response with multiple resource.
     * @return The assembled response.
     */
    public static ListResourceSetResourcesResponse sampleListResourceSetResourcesResponseEmptyResource() {
        return ListResourceSetResourcesResponse.builder().nextToken(null).build();
    }

    /**
     * Assembles a sample GetResourceSet request with all possible readable parameters.
     * @return The assembled request.
     */
    public static GetResourceSetRequest sampleGetResourceSetRequest() {

        return GetResourceSetRequest.builder()
                .identifier(sampleResourceSetId)
                .build();
    }

    /**
     * Assembles a sample ListResourceSetResources request with all possible readable parameters.
     * @return The assembled request.
     */
    public static ListResourceSetResourcesRequest sampleListResourceSetResourcesRequest() {

        return ListResourceSetResourcesRequest.builder()
                .identifier(sampleResourceSetId)
                .build();
    }

    /**
     * Assembles a sample BatchAssociateResource request with all possible readable parameters.
     * @return The assembled request.
     */
    public static BatchAssociateResourceRequest sampleBatchAssociateResourceRequest() {

        return BatchAssociateResourceRequest.builder()
                .resourceSetIdentifier(sampleResourceSetId)
                .items(sampleResourceUri)
                .build();
    }

    /**
     * Assembles a sample BatchDisassociateResource request with all possible readable parameters.
     * @return The assembled request.
     */
    public static BatchDisassociateResourceRequest sampleBatchDisassociateResourceRequest() {

        return BatchDisassociateResourceRequest.builder()
                .resourceSetIdentifier(sampleResourceSetId)
                .items(sampleResourceUri2)
                .build();
    }

    /**
     * Assembles a sample ListTagsForResource request.
     * @return The assembled response.
     */
    public static ListTagsForResourceRequest sampleListTagsForResourceRequest() {

        return ListTagsForResourceRequest.builder()
                .resourceArn(sampleResourceSetArn)
                .build();
    }

    /**
     * Assembles a sample GetResourceSet response with all possible readable parameters.
     * @return The assembled response.
     */
    public static GetResourceSetResponse sampleGetResourceSetAllParametersResponse() {

        return GetResourceSetResponse.builder()
                .resourceSet(sampleAllParametersResourceSet(true).build())
                .resourceSetArn(sampleResourceSetArn)
                .build();
    }

    /**
     * Assembles a sample FMS resourceSet with all possible readable parameters.
     * @param includeIdentifiers Should the resourceSet identifiers be included in the sample resourceSet.
     * @return The assembled resourceSet builder.
     */
    private static ResourceSet.Builder sampleAllParametersResourceSet(final boolean includeIdentifiers) {

        // assemble sample resourceSet with all possible parameters
        return sampleRequiredParametersResourceSet(includeIdentifiers)
                .description(sampleResourceSetDescription);
    }

    /**
     * Assembles a sample ListResourceSets response.
     * @return The assembled response.
     */
    public static ListResourceSetsResponse sampleListResourceSets(String nextToken) {
        return ListResourceSetsResponse.builder()
                .resourceSets(Collections.singletonList(sampleResourceSetSummary().build()))
                .nextToken(nextToken)
                .build();
    }

    /**
     * Assembles a sample FMS resourceSet summary.
     * @return The assembled resourceSet summary  builder.
     */
    private static ResourceSetSummary.Builder sampleResourceSetSummary() {
        // assemble a sample resourceSet with only the required parameters

        return ResourceSetSummary.builder()
                .id(sampleResourceSetId)
                .name(sampleResourceSetName)
                .lastUpdateTime(Instant.now());
    }

    /**
     * Assembles a sample PutResourceSet response with only the required readable parameters.
     * @return The assembled response.
     */
    public static PutResourceSetResponse samplePutResourceSetRequiredParametersResponse() {

        return PutResourceSetResponse.builder()
                .resourceSet(sampleRequiredParametersResourceSet(true).build())
                .resourceSetArn(sampleResourceSetArn)
                .build();
    }

    /**
     * Assembles a sample PutResourceSet request with only the required readable parameters.
     * @param includeIdentifiers Should the resourceSet identifiers be included.
     * @param includeTag1 Should unique tag 1 be included.
     * @param includeTag2 Should unique tag 2 be included.
     * @return The assembled request.
     */
    public static PutResourceSetRequest samplePutResourceSetRequiredParametersRequest(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2) {

        // determine tags to list
        final List<Tag> addTags = new ArrayList<>();
        if (includeTag1) {
            addTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "1")).value(sampleTagValue).build());
        }
        if (includeTag2) {
            addTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "2")).value(sampleTagValue).build());
        }

        final PutResourceSetRequest.Builder requestBuilder = PutResourceSetRequest.builder()
                .resourceSet(sampleRequiredParametersResourceSet(includeIdentifiers).build());

        if (!addTags.isEmpty()) {
            requestBuilder.tagList(addTags);
        }
        return requestBuilder.build();
    }

    /**
     * Assembles a sample PutResourceSet response with all possible readable parameters.
     * @return The assembled response.
     */
    public static PutResourceSetResponse samplePutResourceSetAllParametersResponse() {
        return PutResourceSetResponse.builder()
                .resourceSet(sampleAllParametersResourceSet(true).build())
                .resourceSetArn(sampleResourceSetArn)
                .build();
    }

    /**
     * Assembles a sample PutResourceSet request with all possible readable parameters.
     * @param includeIdentifiers Should the resourceSet identifiers be included.
     * @return The assembled request.
     */
    public static PutResourceSetRequest samplePutResourceSetAllParametersRequest(final boolean includeIdentifiers) {

        return PutResourceSetRequest.builder()
                .resourceSet(sampleAllParametersResourceSet(includeIdentifiers).build())
                .build();
    }

    /**
     * Assembles a sample UntagResource response.
     * @return The assembled response.
     */
    public static UntagResourceResponse sampleUntagResourceResponse() {

        return UntagResourceResponse.builder().build();
    }

    /**
     * Assembles a sample TagResource response.
     * @return The assembled response.
     */
    public static TagResourceResponse sampleTagResourceResponse() {

        return TagResourceResponse.builder().build();
    }

    /**
     * Assembles a sample UntagResource request.
     * @param includeTag1 Should unique tag 1 be removed.
     * @param includeTag2 Should unique tag 2 be removed.
     * @return The assembled request.
     */
    public static UntagResourceRequest sampleUntagResourceRequest(
            final boolean includeTag1,
            final boolean includeTag2) {

        // determines tags to remove
        final List<String> deleteKeys = new ArrayList<>();
        if (includeTag1) {
            deleteKeys.add(String.format("%s%s", sampleTagKey, "1"));
        }
        if (includeTag2) {
            deleteKeys.add(String.format("%s%s", sampleTagKey, "2"));
        }

        return UntagResourceRequest.builder()
                .resourceArn(sampleResourceSetArn)
                .tagKeys(deleteKeys)
                .build();
    }

    /**
     * Assembles a sample TagResource request.
     * @param includeTag1 Should unique tag 1 be added.
     * @param includeTag2 Should unique tag 2 be added.
     * @return The assembled request.
     */
    public static TagResourceRequest sampleTagResourceRequest(
            final boolean includeTag1,
            boolean includeTag2) {

        // determine tags to add
        final List<Tag> addTags = new ArrayList<>();
        if (includeTag1) {
            addTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "1")).value(sampleTagValue).build());
        }
        if (includeTag2) {
            addTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "2")).value(sampleTagValue).build());
        }

        return TagResourceRequest.builder()
                .resourceArn(sampleResourceSetArn)
                .tagList(addTags)
                .build();
    }
}
