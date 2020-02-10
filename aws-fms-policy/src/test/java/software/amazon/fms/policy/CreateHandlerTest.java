package software.amazon.fms.policy;

import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import software.amazon.awssdk.services.fms.model.DeletePolicyRequest;
import software.amazon.awssdk.services.fms.model.DeletePolicyResponse;
import software.amazon.awssdk.services.fms.model.FmsRequest;
import software.amazon.awssdk.services.fms.model.InternalErrorException;
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
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    @Captor
    private ArgumentCaptor<FmsRequest> captor;

    private Configuration configuration;
    private CreateHandler handler;

    @BeforeEach
    void setup() {

        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        configuration = new Configuration();
        handler = new CreateHandler();
    }

    @Test
    void handleRequestRequiredParametersSuccess() {

        // stub the response for the create request
        final PutPolicyResponse describeResponse = FmsSampleHelper.samplePutPolicyRequiredParametersResponse();
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(false, false, false);
        final ResourceModel expectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);

        // create the create request and send it
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
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(false, false, false)
        );

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
    void handleRequestAllParametersSuccess() {

        // stub the response for the create request
        final PutPolicyResponse describeResponse = FmsSampleHelper.samplePutPolicyAllParametersResponse();
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleAllParametersResourceModel(false, false, false);
        final ResourceModel expectedModel = CfnSampleHelper.sampleAllParametersResourceModel(true, false, false);

        // create the create request and send it
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
                FmsSampleHelper.samplePutPolicyAllParametersRequest(false)
        );

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
    void handlerRequestPolicyTags() {

        // stub the response for the create request
        final PutPolicyResponse describeResponse = FmsSampleHelper.samplePutPolicyRequiredParametersResponse();
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(false, true, false);
        final ResourceModel expectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, true, false);

        // create sample tags how cfn interprets them from the resource model
        final Map<String, String> tags = configuration.resourceDefinedTags(requestModel);

        // create the create request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .desiredResourceTags(tags)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(1)).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        assertThat(captor.getValue()).isEqualTo(
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(false, true, false)
        );

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
    void handleRequestResourceNotFoundException() {

        // mock a ResourceNotFoundException from the FMS API
        doThrow(ResourceNotFoundException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(false, false, false);

        // create the create request and send it
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
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(false, false, false)
        );

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

        // mock an InvalidOperationException from the FMS API
        doThrow(InvalidOperationException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(false, false, false);

        // create the create request and send it
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
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(false, false, false)
        );

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

        // mock an InvalidInputException from the FMS API
        doThrow(InvalidInputException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(false, false, false);

        // create the create request and send it
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
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(false, false, false)
        );

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

        // mock an InvalidTypeException from the FMS API
        doThrow(InvalidTypeException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(false, false, false);

        // create the create request and send it
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
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(false, false, false)
        );

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

        // mock a LimitExceededException from the FMS API
        doThrow(LimitExceededException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(false, false, false);

        // create the create request and send it
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
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(false, false, false)
        );

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceLimitExceeded);
    }

    @Test
    void handleRequestInternalErrorException() {

        // mock a LimitExceededException from the FMS API
        doThrow(InternalErrorException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(false, false, false);

        // create the create request and send it
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
                FmsSampleHelper.samplePutPolicyRequiredParametersRequest(false, false, false)
        );

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }

    @Test
    void handlePostPolicyCreationException() {

        // stub the response for the create request
        final PutPolicyResponse describePutResponse = FmsSampleHelper.samplePutPolicyRequiredParametersResponse();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutPolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the delete request
        final DeletePolicyResponse describeDeleteResponse = FmsSampleHelper.sampleDeletePolicyResponse();
        doReturn(describeDeleteResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(DeletePolicyRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleRequiredParametersResourceModel(false, false, false);

        // create the create request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();

        // spy the request to throw an exception during constructSuccessResourceModel(). the exception handling being
        // tested is primarily guarding against possible errors in convertFMSPolicyToCFNResourceModel.
        // getDesiredResourceTags() is being used to throw an exception since it is not static and can be accessed
        // from this test. this ends up testing exception handling logic the same way as an exception in
        // constructSuccessResourceModel() would.
        final ResourceHandlerRequest<ResourceModel> spyRequest = Mockito.spy(request);
        when(spyRequest.getDesiredResourceTags())
                .thenReturn(request.getDesiredResourceTags())
                .thenThrow(new NullPointerException());

        // assertions
        Assertions.assertThrows(CfnInternalFailureException.class, () -> {
            handler.handleRequest(proxy, spyRequest, null, logger);

            // verify stub calls
            verify(proxy, times(1)).injectCredentialsAndInvokeV2(
                    captor.capture(),
                    ArgumentMatchers.any()
            );
            assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                    FmsSampleHelper.samplePutPolicyRequiredParametersRequest(false, false, false),
                    FmsSampleHelper.sampleDeletePolicyRequest()
            ));
        });
    }
}
