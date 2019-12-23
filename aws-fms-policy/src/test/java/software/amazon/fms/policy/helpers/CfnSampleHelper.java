package software.amazon.fms.policy.helpers;

import software.amazon.fms.policy.AccountMap;
import software.amazon.fms.policy.Policy;
import software.amazon.fms.policy.ResourceModel;
import software.amazon.fms.policy.ResourceTag;
import software.amazon.fms.policy.SecurityServicePolicyData;

import java.util.ArrayList;
import java.util.List;

public class CfnSampleHelper extends BaseSampleHelper {

    /**
     * Assembles a sample resource model policy with only the required read/write parameters.
     * @return The assembled policy builder.
     */
    private static Policy.PolicyBuilder sampleRequiredParametersPolicy() {

        // assemble sample security service policy data
        final SecurityServicePolicyData securityServicePolicyData = SecurityServicePolicyData.builder()
                .managedServiceData(sampleManagedServiceData)
                .type(samplePolicyType)
                .build();

        // assemble a sample policy with only the required parameters
        return Policy.builder()
                .excludeResourceTags(sampleExcludeResourceTags)
                .policyName(samplePolicyName)
                .remediationEnabled(sampleRemediationEnabled)
                .resourceType(sampleResourceTypeListElement)
                .securityServicePolicyData(securityServicePolicyData);
    }

    /**
     * Assembles a sample resource model policy with all possible read/write parameters.
     * @return The assembled policy builder.
     */
    private static Policy.PolicyBuilder sampleAllParametersPolicy() {

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
        return sampleRequiredParametersPolicy()
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

        return ResourceModel.builder()
                .policy(sampleRequiredParametersPolicy().build())
                .build();
    }

    /**
     * Assembles a sample resource model with all possible read/write parameters.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleAllParametersResourceModel() {

        return ResourceModel.builder()
                .policy(sampleAllParametersPolicy().build())
                .build();
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
        Policy policy = Policy.builder()
                .policyId(samplePolicyId)
                .policyName(samplePolicyName)
                .resourceType(sampleResourceType)
                .securityServicePolicyData(sampleSecurityServicePolicyData)
                .remediationEnabled(sampleRemediationEnabled)
                .build();

        // build and return the sample resource model
        return ResourceModel.builder()
                .policy(policy)
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

        final Policy policy = Policy.builder()
                .policyId(samplePolicyId)
                .build();
        return ResourceModel.builder()
                .policy(policy)
                .build();
    }

    /**
     * Adds identifying characteristics (PolicyId, PolicyUpdateToken, and PolicyArn) to a resource model.
     * @param resourceModel Resource model to add identifying characteristics to.
     * @return Resource model containing identifying characteristics.
     */
    public static ResourceModel identifySampleResourceModel(final ResourceModel resourceModel) {

        Policy policy = resourceModel.getPolicy();
        policy.setPolicyId(samplePolicyId);
        policy.setPolicyUpdateToken(samplePolicyUpdateToken);
        return ResourceModel.builder()
                .policy(policy)
                .policyArn(samplePolicyArn)
                .build();
    }
}
