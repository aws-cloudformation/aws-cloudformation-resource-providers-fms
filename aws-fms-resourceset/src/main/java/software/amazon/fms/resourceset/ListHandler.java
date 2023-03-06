package software.amazon.fms.resourceset;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.ListResourceSetsRequest;
import software.amazon.awssdk.services.fms.model.ListResourceSetsResponse;
import software.amazon.awssdk.services.fms.model.ResourceSetSummary;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.resourceset.helpers.CfnHelper;

import java.util.ArrayList;
import java.util.List;

public class ListHandler extends ResourceSetHandler<ListResourceSetsResponse> {

    public static final int MAX_RESULTS = 50;

    ListHandler() {
        super();
    }

    ListHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected ListResourceSetsResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger
    ) {

        // Make the list request
        final ListResourceSetsRequest listResourceSetsRequest = ListResourceSetsRequest.builder()
                .nextToken(request.getNextToken())
                .maxResults(MAX_RESULTS)
                .build();

        ListResourceSetsResponse listResourceSetsResponse = proxy.injectCredentialsAndInvokeV2(
                listResourceSetsRequest,
                client::listResourceSets);
        logRequest(listResourceSetsResponse, logger);

        return listResourceSetsResponse;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final ListResourceSetsResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy
    ) {
        List<ResourceModel> resourceModels = new ArrayList<>();

        if (response.hasResourceSets()) {
            for (ResourceSetSummary resourceSetSummary : response.resourceSets()) {
                // convert the list request response to a resource model
                resourceModels.add(CfnHelper.convertResourceSetSummaryToCFNResourceModel(resourceSetSummary));
            }
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(resourceModels)
                .nextToken(response.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
