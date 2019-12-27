package software.amazon.fms.policy.helpers;

import software.amazon.awssdk.services.fms.model.CustomerPolicyScopeIdType;
import software.amazon.awssdk.services.fms.model.Policy;
import software.amazon.awssdk.services.fms.model.ResourceTag;
import software.amazon.awssdk.services.fms.model.SecurityServicePolicyData;
import software.amazon.fms.policy.ResourceModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // assemble the security service policy data
        SecurityServicePolicyData.Builder securityServicePolicyData = SecurityServicePolicyData.builder()
                .type(resourceModel.getSecurityServicePolicyData().getType());

        // add the managed service data if it exists
        if (resourceModel.getSecurityServicePolicyData().getManagedServiceData() != null) {
                securityServicePolicyData.managedServiceData(resourceModel.getSecurityServicePolicyData().getManagedServiceData());
        }

        // assemble the policy with the required parameters
        Policy.Builder builder = Policy.builder()
                .policyName(resourceModel.getPolicyName())
                .remediationEnabled(resourceModel.getRemediationEnabled())
                .resourceType(resourceModel.getResourceType())
                .securityServicePolicyData(securityServicePolicyData.build());

        // check each optional parameter and add it if it exists
        if (resourceModel.getExcludeMap() != null) {
            builder.excludeMap(mapAccounts(resourceModel.getExcludeMap().getACCOUNT()));
        }
        if (resourceModel.getExcludeResourceTags() != null) {
            builder.excludeResourceTags(resourceModel.getExcludeResourceTags());
        }
        if (resourceModel.getIncludeMap() != null) {
            builder.includeMap(mapAccounts(resourceModel.getIncludeMap().getACCOUNT()));
        }
        if (resourceModel.getPolicyId() != null) {
            builder.policyId(resourceModel.getPolicyId());
        }
        if (resourceModel.getResourceTags() != null) {
            Collection<ResourceTag> resourceTags = new ArrayList<>();
            resourceModel.getResourceTags().forEach(rt -> resourceTags.add(
                    software.amazon.awssdk.services.fms.model.ResourceTag.builder()
                            .key(rt.getKey())
                            .value(rt.getValue())
                            .build()
            ));
            builder.resourceTags(resourceTags);
        }
        if (resourceModel.getResourceTypeList() != null) {
            Collection<String> resourceTypeList = new ArrayList<>(resourceModel.getResourceTypeList());
            builder.resourceTypeList(resourceTypeList);
        }

        // return the policy builder
        return builder;
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
}
