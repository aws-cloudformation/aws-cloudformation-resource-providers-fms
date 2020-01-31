package software.amazon.fms.policy;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import software.amazon.awssdk.services.fms.model.FmsRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.awssdk.services.fms.model.InvalidInputException;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.InvalidTypeException;
import software.amazon.awssdk.services.fms.model.LimitExceededException;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.fms.model.PutPolicyRequest;
import software.amazon.awssdk.services.fms.model.PutPolicyResponse;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import software.amazon.fms.policy.helpers.FmsSampleHelper;
import software.amazon.fms.policy.helpers.CfnSampleHelper;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

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
        handler = new UpdateHandler();
    }

    @Test
    void handleRequestRequiredParametersSuccess() {

        // stub the response for the read request
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyRequiredParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutPolicyResponse describePutResponse = FmsSampleHelper.samplePutPolicyRequiredParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list tags request
        final ListTagsForResourceResponse describeListResponse =
                FmsSampleHelper.sampleListTagsForResourceResponse(false, false);
        doReturn(describeListResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListTagsForResourceRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestExpectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);

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
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true, false, false),
                FmsSampleHelper.sampleListTagsForResourceRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(requestExpectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestAllParametersSuccess() {

        // stub the response for the read request
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutPolicyResponse describePutResponse = FmsSampleHelper.samplePutPolicyAllParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list tags request
        final ListTagsForResourceResponse describeListResponse =
                FmsSampleHelper.sampleListTagsForResourceResponse(false, false);
        doReturn(describeListResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListTagsForResourceRequest.class),
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
        verify(proxy, times(3)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyAllParametersRequest(true),
                FmsSampleHelper.sampleListTagsForResourceRequest()
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
    void handlerRequestDeletePolicyTags() {

        // stub the response for the read request
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyRequiredParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutPolicyResponse describePutResponse = FmsSampleHelper.samplePutPolicyRequiredParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list tags request
        final ListTagsForResourceResponse describeListResponse =
                FmsSampleHelper.sampleListTagsForResourceResponse(true, false);
        doReturn(describeListResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListTagsForResourceRequest.class),
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
        final ResourceModel requestExpectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestExpectedModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(4)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true, false, false),
                FmsSampleHelper.sampleListTagsForResourceRequest(),
                FmsSampleHelper.sampleUntagResourceRequest(true, false)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(requestExpectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handlerRequestAddPolicyTags() {

        // stub the response for the read request
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyRequiredParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutPolicyResponse describePutResponse = FmsSampleHelper.samplePutPolicyRequiredParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list tags request
        final ListTagsForResourceResponse describeListResponse =
                FmsSampleHelper.sampleListTagsForResourceResponse(false, false);
        doReturn(describeListResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListTagsForResourceRequest.class),
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

        // model the pre-request and post-request resource state
        final ResourceModel requestExpectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, true, false);

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
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true, false, false),
                FmsSampleHelper.sampleListTagsForResourceRequest(),
                FmsSampleHelper.sampleTagResourceRequest(true, false)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(requestExpectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handlerRequestAddDeletePolicyTags() {

        // stub the response for the read request
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyRequiredParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutPolicyResponse describePutResponse = FmsSampleHelper.samplePutPolicyRequiredParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the list tags request
        final ListTagsForResourceResponse describeListResponse =
                FmsSampleHelper.sampleListTagsForResourceResponse(true, false);
        doReturn(describeListResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListTagsForResourceRequest.class),
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

        // model the pre-request and post-request resource state
        final ResourceModel requestExpectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, true);

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
        verify(proxy, times(5)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true, false, false),
                FmsSampleHelper.sampleListTagsForResourceRequest(),
                FmsSampleHelper.sampleUntagResourceRequest(true, false),
                FmsSampleHelper.sampleTagResourceRequest(false, true)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(requestExpectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestResourceNotFoundException() {

        // stub the response for the read request
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // mock a ResourceNotFoundException from the FMS API
        doThrow(ResourceNotFoundException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);

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
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true, false, false)
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
    void handleRequestInvalidOperationException() {

        // stub the response for the read request
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // mock an InvalidOperationException from the FMS API
        doThrow(InvalidOperationException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);

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
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true, false, false)
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
    void handleRequestInvalidInputException() {

        // stub the response for the read request
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // mock an InvalidInputException from the FMS API
        doThrow(InvalidInputException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);

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
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true, false, false)
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
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // mock an InvalidTypeException from the FMS API
        doThrow(InvalidTypeException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);

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
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true, false, false)
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
    void handleRequestLimitExceededException() {

        // stub the response for the read request
        final GetPolicyResponse describeGetResponse = FmsSampleHelper.sampleGetPolicyAllParametersResponse();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // mock a LimitExceededException from the FMS API
        doThrow(LimitExceededException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);

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
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true, false, false)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceLimitExceeded);
    }
}
