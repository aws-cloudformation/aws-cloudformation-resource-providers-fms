package software.amazon.fms.resourceset;

import java.time.Duration;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.DeleteResourceSetRequest;
import software.amazon.awssdk.services.fms.model.DeleteResourceSetResponse;
import software.amazon.awssdk.services.fms.model.FmsRequest;
import software.amazon.awssdk.services.fms.model.InternalErrorException;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
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
public class DeleteHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private FmsClient client;

    @Mock
    private Logger logger;

    @Captor
    private ArgumentCaptor<FmsRequest> captor;

    private DeleteHandler handler;

    @BeforeEach
    void setup() {

        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        handler = new DeleteHandler(client);
    }

    @Test
    void handleRequestDeleteAllResourceSetDefaultSuccess() {

        // stub the response for the delete request
        final DeleteResourceSetResponse describeResponse = FmsSampleHelper.sampleDeleteResourceSetResponse();
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(DeleteResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);

        // create the delete request and send it
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
                FmsSampleHelper.sampleDeleteResourceSetRequest()
        );

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(null);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestResourceNotFoundException() {

        // mock a ResourceNotFoundException from the FMS API
        doThrow(ResourceNotFoundException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(DeleteResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);

        // create the delete request and send it
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
                FmsSampleHelper.sampleDeleteResourceSetRequest()
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
                        ArgumentMatchers.isA(DeleteResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);

        // create the delete request and send it
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
                FmsSampleHelper.sampleDeleteResourceSetRequest()
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
    void handleRequestInternalErrorException() {

        // mock an InvalidOperationException from the FMS API
        doThrow(InternalErrorException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(DeleteResourceSetRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);

        // create the delete request and send it
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
                FmsSampleHelper.sampleDeleteResourceSetRequest()
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
}
