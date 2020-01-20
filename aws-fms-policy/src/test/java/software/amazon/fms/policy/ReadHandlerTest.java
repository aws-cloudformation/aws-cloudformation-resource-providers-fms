package software.amazon.fms.policy;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import software.amazon.awssdk.services.fms.model.FmsRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyRequest;
import software.amazon.awssdk.services.fms.model.GetPolicyResponse;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.InvalidTypeException;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.fms.model.ListTagsForResourceResponse;
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
class ReadHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    @Captor
    private ArgumentCaptor<FmsRequest> captor;

    private ReadHandler handler;

    @BeforeEach
    void setup() {

        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        handler = new ReadHandler();
    }

    @Test
    void handleRequestRequiredParametersSuccess() {

        // stub the response for the read request
        final GetPolicyResponse describeResponse = FmsSampleHelper.sampleGetPolicyRequiredParametersResponse();
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
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
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);
        final ResourceModel expectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);

        // create the read request and send it
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
                FmsSampleHelper.sampleListTagsForResourceRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestAllParametersSuccess() {

        // stub the response for the read request
        final GetPolicyResponse describeResponse = FmsSampleHelper.sampleGetPolicyAllParametersResponse();
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
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
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);
        final ResourceModel expectedModel = CfnSampleHelper.sampleAllParametersResourceModel(true, false, false);

        // create the read request and send it
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
                FmsSampleHelper.sampleListTagsForResourceRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestRetrievePolicyTags() {

        // stub the response for the read request
        final GetPolicyResponse describeResponse = FmsSampleHelper.sampleGetPolicyRequiredParametersResponse();
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
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

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);
        final ResourceModel expectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, true, false);

        // create the read request and send it
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
                FmsSampleHelper.sampleListTagsForResourceRequest()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestResourceNotFoundException() {

        // mock a ResourceNotFoundException from the FMS API
        doThrow(ResourceNotFoundException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);

        // create the read request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(1)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getValue()).isEqualTo(
                FmsSampleHelper.sampleGetPolicyRequest()
        );

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

        // mock an InvalidOperationException from the FMS API
        doThrow(InvalidOperationException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);

        // create the read request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(1)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getValue()).isEqualTo(
                FmsSampleHelper.sampleGetPolicyRequest()
        );

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

        // mock an InvalidTypeException from the FMS API
        doThrow(InvalidTypeException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);

        // create the read request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(1)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getValue()).isEqualTo(
                FmsSampleHelper.sampleGetPolicyRequest()
        );

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
}
