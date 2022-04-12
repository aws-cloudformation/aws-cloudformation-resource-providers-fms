package software.amazon.fms.policy.helpers;

import org.apache.commons.collections.CollectionUtils;
import software.amazon.fms.policy.IEMap;
import software.amazon.fms.policy.PolicyTag;
import software.amazon.fms.policy.ResourceModel;
import software.amazon.fms.policy.ResourceTag;
import software.amazon.fms.policy.SecurityServicePolicyData;
import software.amazon.fms.policy.PolicyOption;
import software.amazon.fms.policy.NetworkFirewallPolicy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CfnSampleHelper extends BaseSampleHelper {

    /**
     * Assembles a sample resource model builder with only the required read/write parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param includeTag1 Should the policy have unique tag 1.
     * @param includeTag2 Should the policy have unique tag 2.
     * @return The assembled resource model builder.
     */
    private static ResourceModel.ResourceModelBuilder sampleRequiredParametersResourceModelBuilder(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2) {

        // assemble sample security service policy data
        final SecurityServicePolicyData securityServicePolicyData = SecurityServicePolicyData.builder()
            .managedServiceData(sampleManagedServiceData)
            .type(samplePolicyType).policyOption(PolicyOption.builder()
                .networkFirewallPolicy(NetworkFirewallPolicy.builder()
                    .firewallDeploymentModel("CENTRALIZED").build()).build()).build();

        // assemble a sample resource model with only the required parameters
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder()
                .excludeResourceTags(sampleExcludeResourceTags)
                .policyName(samplePolicyName)
                .remediationEnabled(sampleRemediationEnabled)
                .resourceType(sampleResourceTypeListElement)
                .securityServicePolicyData(securityServicePolicyData);

        // optionally include the policy id
        if (includeIdentifiers) {
            resourceModelBuilder.id(samplePolicyId);
            resourceModelBuilder.arn(samplePolicyArn);
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

        return resourceModelBuilder;
    }

    /**
     * Assembles a sample resource model builder with all possible read/write parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param includeTag1 Should the policy have unique tag 1.
     * @param includeTag2 Should the policy have unique tag 2.
     * @return The assembled resource model builder.
     */
    private static ResourceModel.ResourceModelBuilder sampleAllParametersResourceModelBuilder(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2) {
        return sampleAllParametersResourceModelBuilder(includeIdentifiers, includeTag1, includeTag2, Collections.emptyList());
    }

    /**
     * Assembles a sample resource model builder with all possible read/write parameters.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param includeTag1 Should the policy have unique tag 1.
     * @param includeTag2 Should the policy have unique tag 2.
     * @param ouList List of OUs to be included.
     * @return The assembled resource model builder.
     */
    private static ResourceModel.ResourceModelBuilder sampleAllParametersResourceModelBuilder(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2,
            final List<String> ouList) {

        // assemble a sample include/exclude map
        final List<String> accountList = new ArrayList<>();
        accountList.add(sampleAccountId);
        final IEMap sampleIEMap;
        if (CollectionUtils.isNotEmpty(ouList)) {
            sampleIEMap = IEMap.builder()
                    .aCCOUNT(accountList)
                    .oRGUNIT(ouList)
                    .build();
        } else {
            sampleIEMap = IEMap.builder()
                    .aCCOUNT(accountList)
                    .build();
        }

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

        // assemble sample policy with all possible parameters
        return sampleRequiredParametersResourceModelBuilder(includeIdentifiers, includeTag1, includeTag2)
                .excludeMap(sampleIEMap)
                .includeMap(sampleIEMap)
                .resourceTags(sampleResourceTags)
                .resourceType(sampleResourceType)
                .resourceTypeList(sampleResourceTypeList)
                .resourcesCleanUp(true);
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

        return sampleRequiredParametersResourceModelBuilder(includeIdentifiers, includeTag1, includeTag2).build();
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

        return sampleAllParametersResourceModelBuilder(includeIdentifiers, includeTag1, includeTag2).build();
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
            final boolean includeTag2,
            final List<String> ouList) {

        return sampleAllParametersResourceModelBuilder(includeIdentifiers, includeTag1, includeTag2, ouList).build();
    }

    /**
     * Assembles a sample resource model with only the SamplePolicyId parameter.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleBareResourceModel(final boolean includeIdentifiers) {

        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();

        // optionally include the policy id
        if (includeIdentifiers) {
            resourceModelBuilder.id(samplePolicyId);
        }

        return resourceModelBuilder.build();
    }

    /**
     * Assembles a sample resource model with the SamplePolicyId parameter and DeleteAllPolicyResources parameter.
     * @param includeIdentifiers Should the policy identifiers be included.
     * @param deleteAllPolicyResources Should all the policy resources be deleted.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleBareResourceModel(
            final boolean includeIdentifiers,
            final boolean deleteAllPolicyResources) {

        final ResourceModel resourceModel = sampleBareResourceModel(includeIdentifiers);
        resourceModel.setDeleteAllPolicyResources(deleteAllPolicyResources);
        return resourceModel;
    }
}
