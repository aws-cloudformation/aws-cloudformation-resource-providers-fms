package software.amazon.fms.policy.helpers;

import software.amazon.fms.policy.AccountMap;
import software.amazon.fms.policy.ResourceModel;
import software.amazon.fms.policy.ResourceTag;
import software.amazon.fms.policy.SecurityServicePolicyData;

import java.util.ArrayList;
import java.util.List;

public class CfnSampleHelper extends BaseSampleHelper {

    /**
     * Assembles a sample resource model builder with only the required read/write parameters.
     * @return The assembled resource model builder.
     */
    private static ResourceModel.ResourceModelBuilder sampleRequiredParametersResourceModelBuilder() {

        // assemble sample security service policy data
        final SecurityServicePolicyData securityServicePolicyData = SecurityServicePolicyData.builder()
                .managedServiceData(sampleManagedServiceData)
                .type(samplePolicyType)
                .build();

        // assemble a sample policy with only the required parameters
        return ResourceModel.builder()
                .excludeResourceTags(sampleExcludeResourceTags)
                .policyName(samplePolicyName)
                .remediationEnabled(sampleRemediationEnabled)
                .resourceType(sampleResourceTypeListElement)
                .securityServicePolicyData(securityServicePolicyData);
    }

    /**
     * Assembles a sample resource model builder with all possible read/write parameters.
     * @return The assembled resource model builder.
     */
    private static ResourceModel.ResourceModelBuilder sampleAllParametersResourceModelBuilder() {

        // assemble a sample account map
        List<String> accountList = new ArrayList<>();
        accountList.add(sampleAccountId);
        final AccountMap sampleAccountMap = AccountMap.builder()
                .aCCOUNT(accountList)
                .build();

        // assemble sample resource tags
        final List<ResourceTag> sampleResourceTags = new ArrayList<>();
        sampleResourceTags.add(
                ResourceTag.builder()
                        .key(sampleResourceTagKey)
                        .value(sampleResourceTagValue)
                        .build()
        );

        // assemble a sample resource type list
        final List<String> sampleResourceTypeList = new ArrayList<>();
        sampleResourceTypeList.add(sampleResourceTypeListElement);

        // assemble sample policy with all possible parameters
        return sampleRequiredParametersResourceModelBuilder()
                .excludeMap(sampleAccountMap)
                .includeMap(sampleAccountMap)
                .resourceTags(sampleResourceTags)
                .resourceType(sampleResourceType)
                .resourceTypeList(sampleResourceTypeList);
    }

    /**
     * Assembles a sample resource model with only the required read/write parameters.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleRequiredParametersResourceModel() {

        return sampleRequiredParametersResourceModelBuilder().build();
    }

    /**
     * Assembles a sample resource model with all possible read/write parameters.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleAllParametersResourceModel() {

        return sampleAllParametersResourceModelBuilder().build();
    }

    /**
     * Assembles a sample resource model with policy summary parameters.
     * @return The assembled resource model.
     */
    private static ResourceModel samplePolicySummaryResourceModel() {

        // assemble sample security service policy data
        SecurityServicePolicyData sampleSecurityServicePolicyData = SecurityServicePolicyData.builder()
                .type(sampleResourceType)
                .build();

        // assemble the sample policy with the required parameters
        return ResourceModel.builder()
                .policyId(samplePolicyId)
                .policyName(samplePolicyName)
                .resourceType(sampleResourceType)
                .securityServicePolicyData(sampleSecurityServicePolicyData)
                .remediationEnabled(sampleRemediationEnabled)
                .policyArn(samplePolicyArn)
                .build();
    }

    /**
     * Assembles a list of sample resource models.
     * @return The assembled resource model list.
     */
    public static List<ResourceModel> samplePolicySummaryResourceModelList() {

        List<ResourceModel> resourceModelList = new ArrayList<>();
        resourceModelList.add(samplePolicySummaryResourceModel());
        resourceModelList.add(samplePolicySummaryResourceModel());
        return resourceModelList;
    }

    /**
     * Assembles a sample resource model with only the SamplePolicyId parameter.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleBareResourceModel() {

        return ResourceModel.builder().policyId(samplePolicyId).build();
    }

    /**
     * Adds identifying characteristics (PolicyId and PolicyArn) to a resource model.
     * @param resourceModel Resource model to add identifying characteristics to.
     * @return Resource model containing identifying characteristics.
     */
    public static ResourceModel identifySampleResourceModel(final ResourceModel resourceModel) {

        resourceModel.setPolicyId(samplePolicyId);
        resourceModel.setPolicyArn(samplePolicyArn);
        return resourceModel;
    }
}
