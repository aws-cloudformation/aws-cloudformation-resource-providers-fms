package software.amazon.fms.policy;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.awssdk.services.fms.model.InvalidInputException;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.InvalidTypeException;
import software.amazon.awssdk.services.fms.model.LimitExceededException;
import software.amazon.awssdk.services.fms.model.PutPolicyRequest;
import software.amazon.awssdk.services.fms.model.PutPolicyResponse;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.policy.helpers.FmsSampleHelper;
import software.amazon.fms.policy.helpers.CfnSampleHelper;

import java.util.Arrays;

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
    private ArgumentCaptor<PutPolicyRequest> captor;

    private UpdateHandler handler;

    @BeforeEach
    void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
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

        // model the expected post-request resource state
        ResourceModel model = CfnSampleHelper.sampleRequiredParametersResourceModel(true);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(true)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
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

        // model the expected post-request resource state
        ResourceModel model = CfnSampleHelper.sampleAllParametersResourceModel(true);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyAllParametersRequest(true)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
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

        // model the expected post-request resource state
        ResourceModel model = CfnSampleHelper.sampleAllParametersResourceModel(true);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyAllParametersRequest(true)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
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

        // model the expected post-request resource state
        ResourceModel model = CfnSampleHelper.sampleAllParametersResourceModel(true);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyAllParametersRequest(true)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
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

        // model the expected post-request resource state
        ResourceModel model = CfnSampleHelper.sampleAllParametersResourceModel(true);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyAllParametersRequest(true)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
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

        // model the expected post-request resource state
        ResourceModel model = CfnSampleHelper.sampleAllParametersResourceModel(true);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyAllParametersRequest(true)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
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

        // model the expected post-request resource state
        ResourceModel model = CfnSampleHelper.sampleAllParametersResourceModel(true);

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                FmsSampleHelper.sampleGetPolicyRequest(),
                FmsSampleHelper.samplePutPolicyAllParametersRequest(true)
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceLimitExceeded);
    }
}
