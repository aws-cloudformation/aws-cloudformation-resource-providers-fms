package software.amazon.fms.policy.helpers;

import software.amazon.awssdk.services.fms.model.CustomerPolicyScopeIdType;
import software.amazon.awssdk.services.fms.model.PolicySummary;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.fms.policy.AccountMap;
import software.amazon.fms.policy.Policy;
import software.amazon.fms.policy.ResourceModel;
import software.amazon.fms.policy.ResourceTag;
import software.amazon.fms.policy.SecurityServicePolicyData;

import java.util.ArrayList;
import java.util.List;

public class CfnHelper {

    /**
     * Convert an FMS policy (from the FMS SDK) to a CFN resource model (from the resource provider).
     * @param policy FMS policy that was converted from.
     * @param policyArn Policy ARN to add to the resource model.
     * @param tags FMS tags to add to the resource model.
     * @return CFN resource model that was converted to.
     */
    public static ResourceModel convertFMSPolicyToCFNResourceModel(software.amazon.awssdk.services.fms.model.Policy policy, String policyArn, List<Tag> tags) {

        // assemble the security service policy data
        final SecurityServicePolicyData.SecurityServicePolicyDataBuilder securityServicePolicyData =
                SecurityServicePolicyData.builder().type(policy.securityServicePolicyData().typeAsString());

        // add the managed service data if it exists
        if (!policy.securityServicePolicyData().managedServiceData().isEmpty()) {
            securityServicePolicyData.managedServiceData(policy.securityServicePolicyData().managedServiceData());
        }

        // assemble the resource model with the required parameters
        final Policy.PolicyBuilder policyBuilder = Policy.builder()
                .excludeResourceTags(policy.excludeResourceTags())
                .policyName(policy.policyName())
                .remediationEnabled(policy.remediationEnabled())
                .resourceType(policy.resourceType())
                .securityServicePolicyData(securityServicePolicyData.build())
                .policyId(policy.policyId())
                .policyArn(policyArn);

        // check each optional parameter and add it if it exists
        if (!policy.excludeMap().isEmpty()) {
            policyBuilder.excludeMap(
                    AccountMap.builder()
                            .aCCOUNT(policy.excludeMap().get(CustomerPolicyScopeIdType.fromValue("ACCOUNT")))
                            .build()
            );
        }
        if (!policy.includeMap().isEmpty()) {
            policyBuilder.includeMap(
                    AccountMap.builder()
                            .aCCOUNT(policy.includeMap().get(CustomerPolicyScopeIdType.fromValue("ACCOUNT")))
                            .build()
            );
        }
        if (!policy.resourceTags().isEmpty()) {
            final List<ResourceTag> resourceTags = new ArrayList<>();
            policy.resourceTags().forEach(rt -> resourceTags.add(new ResourceTag(rt.key(), rt.value())));
            policyBuilder.resourceTags(resourceTags);
        }
        if (!policy.resourceTypeList().isEmpty()) {
            policyBuilder.resourceTypeList(policy.resourceTypeList());
        }

        // add tags to the resource model
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder()
                .policy(policyBuilder.build());
        if (!tags.isEmpty()) {
            final List<ResourceTag> resourceTags = new ArrayList<>();
            tags.forEach(tag -> resourceTags.add(new ResourceTag(tag.key(), tag.value())));
            resourceModelBuilder.tags(resourceTags);
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
        final SecurityServicePolicyData securityServicePolicyData = SecurityServicePolicyData.builder()
                .type(policySummary.resourceType())
                .build();

        // assemble the policy with the required parameters
        final Policy policy = Policy.builder()
                .policyId(policySummary.policyId())
                .policyName(policySummary.policyName())
                .resourceType(policySummary.resourceType())
                .securityServicePolicyData(securityServicePolicyData)
                .remediationEnabled(policySummary.remediationEnabled())
                .policyArn(policySummary.policyArn())
                .build();
        return ResourceModel.builder()
                .policy(policy)
                .build();
    }
}
