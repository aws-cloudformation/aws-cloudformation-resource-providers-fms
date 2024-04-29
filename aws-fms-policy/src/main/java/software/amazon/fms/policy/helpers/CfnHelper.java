package software.amazon.fms.policy.helpers;

import software.amazon.awssdk.services.fms.model.CustomerPolicyScopeIdType;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.fms.policy.IEMap;
import software.amazon.fms.policy.IcmpTypeCode;
import software.amazon.fms.policy.NetworkAclCommonPolicy;
import software.amazon.fms.policy.NetworkAclEntry;
import software.amazon.fms.policy.NetworkAclEntrySet;
import software.amazon.fms.policy.NetworkFirewallPolicy;
import software.amazon.fms.policy.PolicyOption;
import software.amazon.fms.policy.PolicyTag;
import software.amazon.fms.policy.PortRange;
import software.amazon.fms.policy.ResourceModel;
import software.amazon.fms.policy.ResourceTag;
import software.amazon.fms.policy.SecurityServicePolicyData;
import software.amazon.fms.policy.ThirdPartyFirewallPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CfnHelper {

    /**
     * Convert an FMS policy (from the FMS SDK) to a CFN resource model (from the resource provider).
     *
     * @param policy    FMS policy that was converted from.
     * @param policyArn Policy ARN to add to the resource model.
     * @param tags      FMS tags to add to the resource model.
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

        if (policy.securityServicePolicyData().policyOption() != null) {
            securityServicePolicyData.policyOption(convertFmsPolicyOptionToCFNPolicyOption(
                    policy.securityServicePolicyData().policyOption()
            ));
        }

        // assemble the resource model with the required parameters
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder()
                .excludeResourceTags(policy.excludeResourceTags())
                .policyName(policy.policyName())
                .policyDescription(policy.policyDescription())
                .remediationEnabled(policy.remediationEnabled())
                .resourceType(policy.resourceType())
                .securityServicePolicyData(securityServicePolicyData.build())
                .id(policy.policyId())
                .arn(policyArn);

        // check each optional parameter and add it if it exists
        final IEMap cfnExcludeMap = new IEMap();
        if (policy.excludeMap() != null) {
            if (policy.excludeMap().containsKey(CustomerPolicyScopeIdType.ACCOUNT)) {
                cfnExcludeMap.setACCOUNT(policy.excludeMap().get(CustomerPolicyScopeIdType.ACCOUNT));
            }
            if (policy.excludeMap().containsKey(CustomerPolicyScopeIdType.ORG_UNIT)) {
                cfnExcludeMap.setORGUNIT(policy.excludeMap().get(CustomerPolicyScopeIdType.ORG_UNIT));
            }
        }
        resourceModelBuilder.excludeMap(cfnExcludeMap);

        final IEMap cfnIncludeMap = new IEMap();
        if (policy.includeMap() != null) {
            if (policy.includeMap().containsKey(CustomerPolicyScopeIdType.ACCOUNT)) {
                cfnIncludeMap.setACCOUNT(policy.includeMap().get(CustomerPolicyScopeIdType.ACCOUNT));
            }
            if (policy.includeMap().containsKey(CustomerPolicyScopeIdType.ORG_UNIT)) {
                cfnIncludeMap.setORGUNIT(policy.includeMap().get(CustomerPolicyScopeIdType.ORG_UNIT));
            }
        }
        resourceModelBuilder.includeMap(cfnIncludeMap);

        if (!policy.resourceTags().isEmpty()) {
            final List<ResourceTag> resourceTags = new ArrayList<>();
            policy.resourceTags().forEach(rt -> resourceTags.add(new ResourceTag(rt.key(), rt.value())));
            resourceModelBuilder.resourceTags(resourceTags);
        }
        resourceModelBuilder.resourceTypeList(policy.resourceTypeList());
        resourceModelBuilder.resourceSetIds(policy.resourceSetIds());
        if (!tags.isEmpty()) {
            final List<PolicyTag> policyTags = new ArrayList<>();
            tags.forEach(tag -> policyTags.add(new PolicyTag(tag.key(), tag.value())));
            resourceModelBuilder.tags(policyTags);
        }

        if (policy.deleteUnusedFMManagedResources() != null) {
            resourceModelBuilder.resourcesCleanUp(policy.deleteUnusedFMManagedResources());
        }

        // build and return the resource model
        return resourceModelBuilder.build();
    }

    /**
     * Convert a list of FMS policies (from the FMS SDK) to a list of CFN resource models (from the resource provider).
     *
     * @param policySummary FMS policy that was converted from.
     * @param policyArn     Policy ARN to add to the resource model.
     * @return CFN resource model that was converted to.
     */
    public static ResourceModel convertFMSPolicySummaryToCFNResourceModel(
            final software.amazon.awssdk.services.fms.model.PolicySummary policySummary,
            final String policyArn) {

        // assemble the security service policy data
        final SecurityServicePolicyData.SecurityServicePolicyDataBuilder securityServicePolicyData =
                SecurityServicePolicyData.builder().type(policySummary.securityServiceTypeAsString());

        // assemble the resource model with the required parameters
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder()
                .policyName(policySummary.policyName())
                .remediationEnabled(policySummary.remediationEnabled())
                .resourceType(policySummary.resourceType())
                .securityServicePolicyData(securityServicePolicyData.build())
                .id(policySummary.policyId())
                .arn(policyArn);

        // build and return the resource model
        return resourceModelBuilder.build();

    }

    /**
     * Convert the FMS PolicyOption to CFN PolicyOption.
     *
     * @param policyOption FMS PolicyOption.
     * @return CFN resource model that was converted to.
     */
    public static PolicyOption convertFmsPolicyOptionToCFNPolicyOption(
            software.amazon.awssdk.services.fms.model.PolicyOption policyOption) {

        final PolicyOption.PolicyOptionBuilder builder = PolicyOption.builder();

        if (policyOption.networkFirewallPolicy() != null) {
            builder.networkFirewallPolicy(NetworkFirewallPolicy.
                    builder().firewallDeploymentModel(policyOption.
                            networkFirewallPolicy().firewallDeploymentModelAsString()).build()).build();
        }

        if (policyOption.thirdPartyFirewallPolicy() != null) {
            builder.thirdPartyFirewallPolicy(ThirdPartyFirewallPolicy.
                    builder().firewallDeploymentModel(policyOption.
                            thirdPartyFirewallPolicy().firewallDeploymentModelAsString()).build()).build();
        }

        if (policyOption.networkAclCommonPolicy() != null
                && policyOption.networkAclCommonPolicy().networkAclEntrySet() != null) {
            final software.amazon.awssdk.services.fms.model.NetworkAclEntrySet entrySet =
                    policyOption.networkAclCommonPolicy().networkAclEntrySet();

            builder.networkAclCommonPolicy(NetworkAclCommonPolicy.builder()
                    .networkAclEntrySet(NetworkAclEntrySet.builder()
                            .firstEntries(
                                    entrySet.firstEntries().stream()
                                            .map(CfnHelper::convertFmsNetworkAclEntryToCFNNetworkAclEntry)
                                            .collect(Collectors.toList())
                            ).lastEntries(
                                    entrySet.lastEntries().stream()
                                            .map(CfnHelper::convertFmsNetworkAclEntryToCFNNetworkAclEntry)
                                            .collect(Collectors.toList())
                            ).forceRemediateForFirstEntries(
                                    entrySet.forceRemediateForFirstEntries()
                            ).forceRemediateForLastEntries(
                                    entrySet.forceRemediateForLastEntries()
                            ).build())
                    .build()
            ).build();
        }
        return builder.build();
    }

    /**
     * Convert the FMS NetworkAclEntry to CFN NetworkAclEntry.
     *
     * @param networkAclEntry FMS NetworkAclEntry.
     * @return CFN resource model that was converted to.
     */
    public static NetworkAclEntry convertFmsNetworkAclEntryToCFNNetworkAclEntry(
            software.amazon.awssdk.services.fms.model.NetworkAclEntry networkAclEntry) {

        return NetworkAclEntry.builder()
                .protocol(networkAclEntry.protocol())
                .ruleAction(networkAclEntry.ruleActionAsString())
                .cidrBlock(networkAclEntry.cidrBlock())
                .ipv6CidrBlock(networkAclEntry.ipv6CidrBlock())
                .icmpTypeCode(networkAclEntry.icmpTypeCode() == null
                        ? null
                        : IcmpTypeCode.builder()
                        .type(networkAclEntry.icmpTypeCode().type())
                        .code(networkAclEntry.icmpTypeCode().code())
                        .build()
                )
                .portRange(networkAclEntry.portRange() == null
                        ? null
                        : PortRange.builder()
                        .from(networkAclEntry.portRange().from())
                        .to(networkAclEntry.portRange().to())
                        .build())
                .egress(networkAclEntry.egress())
                .build();
    }
}
