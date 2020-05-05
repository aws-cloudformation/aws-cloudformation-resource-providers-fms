package software.amazon.fms.policy.helpers;

import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.fms.model.CustomerPolicyScopeIdType;
import software.amazon.awssdk.services.fms.model.Policy;
import software.amazon.awssdk.services.fms.model.ResourceTag;
import software.amazon.awssdk.services.fms.model.SecurityServicePolicyData;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.fms.policy.IEMap;
import software.amazon.fms.policy.ResourceModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FmsHelper {

    /**
     * Helper method to assign values in an include/exclude map.
     * @param cfnIEMap CFN IEMap to covert,
     * @return The converted include/exclude map.
     */
    static Map<CustomerPolicyScopeIdType, ? extends List<String>> convertCFNIEMapToFMSIEMap(IEMap cfnIEMap) {
        HashMap<CustomerPolicyScopeIdType, List<String>> fmsIEMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(cfnIEMap.getACCOUNT())) {
            fmsIEMap.put(CustomerPolicyScopeIdType.ACCOUNT, new ArrayList<>(cfnIEMap.getACCOUNT()));
        }
        if (CollectionUtils.isNotEmpty(cfnIEMap.getORGUNIT())) {
            fmsIEMap.put(CustomerPolicyScopeIdType.ORG_UNIT, new ArrayList<>(cfnIEMap.getORGUNIT()));
        }
        return fmsIEMap;
    }

    /**
     * Logic for converting a CFN resource model (from the resource provider) to an FMS policy (from the FMS SDK).
     * @param resourceModel CFN resource model that was converted from.
     * @return FMS policy builder that was converted to.
     */
    private static Policy.Builder convertCFNResourceModelToBuilder(ResourceModel resourceModel) {

        // assemble the security service policy data
        final SecurityServicePolicyData.Builder securityServicePolicyData = SecurityServicePolicyData.builder()
                .type(resourceModel.getSecurityServicePolicyData().getType());

        // add the managed service data if it exists
        if (resourceModel.getSecurityServicePolicyData().getManagedServiceData() != null) {
                securityServicePolicyData.managedServiceData(resourceModel.getSecurityServicePolicyData().getManagedServiceData());
        }

        // assemble the policy with the required parameters
        final Policy.Builder policyBuilder = Policy.builder()
                .policyName(resourceModel.getPolicyName())
                .remediationEnabled(resourceModel.getRemediationEnabled())
                .resourceType(resourceModel.getResourceType())
                .securityServicePolicyData(securityServicePolicyData.build());

        // add exclude map if present
        if (resourceModel.getExcludeMap() != null) {
            policyBuilder.excludeMap(convertCFNIEMapToFMSIEMap(resourceModel.getExcludeMap()));
        }

        // add exclude resource tags if present
        if (resourceModel.getExcludeResourceTags() != null) {
            policyBuilder.excludeResourceTags(resourceModel.getExcludeResourceTags());
        }

        // add include map if present
        if (resourceModel.getIncludeMap() != null) {
            policyBuilder.includeMap(convertCFNIEMapToFMSIEMap(resourceModel.getIncludeMap()));
        }

        // add policy id if present
        if (resourceModel.getId() != null) {
            policyBuilder.policyId(resourceModel.getId());
        }

        // add resource tags if present
        if (resourceModel.getResourceTags() != null) {
            final Collection<ResourceTag> resourceTags = new ArrayList<>();
            resourceModel.getResourceTags().forEach(rt -> resourceTags.add(
                    software.amazon.awssdk.services.fms.model.ResourceTag.builder()
                            .key(rt.getKey())
                            .value(rt.getValue())
                            .build()
            ));
            policyBuilder.resourceTags(resourceTags);
        }

        // add resource type list if present
        if (resourceModel.getResourceTypeList() != null) {
            final Collection<String> resourceTypeList = new ArrayList<>(resourceModel.getResourceTypeList());
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
