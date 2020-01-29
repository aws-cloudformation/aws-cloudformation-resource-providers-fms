package software.amazon.fms.policy.helpers;

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
        if (!policy.excludeMap().isEmpty()) {
            resourceModelBuilder.excludeMap(
                    IEMap.builder()
                            .aCCOUNT(policy.excludeMap().get(CustomerPolicyScopeIdType.fromValue("ACCOUNT")))
                            .build()
            );
        }
        if (!policy.includeMap().isEmpty()) {
            resourceModelBuilder.includeMap(
                    IEMap.builder()
                            .aCCOUNT(policy.includeMap().get(CustomerPolicyScopeIdType.fromValue("ACCOUNT")))
                            .build()
            );
        }
        if (!policy.resourceTags().isEmpty()) {
            final List<ResourceTag> resourceTags = new ArrayList<>();
            policy.resourceTags().forEach(rt -> resourceTags.add(new ResourceTag(rt.key(), rt.value())));
            resourceModelBuilder.resourceTags(resourceTags);
        }
        if (!policy.resourceTypeList().isEmpty()) {
            resourceModelBuilder.resourceTypeList(policy.resourceTypeList());
        }
        if (!tags.isEmpty()) {
            final List<PolicyTag> policyTags = new ArrayList<>();
            tags.forEach(tag -> policyTags.add(new PolicyTag(tag.key(), tag.value())));
            resourceModelBuilder.tags(policyTags);
        }

        // build and return the resource model
        return resourceModelBuilder.build();
    }
}
