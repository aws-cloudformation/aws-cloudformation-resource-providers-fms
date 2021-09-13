package software.amazon.fms.policy.helpers;

import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.fms.model.DeletePolicyRequest;
import software.amazon.awssdk.services.fms.model.DeletePolicyResponse;
import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.awssdk.services.fms.model.ListPoliciesResponse;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.fms.model.Policy;
import software.amazon.awssdk.services.fms.model.PolicySummary;
import software.amazon.awssdk.services.fms.model.PutPolicyRequest;
import software.amazon.awssdk.services.fms.model.PutPolicyResponse;
import software.amazon.awssdk.services.fms.model.ResourceTag;
import software.amazon.awssdk.services.fms.model.SecurityServicePolicyData;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.awssdk.services.fms.model.TagResourceRequest;
import software.amazon.awssdk.services.fms.model.TagResourceResponse;
import software.amazon.awssdk.services.fms.model.UntagResourceRequest;
import software.amazon.awssdk.services.fms.model.UntagResourceResponse;
import software.amazon.fms.policy.IEMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FmsSampleHelper extends BaseSampleHelper {

    /**
     * Assembles a sample FMS policy with only the required readable parameters.
     * @param includeIdentifiers Should the policy identifiers be included in the sample policy.
     * @return The assembled policy builder.
     */
    private static Policy.Builder sampleRequiredParametersPolicy(final boolean includeIdentifiers) {

        // assemble sample security service policy data
        final SecurityServicePolicyData sampleSecurityServicePolicyData = SecurityServicePolicyData.builder()
                .managedServiceData(sampleManagedServiceData)
                .type(samplePolicyType)
                .build();
        // assemble a sample policy with only the required parameters
        final Policy.Builder policyBuilder = Policy.builder()
                .excludeResourceTags(sampleExcludeResourceTags)
                .policyName(samplePolicyName)
                .remediationEnabled(sampleRemediationEnabled)
                .resourceType(sampleResourceTypeListElement)
                .securityServicePolicyData(sampleSecurityServicePolicyData);

        // optionally include the policy id
        if (includeIdentifiers) {
            policyBuilder.policyId(samplePolicyId).policyUpdateToken(samplePolicyUpdateToken);
        }

        return policyBuilder;
    }

    /**
     * Assembles a sample FMS policy with all possible readable parameters.
     * @param includeIdentifiers Should the policy identifiers be included in the sample policy.
     * @return The assembled policy builder.
     */
    private static Policy.Builder sampleAllParametersPolicy(final boolean includeIdentifiers,
                                                            final List<String> ouList) {

        // assemble a sample include/exclude map
        final List<String> sampleAccountList = new ArrayList<>();
        sampleAccountList.add(sampleAccountId);
        final IEMap sampleIEMap;
        if (CollectionUtils.isNotEmpty(ouList)) {
            sampleIEMap = IEMap.builder()
                    .aCCOUNT(sampleAccountList)
                    .oRGUNIT(ouList)
                    .build();
        } else {
            sampleIEMap = IEMap.builder()
                    .aCCOUNT(sampleAccountList)
                    .build();
        }

        // assemble sample resource tags
        final ResourceTag[] sampleResourceTags = {
                ResourceTag.builder().key(sampleTagKey).value(sampleTagValue).build()
        };

        // assemble a sample resource type list
        final Collection<String> sampleResourceTypeList = new ArrayList<>();
        sampleResourceTypeList.add(sampleResourceTypeListElement);

        // assemble sample policy with all possible parameters
        return sampleRequiredParametersPolicy(includeIdentifiers)
                .excludeMap(FmsHelper.convertCFNIEMapToFMSIEMap(sampleIEMap))
                .includeMap(FmsHelper.convertCFNIEMapToFMSIEMap(sampleIEMap))
                .resourceTags(sampleResourceTags)
                .resourceType(sampleResourceType)
                .resourceTypeList(sampleResourceTypeList)
                .deleteUnusedFMManagedResources(true);
    }

    /**
     * Assembles a sample FMS policy with all possible readable parameters.
     * @param includeIdentifiers Should the policy identifiers be included in the sample policy.
     * @return The assembled policy builder.
     */
    private static Policy.Builder sampleAllParametersPolicy(final boolean includeIdentifiers) {

        return sampleAllParametersPolicy(includeIdentifiers, Collections.emptyList());
    }

    /**
     * Assembles a sample PutPolicy response with only the required readable parameters.
     * @return The assembled response.
     */
    public static PutPolicyResponse samplePutPolicyRequiredParametersResponse() {

        return PutPolicyResponse.builder()
                .policy(sampleRequiredParametersPolicy(true).build())
                .policyArn(samplePolicyArn)
                .build();
    }

    /**
     * Assembles a sample PutPolicy response with all possible readable parameters.
     * @return The assembled response.
     */
    public static PutPolicyResponse samplePutPolicyAllParametersResponse() {

        return PutPolicyResponse.builder()
                .policy(sampleAllParametersPolicy(true).build())
                .policyArn(samplePolicyArn)
                .build();
    }

    public static PutPolicyResponse samplePutPolicyAllParametersResponse(final List<String> ouList) {

        return PutPolicyResponse.builder()
                .policy(sampleAllParametersPolicy(true, ouList).build())
                .policyArn(samplePolicyArn)
                .build();
    }

    /**
     * Assembles a sample PutPolicy request with only the required readable parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param includeTag1 Should unique tag 1 be included.
     * @param includeTag2 Should unique tag 2 be included.
     * @return The assembled request.
     */
    public static PutPolicyRequest samplePutPolicyRequiredParametersRequest(
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


        final PutPolicyRequest.Builder requestBuilder = PutPolicyRequest.builder()
                .policy(sampleRequiredParametersPolicy(includeIdentifiers).build());

        if (!addTags.isEmpty()) {
            requestBuilder.tagList(addTags);
        }
        return requestBuilder.build();
    }

    /**
     * Assembles a sample PutPolicy request with all possible readable parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @return The assembled request.
     */
    public static PutPolicyRequest samplePutPolicyAllParametersRequest(final boolean includeIdentifiers) {

        return PutPolicyRequest.builder()
                .policy(sampleAllParametersPolicy(includeIdentifiers).build())
                .build();
    }

    public static PutPolicyRequest samplePutPolicyAllParametersRequest(final boolean includeIdentifiers,
                                                                       final List<String> ouList) {

        return PutPolicyRequest.builder()
                .policy(sampleAllParametersPolicy(includeIdentifiers, ouList).build())
                .build();
    }

    /**
     * Assembles a sample GetPolicy response with only the required readable parameters.
     * @return The assembled response.
     */
    public static GetPolicyResponse sampleGetPolicyRequiredParametersResponse() {

        return GetPolicyResponse.builder()
                .policy(sampleRequiredParametersPolicy(true).build())
                .policyArn(samplePolicyArn)
                .build();
    }

    /**
     * Assembles a sample GetPolicy response with all possible readable parameters.
     * @return The assembled response.
     */
    public static GetPolicyResponse sampleGetPolicyAllParametersResponse() {

        return GetPolicyResponse.builder()
                .policy(sampleAllParametersPolicy(true).build())
                .policyArn(samplePolicyArn)
                .build();
    }

    public static GetPolicyResponse sampleGetPolicyAllParametersResponse(final List<String> ouList) {

        return GetPolicyResponse.builder()
                .policy(sampleAllParametersPolicy(true, ouList).build())
                .policyArn(samplePolicyArn)
                .build();
    }

    /**
     * Assembles a sample GetPolicy request with all possible readable parameters.
     * @return The assembled request.
     */
    public static GetPolicyRequest sampleGetPolicyRequest() {

        return GetPolicyRequest.builder()
                .policyId(samplePolicyId)
                .build();
    }

    /**
     * Assembles a sample DeletePolicy response.
     * @return The assembled response.
     */
    public static DeletePolicyResponse sampleDeletePolicyResponse() {

        return DeletePolicyResponse.builder().build();
    }

    /**
     * Assembles a sample DeletePolicy request.
     * @return The assembled request.
     */
    public static DeletePolicyRequest sampleDeletePolicyRequest() {

        return DeletePolicyRequest.builder()
                .policyId(samplePolicyId)
                .build();
    }

    /**
     * Assembles a sample DeletePolicy request with the deleteAllPolicyResources option.
     * @param deleteAllPolicyResources Should all the policy resources be deleted.
     * @return The assembled request.
     */
    public static DeletePolicyRequest sampleDeletePolicyRequest(final boolean deleteAllPolicyResources) {

        return DeletePolicyRequest.builder()
                .policyId(samplePolicyId)
                .deleteAllPolicyResources(deleteAllPolicyResources)
                .build();
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

    /**
     * Assembles a sample ListTagsForResource request.
     * @return The assembled response.
     */
    public static ListTagsForResourceRequest sampleListTagsForResourceRequest() {

        return ListTagsForResourceRequest.builder()
                .resourceArn(samplePolicyArn)
                .build();
    }

    /**
     * Assembles a sample TagResource response.
     * @return The assembled response.
     */
    public static TagResourceResponse sampleTagResourceResponse() {

        return TagResourceResponse.builder().build();
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
                .resourceArn(samplePolicyArn)
                .tagList(addTags)
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
                .resourceArn(samplePolicyArn)
                .tagKeys(deleteKeys)
                .build();
    }

    /**
     * Assembles a sample FMS policy summary.
     * @return The assembled policy summary  builder.
     */
    private static PolicySummary.Builder sampleRequiredParametersPolicySummary() {
        // assemble a sample policy with only the required parameters

        return PolicySummary.builder()
                .policyName(samplePolicyName)
                .remediationEnabled(sampleRemediationEnabled)
                .resourceType(sampleResourceTypeListElement)
                .securityServiceType(samplePolicyType)
                .policyId(samplePolicyId)
                .policyArn(samplePolicyArn);
    }

    /**
     * Assembles a sample GetPolicy response with only the required readable parameters.
     * @return The assembled response.
     */
    public static ListPoliciesResponse sampleListPolicies(String nextToken) {

        return ListPoliciesResponse.builder()
                .policyList(Collections.singletonList(sampleRequiredParametersPolicySummary().build()))
                .nextToken(nextToken)
                .build();
    }
}
