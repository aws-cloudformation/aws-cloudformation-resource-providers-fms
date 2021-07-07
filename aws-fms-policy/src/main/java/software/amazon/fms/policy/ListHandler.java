package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.ListPoliciesRequest;
import software.amazon.awssdk.services.fms.model.ListPoliciesResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.policy.helpers.CfnHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListHandler extends PolicyHandler<ListPoliciesResponse> {

    public static final int MAX_RESULTS = 50;

    ListHandler() { super(); }

    ListHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected ListPoliciesResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger) {

        // Make the list request
        final ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder()
                .nextToken(request.getNextToken())
                .maxResults(MAX_RESULTS)
                .build();

        ListPoliciesResponse listPoliciesResponse = proxy.injectCredentialsAndInvokeV2(
                listPoliciesRequest,
                client::listPolicies);
        logRequest(listPoliciesResponse, logger);

        return listPoliciesResponse;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final ListPoliciesResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy) {
        List<ResourceModel> resourceModels = new ArrayList<>();

        if (response.hasPolicyList()) {
            // convert the list request response to a resource model
            resourceModels.addAll(
                    response.policyList().stream()
                    .map(p -> CfnHelper.convertFMSPolicySummaryToCFNResourceModel(p, p.policyArn()))
                    .collect(Collectors.toList())
            );

        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(resourceModels)
                .nextToken(response.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
