package software.amazon.fms.policy.helpers;

import software.amazon.awssdk.services.fms.model.DeletePolicyRequest;
import software.amazon.awssdk.services.fms.model.DeletePolicyResponse;
import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.awssdk.services.fms.model.ListPoliciesRequest;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FmsSampleHelper extends BaseSampleHelper {

    /**
     * Assembles a sample FMS policy with only the required readable parameters.
     * @param includeIdentifiers Should the policy identifiers be included in the sample policy.
     * @return The assembled policy builder.
     */
    private static Policy.Builder sampleRequiredParametersPolicy(boolean includeIdentifiers) {

        // assemble sample security service policy data
        SecurityServicePolicyData sampleSecurityServicePolicyData = SecurityServicePolicyData.builder()
                .managedServiceData(sampleManagedServiceData)
                .type(samplePolicyType)
                .build();
        // assemble a sample policy with only the required parameters
        Policy.Builder builder = Policy.builder()
                .excludeResourceTags(sampleExcludeResourceTags)
                .policyName(samplePolicyName)
                .remediationEnabled(sampleRemediationEnabled)
                .resourceType(sampleResourceTypeListElement)
                .securityServicePolicyData(sampleSecurityServicePolicyData);

        // optionally include the policy id
        if (includeIdentifiers) {
            builder.policyId(samplePolicyId)
                    .policyUpdateToken(samplePolicyUpdateToken);
        }

        return builder;
    }

    /**
     * Assembles a sample FMS policy with all possible readable parameters.
     * @param includeIdentifiers Should the policy identifiers be included in the sample policy.
     * @return The assembled policy builder.
     */
    private static Policy.Builder sampleAllParametersPolicy(boolean includeIdentifiers) {

        // assemble a sample account map
        List<String> sampleAccountMap = new ArrayList<>();
        sampleAccountMap.add(sampleAccountId);

        // assemble sample resource tags
        ResourceTag[] sampleResourceTags = {
                ResourceTag.builder().key(sampleTagKey).value(sampleTagValue).build()
        };

        // assemble a sample resource type list
        Collection<String> sampleResourceTypeList = new ArrayList<>();
        sampleResourceTypeList.add(sampleResourceTypeListElement);

        // assemble sample policy with all possible parameters
        return sampleRequiredParametersPolicy(includeIdentifiers)
                .excludeMap(FmsHelper.mapAccounts(sampleAccountMap))
                .includeMap(FmsHelper.mapAccounts(sampleAccountMap))
                .resourceTags(sampleResourceTags)
                .resourceType(sampleResourceType)
                .resourceTypeList(sampleResourceTypeList);
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

    /**
     * Assembles a sample PutPolicy request with only the required readable parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param includeTag1 Should unique tag 1 be included.
     * @param includeTag2 Should unique tag 2 be included.
     * @return The assembled request.
     */
    public static PutPolicyRequest samplePutPolicyRequiredParametersRequest(boolean includeIdentifiers,
                                                                            boolean includeTag1,
                                                                            boolean includeTag2) {

        // determine tags to list
        List<Tag> addTags = new ArrayList<>();
        if (includeTag1) {
            addTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "1")).value(sampleTagValue).build());
        }
        if (includeTag2) {
            addTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "2")).value(sampleTagValue).build());
        }

        return PutPolicyRequest.builder()
                .tagList(addTags)
                .policy(sampleRequiredParametersPolicy(includeIdentifiers).build())
                .build();
    }

    /**
     * Assembles a sample PutPolicy request with all possible readable parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @return The assembled request.
     */
    public static PutPolicyRequest samplePutPolicyAllParametersRequest(boolean includeIdentifiers) {

        return PutPolicyRequest.builder()
                .policy(sampleAllParametersPolicy(includeIdentifiers).build())
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
     * Assembles a sample ListPolicy response.
     * @return The assembled response.
     */
    public static ListPoliciesResponse sampleListPoliciesResponse() {

        PolicySummary policySummary = PolicySummary.builder()
                .policyArn(samplePolicyArn)
                .policyId(samplePolicyId)
                .policyName(samplePolicyName)
                .resourceType(sampleResourceType)
                .securityServiceType(samplePolicyType)
                .remediationEnabled(sampleRemediationEnabled)
                .build();

        List<PolicySummary> policyList = new ArrayList<>();
        policyList.add(policySummary);
        policyList.add(policySummary);

        return ListPoliciesResponse.builder()
                .policyList(policyList)
                .build();
    }

    /**
     * Assembles a sample ListPolicy request.
     * @return The assembled request.
     */
    public static ListPoliciesRequest sampleListPoliciesRequest() {

        return ListPoliciesRequest.builder().build();
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
                .deleteAllPolicyResources(true)
                .build();
    }

    /**
     * Assembles a sample ListTagsForResource response.
     * @param includeTag1 Should unique tag 1 be added.
     * @param includeTag2 Should unique tag 2 be added.
     * @return The assembled response.
     */
    public static ListTagsForResourceResponse sampleListTagsForResourceResponse(boolean includeTag1, boolean includeTag2) {

        // determine tags to list
        List<Tag> listTags = new ArrayList<>();
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
    public static TagResourceRequest sampleTagResourceRequest(boolean includeTag1, boolean includeTag2) {

        // determine tags to add
        List<Tag> addTags = new ArrayList<>();
        if (includeTag1) {
            addTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "1")).value(sampleTagValue).build());
        }
        if (includeTag2) {
            addTags.add(Tag.builder().key(String.format("%s%s", sampleTagKey, "2")).value(sampleTagValue).build());
        }

        return TagResourceRequest.builder()
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
    public static UntagResourceRequest sampleUntagResourceRequest(boolean includeTag1, boolean includeTag2) {

        // determines tags to remove
        List<String> deleteKeys = new ArrayList<>();
        if (includeTag1) {
            deleteKeys.add(String.format("%s%s", sampleTagKey, "1"));
        }
        if (includeTag2) {
            deleteKeys.add(String.format("%s%s", sampleTagKey, "2"));
        }

        return UntagResourceRequest.builder()
                .tagKeys(deleteKeys)
                .build();
    }
}
