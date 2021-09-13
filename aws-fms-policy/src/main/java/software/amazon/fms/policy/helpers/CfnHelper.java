package software.amazon.fms.policy.helpers;

import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.fms.model.CustomerPolicyScopeIdType;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.fms.policy.IEMap;
import software.amazon.fms.policy.PolicyTag;
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
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder()
                .excludeResourceTags(policy.excludeResourceTags())
                .policyName(policy.policyName())
                .remediationEnabled(policy.remediationEnabled())
                .resourceType(policy.resourceType())
                .securityServicePolicyData(securityServicePolicyData.build())
                .id(policy.policyId())
                .arn(policyArn);

        // check each optional parameter and add it if it exists
        IEMap cfnExcludeMap = new IEMap();
        if (!policy.excludeMap().isEmpty()) {
            if (CollectionUtils.isNotEmpty(policy.excludeMap().get(CustomerPolicyScopeIdType.ACCOUNT))) {
                cfnExcludeMap.setACCOUNT(policy.excludeMap().get(CustomerPolicyScopeIdType.ACCOUNT));
            }
            if (CollectionUtils.isNotEmpty(policy.excludeMap().get(CustomerPolicyScopeIdType.ORG_UNIT))) {
                cfnExcludeMap.setORGUNIT(policy.excludeMap().get(CustomerPolicyScopeIdType.ORG_UNIT));
            }
        }
        resourceModelBuilder.excludeMap(cfnExcludeMap);
        IEMap cfnIncludeMap = new IEMap();
        if (!policy.includeMap().isEmpty()) {
            if (CollectionUtils.isNotEmpty(policy.includeMap().get(CustomerPolicyScopeIdType.ACCOUNT))) {
                cfnIncludeMap.setACCOUNT(policy.includeMap().get(CustomerPolicyScopeIdType.ACCOUNT));
            }
            if (CollectionUtils.isNotEmpty(policy.includeMap().get(CustomerPolicyScopeIdType.ORG_UNIT))) {
                cfnIncludeMap.setORGUNIT(policy.includeMap().get(CustomerPolicyScopeIdType.ORG_UNIT));
            }
        }
        resourceModelBuilder.includeMap(cfnIncludeMap);
        if (!policy.resourceTags().isEmpty()) {
            final List<ResourceTag> resourceTags = new ArrayList<>();
            policy.resourceTags().forEach(rt -> resourceTags.add(new ResourceTag(rt.key(), rt.value())));
            resourceModelBuilder.resourceTags(resourceTags);
        }
        if (!policy.resourceTypeList().isEmpty()) {
            resourceModelBuilder.resourceTypeList(policy.resourceTypeList());
        } else {
            resourceModelBuilder.resourceTypeList(new ArrayList<>());
        }
        if (!tags.isEmpty()) {
            final List<PolicyTag> policyTags = new ArrayList<>();
            tags.forEach(tag -> policyTags.add(new PolicyTag(tag.key(), tag.value())));
            resourceModelBuilder.tags(policyTags);
        }

        if(policy.deleteUnusedFMManagedResources()!=null){
            resourceModelBuilder.resourcesCleanUp(policy.deleteUnusedFMManagedResources());
        }

        // build and return the resource model
        return resourceModelBuilder.build();
    }

    /**
     * Convert a list of FMS policies (from the FMS SDK) to a list of CFN resource models (from the resource provider).
     * @param policySummary FMS policy that was converted from.
     * @param policyArn Policy ARN to add to the resource model.
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
}
