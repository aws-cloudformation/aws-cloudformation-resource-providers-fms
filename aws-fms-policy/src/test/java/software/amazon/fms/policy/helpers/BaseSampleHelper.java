package software.amazon.fms.policy.helpers;

import software.amazon.awssdk.services.fms.model.SecurityServiceType;

class BaseSampleHelper {

    public final static String sampleAccountId = "000000000000";
    public final static String sampleManagedServiceData = "{\"type\":\"SHIELD_ADVANCED\"}";

    public final static SecurityServiceType sampleSecurityServiceType = SecurityServiceType.SHIELD_ADVANCED;
    public final static boolean sampleExcludeResourceTags = false;
    public final static String samplePolicyId = "00000000-0000-0000-0000-000000000000";
    public final static String samplePolicyName = "TEST";
    public final static String samplePolicyDescription = "TEST";
    public final static String samplePolicyUpdateToken = "000000000000000000000000==";
    public final static boolean sampleRemediationEnabled = false;
    public final static String sampleTagKey = "key";
    public final static String sampleTagValue = "value";
    public final static String sampleResourceType = "ResourceTypeList";
    public final static String sampleResourceTypeListElement = "AWS::ElasticLoadBalancingV2::LoadBalancer";
    public final static String sampleResourceSetIdsElement = "11100000-0000-0000-0000-000000000111";
    public final static String samplePolicyArn = "arn:aws:fms:us-east-1:000000000000:policy/00000000-0000-0000-0000-000000000000";
}
