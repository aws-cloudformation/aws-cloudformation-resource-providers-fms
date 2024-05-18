package software.amazon.fms.resourceset;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.DeleteResourceSetRequest;
import software.amazon.awssdk.services.fms.model.PutResourceSetRequest;
import software.amazon.awssdk.services.fms.model.PutResourceSetResponse;
import software.amazon.awssdk.services.fms.model.Tag;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.resourceset.helpers.AssociationHelper;
import software.amazon.fms.resourceset.helpers.CfnHelper;
import software.amazon.fms.resourceset.helpers.FmsHelper;

import java.util.Collections;
import java.util.List;

public class CreateHandler extends ResourceSetHandler<PutResourceSetResponse> {

    CreateHandler() {
        super();
    }

    CreateHandler(final FmsClient client) {
        super(client);
    }

    @Override
    protected PutResourceSetResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger
    ) {

        // make the create request
        final PutResourceSetRequest.Builder putResourceSetRequestBuilder = PutResourceSetRequest.builder()
                .resourceSet(FmsHelper.convertCFNResourceModelToFMSResourceSet(request.getDesiredResourceState()));
        final List<Tag> tags = FmsHelper.convertCFNTagMapToFMSTagSet(request.getDesiredResourceTags());

        if (!tags.isEmpty()) {
            putResourceSetRequestBuilder.tagList(tags);
        }
        final PutResourceSetResponse putResourceSetResponse = proxy.injectCredentialsAndInvokeV2(
                putResourceSetRequestBuilder.build(),
                client::putResourceSet);
        logRequest(putResourceSetResponse, logger);

        AssociationHelper.updateResourceAssociations(
                putResourceSetResponse.resourceSet().id(),
                request.getDesiredResourceState().getResources(),
                client,
                proxy,
                logger
        );

        return putResourceSetResponse;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final PutResourceSetResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy
    ) {
        return ProgressEvent.defaultSuccessHandler(constructSuccessResourceModel(response, request, proxy));
    }

    private ResourceModel constructSuccessResourceModel(
            final PutResourceSetResponse response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy
    ) {

        try {
            // convert the create request response to a resource model and add the tags in
            return CfnHelper.convertResourceSetToCFNResourceModel(
                    response.resourceSet(),
                    Collections.emptySet(),
                    FmsHelper.convertCFNTagMapToFMSTagSet(request.getDesiredResourceTags()));
        } catch (Exception e) {
            // if any code fails, delete the resourceSet since CloudFormation is unaware of it
            DeleteResourceSetRequest deleteResourceSetRequest = DeleteResourceSetRequest.builder()
                    .identifier(response.resourceSet().id())
                    .build();
            proxy.injectCredentialsAndInvokeV2(deleteResourceSetRequest, client::deleteResourceSet);

            // raise an internal exception so CloudFormation knows resourceSet creation failed
            throw new CfnInternalFailureException(e);
        }
    }
}
