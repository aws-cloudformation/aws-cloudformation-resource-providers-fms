package software.amazon.fms.resourceset;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.GetResourceSetRequest;
import software.amazon.awssdk.services.fms.model.GetResourceSetResponse;
import software.amazon.awssdk.services.fms.model.ListResourceSetResourcesRequest;
import software.amazon.awssdk.services.fms.model.ListResourceSetResourcesResponse;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.fms.model.Resource;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.resourceset.helpers.CfnHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadHandler extends ResourceSetHandler<GetResourceSetResponse> {

    ReadHandler() {
        super();
    }

    ReadHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected GetResourceSetResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger
    ) {

        // make the read request
        final GetResourceSetRequest getPolicyRequest = GetResourceSetRequest.builder()
                .identifier(request.getDesiredResourceState().getId())
                .build();
        final GetResourceSetResponse response = proxy.injectCredentialsAndInvokeV2(
                getPolicyRequest,
                client::getResourceSet);
        logRequest(response, logger);
        return response;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final GetResourceSetResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy
    ) {
        return ProgressEvent.defaultSuccessHandler(constructSuccessResourceModel(response, request, proxy));
    }

    private ResourceModel constructSuccessResourceModel(
            final GetResourceSetResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy
    ) {

        // list the tags for the resourceSet
        final ListTagsForResourceRequest listTagsForResourceRequest = ListTagsForResourceRequest.builder()
                .resourceArn(response.resourceSetArn())
                .build();
        final ListTagsForResourceResponse listTagsForResourceResponse = proxy.injectCredentialsAndInvokeV2(
                listTagsForResourceRequest,
                client::listTagsForResource);

        // list the resources for the resourceSet
        String nextToken = null;
        List<Resource> resources = new ArrayList<>();
        do {
            // list the resources for the resourceSet
            ListResourceSetResourcesRequest resourceSetResourcesRequest = ListResourceSetResourcesRequest.builder()
                    .identifier(response.resourceSet().id())
                    .nextToken(nextToken)
                    .build();

            ListResourceSetResourcesResponse resourceSetResourcesResponse = proxy.injectCredentialsAndInvokeV2(
                    resourceSetResourcesRequest,
                    client::listResourceSetResources);

            nextToken = resourceSetResourcesResponse.nextToken();

            resources.addAll(resourceSetResourcesResponse.items());
        } while (nextToken != null);

        // convert the read request response to a resource model
        return CfnHelper.convertResourceSetToCFNResourceModel(
                response.resourceSet(),
                resources.stream().map(i -> i.uri()).collect(Collectors.toSet()),
                listTagsForResourceResponse.tagList());
    }
}
