package software.amazon.fms.policy.helpers;

import software.amazon.fms.policy.AccountMap;
import software.amazon.fms.policy.Policy;
import software.amazon.fms.policy.PolicyTag;
import software.amazon.fms.policy.ResourceModel;
import software.amazon.fms.policy.ResourceTag;
import software.amazon.fms.policy.SecurityServicePolicyData;

import java.util.ArrayList;
import java.util.List;

public class CfnSampleHelper extends BaseSampleHelper {

    /**
     * Assembles a sample resource model builder with only the required read/write parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param includeTag1 Should the policy have unique tag 1.
     * @param includeTag2 Should the policy have unique tag 2.
     * @return The assembled resource model builder.
     */
    private static ResourceModel sampleRequiredParametersResourceModelBuilder(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2) {


        // assemble sample security service policy data
        final SecurityServicePolicyData securityServicePolicyData = SecurityServicePolicyData.builder()
                .managedServiceData(sampleManagedServiceData)
                .type(samplePolicyType)
                .build();

        // assemble a sample resource model with only the required parameters
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();
        final Policy.PolicyBuilder policyBuilder = Policy.builder()
                .excludeResourceTags(sampleExcludeResourceTags)
                .policyName(samplePolicyName)
                .remediationEnabled(sampleRemediationEnabled)
                .resourceType(sampleResourceTypeListElement)
                .securityServicePolicyData(securityServicePolicyData);

        // optionally include the policy id
        if (includeIdentifiers) {
            policyBuilder.policyId(samplePolicyId);
            policyBuilder.policyArn(samplePolicyArn);
        }

        // optionally include the policy tags
        List<PolicyTag> sampleTags = new ArrayList<>();
        if (includeTag1) {
            sampleTags.add(PolicyTag.builder()
                    .key(String.format("%s%s", sampleTagKey, "1"))
                    .value(sampleTagValue)
                    .build());
        }
        if (includeTag2) {
            sampleTags.add(PolicyTag.builder()
                    .key(String.format("%s%s", sampleTagKey, "2"))
                    .value(sampleTagValue)
                    .build());
        }

        // dont include an empty tags list
        if (includeTag1 || includeTag2) {
            resourceModelBuilder.tags(sampleTags);
        }

        // add the policy to the resource model
        resourceModelBuilder.policy(policyBuilder.build());

        return resourceModelBuilder.build();
    }

    /**
     * Assembles a sample resource model builder with all possible read/write parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param includeTag1 Should the policy have unique tag 1.
     * @param includeTag2 Should the policy have unique tag 2.
     * @return The assembled resource model builder.
     */
    private static ResourceModel sampleAllParametersResourceModelBuilder(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2) {

        // assemble a sample account map
        final List<String> accountList = new ArrayList<>();
        accountList.add(sampleAccountId);
        final AccountMap sampleAccountMap = AccountMap.builder()
                .aCCOUNT(accountList)
                .build();

        // assemble sample resource tags
        final List<ResourceTag> sampleResourceTags = new ArrayList<>();
        sampleResourceTags.add(
                ResourceTag.builder()
                        .key(sampleTagKey)
                        .value(sampleTagValue)
                        .build()
        );

        // assemble a sample resource type list
        final List<String> sampleResourceTypeList = new ArrayList<>();
        sampleResourceTypeList.add(sampleResourceTypeListElement);

        // assemble sample resource model with all possible policy parameters
        final ResourceModel resourceModel =
                sampleRequiredParametersResourceModelBuilder(includeIdentifiers, includeTag1, includeTag2);
        final Policy policy = resourceModel.getPolicy();
        policy.setExcludeMap(sampleAccountMap);
        policy.setIncludeMap(sampleAccountMap);
        policy.setResourceTags(sampleResourceTags);
        policy.setResourceType(sampleResourceType);
        policy.setResourceTypeList(sampleResourceTypeList);
        resourceModel.setPolicy(policy);

        return resourceModel;
    }

    /**
     * Assembles a sample resource model with only the required read/write parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param includeTag1 Should the policy have unique tag 1.
     * @param includeTag2 Should the policy have unique tag 2.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleRequiredParametersResourceModel(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2) {

        return sampleRequiredParametersResourceModelBuilder(includeIdentifiers, includeTag1, includeTag2);
    }

    /**
     * Assembles a sample resource model with all possible read/write parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param includeTag1 Should the policy have unique tag 1.
     * @param includeTag2 Should the policy have unique tag 2.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleAllParametersResourceModel(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2) {

        return sampleAllParametersResourceModelBuilder(includeIdentifiers, includeTag1, includeTag2);
    }

    /**
     * Assembles a sample resource model with policy summary parameters.
     * @return The assembled resource model.
     */
    private static ResourceModel samplePolicySummaryResourceModel() {

        // assemble sample security service policy data
        final SecurityServicePolicyData sampleSecurityServicePolicyData = SecurityServicePolicyData.builder()
                .type(sampleResourceType)
                .build();

        // assemble the sample policy with the required parameters
        final Policy policy = Policy.builder()
                .policyId(samplePolicyId)
                .policyName(samplePolicyName)
                .resourceType(sampleResourceType)
                .securityServicePolicyData(sampleSecurityServicePolicyData)
                .remediationEnabled(sampleRemediationEnabled)
                .policyArn(samplePolicyArn)
                .build();

        return ResourceModel.builder().policy(policy).build();
    }

    /**
     * Assembles a list of sample resource models.
     * @return The assembled resource model list.
     */
    public static List<ResourceModel> samplePolicySummaryResourceModelList() {

        final List<ResourceModel> resourceModelList = new ArrayList<>();
        resourceModelList.add(samplePolicySummaryResourceModel());
        resourceModelList.add(samplePolicySummaryResourceModel());
        return resourceModelList;
    }

    /**
     * Assembles a sample resource model with only the SamplePolicyId parameter.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleBareResourceModel(final boolean includeIdentifiers) {

        // optionally include the policy id
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();
        if (includeIdentifiers) {
            resourceModelBuilder.policy(Policy.builder().policyId(samplePolicyId).build());
        }

        return resourceModelBuilder.build();
    }
}
