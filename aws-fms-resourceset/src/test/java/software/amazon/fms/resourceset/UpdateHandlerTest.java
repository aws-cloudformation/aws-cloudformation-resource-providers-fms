package software.amazon.fms.resourceset;

import java.util.Arrays;
import java.util.Map;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.BatchAssociateResourceRequest;
import software.amazon.awssdk.services.fms.model.BatchAssociateResourceResponse;
import software.amazon.awssdk.services.fms.model.BatchDisassociateResourceRequest;
import software.amazon.awssdk.services.fms.model.BatchDisassociateResourceResponse;
import software.amazon.awssdk.services.fms.model.FmsRequest;
import software.amazon.awssdk.services.fms.model.GetResourceSetRequest;
import software.amazon.awssdk.services.fms.model.GetResourceSetResponse;
import software.amazon.awssdk.services.fms.model.InternalErrorException;
import software.amazon.awssdk.services.fms.model.InvalidInputException;
import software.amazon.awssdk.services.fms.model.InvalidTypeException;
import software.amazon.awssdk.services.fms.model.ListResourceSetResourcesRequest;
import software.amazon.awssdk.services.fms.model.ListResourceSetResourcesResponse;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.fms.model.PutResourceSetRequest;
import software.amazon.awssdk.services.fms.model.PutResourceSetResponse;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import software.amazon.awssdk.services.fms.model.TagResourceRequest;
import software.amazon.awssdk.services.fms.model.TagResourceResponse;
import software.amazon.awssdk.services.fms.model.UntagResourceRequest;
import software.amazon.awssdk.services.fms.model.UntagResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.fms.resourceset.helpers.CfnSampleHelper;
import software.amazon.fms.resourceset.helpers.FmsSampleHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private FmsClient client;

    @Mock
    private Logger logger;

    @Captor
    private ArgumentCaptor<FmsRequest> captor;

    private Configuration configuration;
    private UpdateHandler handler;

    @BeforeEach
    void setup() {

        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        configuration = new Configuration();
        handler = new UpdateHandler(client);
    }

    @Test
    void handleRequestRequiredParametersSuccess() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse =
                FmsSampleHelper.sampleGetResourceSetRequiredParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutResourceSetResponse describePutResponse =
                FmsSampleHelper.samplePutResourceSetRequiredParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list resourceSet resources request
        final ListResourceSetResourcesResponse describeListResourceSetResourcesResponse =
                FmsSampleHelper.sampleListResourceSetResourcesResponseEmptyResource();
        doReturn(describeListResourceSetResourcesResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListResourceSetResourcesRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestExpectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false, false);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestExpectedModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(3)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetRequiredParametersRequest(true, false, false),
                FmsSampleHelper.sampleListResourceSetResourcesRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestBatchAssociate() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse =
                FmsSampleHelper.sampleGetResourceSetAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutResourceSetResponse describePutResponse =
                FmsSampleHelper.samplePutResourceSetAllParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list resourceSet resources request
        final ListResourceSetResourcesResponse describeListResourceSetResourcesResponse =
                FmsSampleHelper.sampleListResourceSetResourcesResponseEmptyResource();
        doReturn(describeListResourceSetResourcesResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListResourceSetResourcesRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the batch associate request
        final BatchAssociateResourceResponse describeAssociateResponse =
                FmsSampleHelper.sampleBatchAssociateResourceResponse(false);
        doReturn(describeAssociateResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(BatchAssociateResourceRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleAllParametersResourceModel(true, false, false);
        final ResourceModel expectedModel = CfnSampleHelper.sampleAllParametersResourceModel(true, false, false);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(4)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetAllParametersRequest(true),
                FmsSampleHelper.sampleListResourceSetResourcesRequest(),
                FmsSampleHelper.sampleBatchAssociateResourceRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestAllParametersBatchDisassociate() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse =
                FmsSampleHelper.sampleGetResourceSetAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutResourceSetResponse describePutResponse =
                FmsSampleHelper.samplePutResourceSetAllParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list resourceSet resources request
        final ListResourceSetResourcesResponse describeListResourceSetResourcesResponse =
                FmsSampleHelper.sampleListResourceSetResourcesResponseMultipleResources();
        doReturn(describeListResourceSetResourcesResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListResourceSetResourcesRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the batch disassociate request
        final BatchDisassociateResourceResponse describeDisassociateResponse =
                FmsSampleHelper.sampleBatchDisassociateResourceResponse(false);
        doReturn(describeDisassociateResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(BatchDisassociateResourceRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleAllParametersResourceModel(true, false, false);
        final ResourceModel expectedModel = CfnSampleHelper.sampleAllParametersResourceModel(true, false, false);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(4)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetAllParametersRequest(true),
                FmsSampleHelper.sampleListResourceSetResourcesRequest(),
                FmsSampleHelper.sampleBatchDisassociateResourceRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handlerRequestDeleteResourceSetTags() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse = FmsSampleHelper.sampleGetResourceSetRequiredParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutResourceSetResponse describePutResponse = FmsSampleHelper.samplePutResourceSetRequiredParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list resourceSet resources request
        final ListResourceSetResourcesResponse describeListResourceSetResourcesResponse =
                FmsSampleHelper.sampleListResourceSetResourcesResponseEmptyResource();
        doReturn(describeListResourceSetResourcesResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListResourceSetResourcesRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the untag resource request
        final UntagResourceResponse describeUntagResponse = FmsSampleHelper.sampleUntagResourceResponse();
        doReturn(describeUntagResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(UntagResourceRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestExpectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false, false);
        final ResourceModel previousModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, true, false);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestExpectedModel)
                .previousResourceState(previousModel)
                .previousResourceTags(FmsSampleHelper.generateSampleResourceTags(true, false))
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(4)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetRequiredParametersRequest(true, false, false),
                FmsSampleHelper.sampleUntagResourceRequest(true, false),
                FmsSampleHelper.sampleListResourceSetResourcesRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handlerRequestAddResourceSetTags() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse = FmsSampleHelper.sampleGetResourceSetRequiredParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutResourceSetResponse describePutResponse = FmsSampleHelper.samplePutResourceSetRequiredParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the tag resource request
        final TagResourceResponse describeTagResponse = FmsSampleHelper.sampleTagResourceResponse();
        doReturn(describeTagResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(TagResourceRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list resourceSet resources request
        final ListResourceSetResourcesResponse describeListResourceSetResourcesResponse =
                FmsSampleHelper.sampleListResourceSetResourcesResponseEmptyResource();
        doReturn(describeListResourceSetResourcesResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListResourceSetResourcesRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestExpectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, true, false);

        // create sample tags how cfn interprets them from the resource model
        final Map<String, String> tags = configuration.resourceDefinedTags(requestExpectedModel);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestExpectedModel)
                .desiredResourceTags(tags)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(4)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetRequiredParametersRequest(true, false, false),
                FmsSampleHelper.sampleTagResourceRequest(true, false),
                FmsSampleHelper.sampleListResourceSetResourcesRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
//        assertThat(response.getResourceModel()).isEqualTo(requestExpectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handlerRequestAddDeleteResourceSetTags() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse = FmsSampleHelper.sampleGetResourceSetRequiredParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutResourceSetResponse describePutResponse = FmsSampleHelper.samplePutResourceSetRequiredParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the untag resource request
        final UntagResourceResponse describeUntagResponse = FmsSampleHelper.sampleUntagResourceResponse();
        doReturn(describeUntagResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(UntagResourceRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the tag resource request
        final TagResourceResponse describeTagResponse = FmsSampleHelper.sampleTagResourceResponse();
        doReturn(describeTagResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(TagResourceRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list resourceSet resources request
        final ListResourceSetResourcesResponse describeListResourceSetResourcesResponse =
                FmsSampleHelper.sampleListResourceSetResourcesResponseEmptyResource();
        doReturn(describeListResourceSetResourcesResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListResourceSetResourcesRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestExpectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false, true);
        final ResourceModel previousModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, true, false);

        // create sample tags how cfn interprets them from the resource model
        final Map<String, String> tags = configuration.resourceDefinedTags(requestExpectedModel);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestExpectedModel)
                .desiredResourceTags(tags)
                .previousResourceState(previousModel)
                .previousResourceTags(FmsSampleHelper.generateSampleResourceTags(true, false))
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(5)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetRequiredParametersRequest(true, false, false),
                FmsSampleHelper.sampleUntagResourceRequest(true, false),
                FmsSampleHelper.sampleTagResourceRequest(false, true),
                FmsSampleHelper.sampleListResourceSetResourcesRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestResourceNotFoundException() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse = FmsSampleHelper.sampleGetResourceSetAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // mock a ResourceNotFoundException from the FMS API
        doThrow(ResourceNotFoundException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false, false);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetRequiredParametersRequest(true, false, false)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    void handleRequestInvalidInputException() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse = FmsSampleHelper.sampleGetResourceSetAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // mock an InvalidInputException from the FMS API
        doThrow(InvalidInputException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false, false);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetRequiredParametersRequest(true, false, false)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

    @Test
    void handleRequestInvalidTypeException() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse = FmsSampleHelper.sampleGetResourceSetAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // mock an InvalidTypeException from the FMS API
        doThrow(InvalidTypeException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false, false);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetRequiredParametersRequest(true, false, false)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

    @Test
    void handleRequestInternalErrorException() {

        // stub the response for the read request
        final GetResourceSetResponse describeGetResponse = FmsSampleHelper.sampleGetResourceSetAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // mock a LimitExceededException from the FMS API
        doThrow(InternalErrorException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false, false);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetResourceSetRequest(),
                FmsSampleHelper.samplePutResourceSetRequiredParametersRequest(true, false, false)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }
}
