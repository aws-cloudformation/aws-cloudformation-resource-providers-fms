package software.amazon.fms.policy.helpers;

import software.amazon.awssdk.services.fms.model.CustomerPolicyScopeIdType;
import software.amazon.awssdk.services.fms.model.Policy;
import software.amazon.awssdk.services.fms.model.ResourceTag;
import software.amazon.awssdk.services.fms.model.SecurityServicePolicyData;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.fms.policy.ResourceModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FmsHelper {

    /**
     * Helper method to assign accounts to the ACCOUNT key within a map.
     * @param accounts Account list to map.
     * @return Map with one key, ACCOUNT, containing a list of account IDs.
     */
    static Map<CustomerPolicyScopeIdType, ? extends List<String>> mapAccounts(List<String> accounts) {

        return new HashMap<CustomerPolicyScopeIdType, List<String>>() {{
            put(CustomerPolicyScopeIdType.fromValue("ACCOUNT"), new ArrayList<>(accounts));
        }};
    }

    /**
     * Logic for converting a CFN resource model (from the resource provider) to an FMS policy (from the FMS SDK).
     * @param resourceModel CFN resource model that was converted from.
     * @return FMS policy builder that was converted to.
     */
    private static Policy.Builder convertCFNResourceModelToBuilder(ResourceModel resourceModel) {

        // get the policy from the resource model
        final software.amazon.fms.policy.Policy policy = resourceModel.getPolicy();

        // assemble the security service policy data
        final SecurityServicePolicyData.Builder securityServicePolicyData = SecurityServicePolicyData.builder()
                .type(policy.getSecurityServicePolicyData().getType());

        // add the managed service data if it exists
        if (policy.getSecurityServicePolicyData().getManagedServiceData() != null) {
                securityServicePolicyData.managedServiceData(policy.getSecurityServicePolicyData().getManagedServiceData());
        }

        // assemble the policy with the required parameters
        final Policy.Builder policyBuilder = Policy.builder()
                .policyName(policy.getPolicyName())
                .remediationEnabled(policy.getRemediationEnabled())
                .resourceType(policy.getResourceType())
                .securityServicePolicyData(securityServicePolicyData.build());

        // check each optional parameter and add it if it exists
        if (policy.getExcludeMap() != null) {
            policyBuilder.excludeMap(mapAccounts(policy.getExcludeMap().getACCOUNT()));
        }
        if (policy.getExcludeResourceTags() != null) {
            policyBuilder.excludeResourceTags(policy.getExcludeResourceTags());
        }
        if (policy.getIncludeMap() != null) {
            policyBuilder.includeMap(mapAccounts(policy.getIncludeMap().getACCOUNT()));
        }
        if (policy.getPolicyId() != null) {
            policyBuilder.policyId(policy.getPolicyId());
        }
        if (policy.getResourceTags() != null) {
            final Collection<ResourceTag> resourceTags = new ArrayList<>();
            policy.getResourceTags().forEach(rt -> resourceTags.add(
                    software.amazon.awssdk.services.fms.model.ResourceTag.builder()
                            .key(rt.getKey())
                            .value(rt.getValue())
                            .build()
            ));
            policyBuilder.resourceTags(resourceTags);
        }
        if (policy.getResourceTypeList() != null) {
            final Collection<String> resourceTypeList = new ArrayList<>(policy.getResourceTypeList());
            policyBuilder.resourceTypeList(resourceTypeList);
        }

        // return the policy builder
        return policyBuilder;
    }

    /**
     * Convert a CFN resource model (from the resource provider) to an FMS policy (from the FMS SDK).
     * @param resourceModel CFN resource model that was converted from.
     * @return FMS policy that was converted to.
     */
    public static Policy convertCFNResourceModelToFMSPolicy(ResourceModel resourceModel) {

        return convertCFNResourceModelToBuilder(resourceModel).build();
    }

    /**
     * Convert a CFN resource model (from the resource provider) to an FMS policy (from the FMS SDK) and inject a
     * policyUpdateToken.
     * @param resourceModel CFN resource model that was converted from.
     * @param policyUpdateToken The Policy update token to inject into the FMS policy
     * @return FMS policy that was converted to with the policyUpdateToken.
     */
    public static Policy convertCFNResourceModelToFMSPolicy(
            ResourceModel resourceModel,
            String policyUpdateToken) {

        return convertCFNResourceModelToBuilder(resourceModel).policyUpdateToken(policyUpdateToken).build();
    }

    /**
     * Convert a CFN tag map to an FMS tag list.
     * @param cfnTags Tags from the CFN resource provider request.
     * @return A list of FMS tag objects.
     */
    public static List<Tag> convertCFNTagMapToFMSTagSet(Map<String, String> cfnTags) {

        // construct a new list of FMS tags
        final List<Tag> tags = new ArrayList<>();
        if (cfnTags != null) {
            cfnTags.forEach((k, v) -> tags.add(Tag.builder().key(k).value(v).build()));
        }
        return tags;
    }

    /**
     * Determine the tags that need to be removed from a policy.
     * @param existingTagList The tags that currently exist on the policy.
     * @param desiredTagList The tags that should exist on the policy.
     * @return A list of tag keys to remove from the policy.
     */
    public static List<String> tagsToRemove(List<Tag> existingTagList, Map<String, String> desiredTagList) {

        // format existing and new tags
        final List<Tag> desiredTagListFms = convertCFNTagMapToFMSTagSet(desiredTagList);

        // determine tags to remove
        return existingTagList.stream()
                .filter(tag -> !desiredTagListFms.contains(tag))
                .map(Tag::key)
                .collect(Collectors.toList());
    }

    /**
     * Determine the tags that need to be added to a policy.
     * @param existingTagList The tags that currently exist on the policy.
     * @param desiredTagList The tags that should exist on the policy.
     * @return A list of tags to add to the policy.
     */
    public static List<Tag> tagsToAdd(List<Tag> existingTagList, Map<String, String> desiredTagList) {

        // format existing and new tags
        final List<Tag> desiredTagListFms = convertCFNTagMapToFMSTagSet(desiredTagList);

        // determine tags to add
        return desiredTagListFms.stream()
                .filter(tag -> !existingTagList.contains(tag))
                .collect(Collectors.toList());
    }
}
