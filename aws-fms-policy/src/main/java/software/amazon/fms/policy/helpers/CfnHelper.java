package software.amazon.fms.policy.helpers;

import software.amazon.awssdk.services.fms.model.CustomerPolicyScopeIdType;
import software.amazon.awssdk.services.fms.model.PolicySummary;
import software.amazon.fms.policy.AccountMap;
import software.amazon.fms.policy.ResourceModel;
import software.amazon.fms.policy.ResourceTag;
import software.amazon.fms.policy.SecurityServicePolicyData;

import java.util.ArrayList;
import java.util.List;

public class CfnHelper {

    /**
     * Convert an FMS policy (from the FMS SDK) to a CFN resource model (from the resource provider).
     * @param policy FMS policy that was converted from.
     * @return CFN resource model that was converted to.
     */
    public static ResourceModel convertFMSPolicyToCFNResourceModel(software.amazon.awssdk.services.fms.model.Policy policy) {
        // assemble the security service policy data
        SecurityServicePolicyData.SecurityServicePolicyDataBuilder securityServicePolicyData = SecurityServicePolicyData.builder()
                .type(policy.securityServicePolicyData().typeAsString());

        // add the managed service data if it exists
        if (!policy.securityServicePolicyData().managedServiceData().isEmpty()) {
            securityServicePolicyData.managedServiceData(policy.securityServicePolicyData().managedServiceData());
        }

        // assemble the resource model with the required parameters
        ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder()
                .excludeResourceTags(policy.excludeResourceTags())
                .policyName(policy.policyName())
                .remediationEnabled(policy.remediationEnabled())
                .resourceType(policy.resourceType())
                .securityServicePolicyData(securityServicePolicyData.build())
                .policyId(policy.policyId());

        // check each optional parameter and add it if it exists
        if (!policy.excludeMap().isEmpty()) {
            final AccountMap excludeMap = AccountMap.builder()
                .aCCOUNT(policy.excludeMap().get(CustomerPolicyScopeIdType.fromValue("ACCOUNT")))
                .build();
            resourceModelBuilder.excludeMap(excludeMap);
        }
        if (!policy.includeMap().isEmpty()) {
            final AccountMap includeMap = AccountMap.builder()
                .aCCOUNT(policy.includeMap().get(CustomerPolicyScopeIdType.fromValue("ACCOUNT")))
                .build();
            resourceModelBuilder.includeMap(includeMap);
        }
        if (!policy.resourceTags().isEmpty()) {
            final List<ResourceTag> resourceTags = new ArrayList<>();
            policy.resourceTags().forEach(rt -> resourceTags.add(new ResourceTag(rt.key(), rt.value())));
            resourceModelBuilder.resourceTags(resourceTags);
        }
        if (!policy.resourceTypeList().isEmpty()) {
            resourceModelBuilder.resourceTypeList(policy.resourceTypeList());
        }

        // build and return the resource model
        return resourceModelBuilder.build();
    }

    /**
     * Convert an FMS policy summary (from the FMS SDK) to a CFN resource model (from the resource provider).
     * @param policySummary FMS policy summary that was converted from.
     * @return CFN resource model that was converted to.
     */
    public static ResourceModel convertFMSPolicySummaryToCFNResourceModel(PolicySummary policySummary) {

        // assemble the security service policy data
        SecurityServicePolicyData securityServicePolicyData = SecurityServicePolicyData.builder()
                .type(policySummary.resourceType())
                .build();

        // assemble the policy with the required parameters
        return ResourceModel.builder()
                .policyId(policySummary.policyId())
                .policyName(policySummary.policyName())
                .resourceType(policySummary.resourceType())
                .securityServicePolicyData(securityServicePolicyData)
                .remediationEnabled(policySummary.remediationEnabled())
                .policyArn(policySummary.policyArn())
                .build();
    }
}
