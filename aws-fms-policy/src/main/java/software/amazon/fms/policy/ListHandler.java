package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.model.ListPoliciesRequest;
import software.amazon.awssdk.services.fms.model.ListPoliciesResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.fms.policy.helpers.CfnHelper;

import java.util.ArrayList;
import java.util.List;

public class ListHandler extends PolicyHandler<ListPoliciesResponse> {

    @Override
    protected ListPoliciesResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState) {

        final ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder().build();
        return proxy.injectCredentialsAndInvokeV2(listPoliciesRequest, client::listPolicies);
    }

    @Override
    protected List<ResourceModel> constructSuccessResourceModels(final ListPoliciesResponse response) {

        List<ResourceModel> resourceModels = new ArrayList<>();
        response.policyList().forEach(p -> resourceModels.add(CfnHelper.convertFMSPolicySummaryToCFNResourceModel(p)));
        return resourceModels;
    }
}
