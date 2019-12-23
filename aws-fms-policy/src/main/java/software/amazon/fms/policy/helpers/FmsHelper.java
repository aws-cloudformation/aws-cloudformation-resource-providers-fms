package software.amazon.fms.policy.helpers;

import software.amazon.awssdk.services.fms.model.CustomerPolicyScopeIdType;
import software.amazon.awssdk.services.fms.model.Policy;
import software.amazon.awssdk.services.fms.model.ResourceTag;
import software.amazon.awssdk.services.fms.model.SecurityServicePolicyData;

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
    protected static Map<CustomerPolicyScopeIdType, ? extends List<String>> mapAccounts(List<String> accounts) {
        return new HashMap<CustomerPolicyScopeIdType, List<String>>() {{
            put(CustomerPolicyScopeIdType.fromValue("ACCOUNT"), new ArrayList<>(accounts));
        }};
    }

    /**
     * Convert a CFN policy (from the resource provider) to an FMS policy (from the FMS SDK).
     * @param policy CFN policy that was converted from.
     * @return FMS policy that was converted to.
     */
    public static Policy convertCFNPolicyToFMSPolicy(software.amazon.fms.policy.Policy policy) {

        // assemble the security service policy data
        SecurityServicePolicyData.Builder securityServicePolicyData = SecurityServicePolicyData.builder()
                .type(policy.getSecurityServicePolicyData().getType());

        // add the managed service data if it exists
        if (policy.getSecurityServicePolicyData().getManagedServiceData() != null) {
                securityServicePolicyData.managedServiceData(policy.getSecurityServicePolicyData().getManagedServiceData());
        }

        // assemble the policy with the required parameters
        Policy.Builder builder = Policy.builder()
                .policyName(policy.getPolicyName())
                .remediationEnabled(policy.getRemediationEnabled())
                .resourceType(policy.getResourceType())
                .securityServicePolicyData(securityServicePolicyData.build());

        // check each optional parameter and add it if it exists
        if (policy.getExcludeMap() != null) {
            builder.excludeMap(mapAccounts(policy.getExcludeMap().getACCOUNT()));
        }
        if (policy.getExcludeResourceTags() != null) {
            builder.excludeResourceTags(policy.getExcludeResourceTags());
        }
        if (policy.getIncludeMap() != null) {
            builder.includeMap(mapAccounts(policy.getIncludeMap().getACCOUNT()));
        }
        if (policy.getPolicyId() != null) {
            builder.policyId(policy.getPolicyId());
        }
        if (policy.getPolicyUpdateToken() != null) {
            builder.policyUpdateToken(policy.getPolicyUpdateToken());
        }
        if (policy.getResourceTags() != null) {
            Collection<ResourceTag> resourceTags = new ArrayList<>();
            policy.getResourceTags().forEach(rt -> resourceTags.add(
                    software.amazon.awssdk.services.fms.model.ResourceTag.builder()
                            .key(rt.getKey())
                            .value(rt.getValue())
                            .build()
            ));
            builder.resourceTags(resourceTags);
        }
        if (policy.getResourceTypeList() != null) {
            Collection<String> resourceTypeList = new ArrayList<>(policy.getResourceTypeList());
            builder.resourceTypeList(resourceTypeList);
        }

        // build and return the policy
        return builder.build();
    }
}
