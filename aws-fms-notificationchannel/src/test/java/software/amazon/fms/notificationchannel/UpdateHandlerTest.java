package software.amazon.fms.notificationchannel;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.fms.model.FmsRequest;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.PutNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.PutNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    private UpdateHandler handler;
    private String sampleSnsTopicArn;
    private String sampleSnsRoleName;
    private ResourceModel model;

    @BeforeEach
    void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        handler = new UpdateHandler();
        sampleSnsTopicArn = "arn:aws:sns:us-east-1:012345678901:test-topic";
        sampleSnsRoleName = "arn:aws:iam::012345678901:role/aws-service-role/fms.amazonaws.com/AWSServiceRoleForFMS";

        // model the expected post-request resource state
        model = ResourceModel.builder()
                .snsTopicArn(sampleSnsTopicArn)
                .snsRoleName(sampleSnsRoleName)
                .build();
    }

    @Test
    void handleRequestSuccess() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder()
                .snsTopicArn(sampleSnsTopicArn)
                .snsRoleName(sampleSnsRoleName)
                .build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the update request
        final PutNotificationChannelResponse describePutResponse = PutNotificationChannelResponse.builder().build();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(captor.capture(), any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                GetNotificationChannelRequest.builder().build(),
                PutNotificationChannelRequest.builder()
                        .snsTopicArn(sampleSnsTopicArn)
                        .snsRoleName(sampleSnsRoleName)
                        .build()
        ));

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestReadResourceNotFound() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder().build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(1)).injectCredentialsAndInvokeV2(captor.capture(), any());
        assertThat(captor.getValue()).isEqualTo(GetNotificationChannelRequest.builder().build());

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
    void handleRequestUpdateNotFoundException() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder()
                .snsTopicArn(sampleSnsTopicArn)
                .snsRoleName(sampleSnsRoleName)
                .build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // mock a ResourceNotFoundException from the FMS API
        doThrow(ResourceNotFoundException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(captor.capture(), any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                GetNotificationChannelRequest.builder().build(),
                PutNotificationChannelRequest.builder()
                        .snsTopicArn(sampleSnsTopicArn)
                        .snsRoleName(sampleSnsRoleName)
                        .build()
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
    void handleRequestUpdateInvalidOperationException() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder()
                .snsTopicArn(sampleSnsTopicArn)
                .snsRoleName(sampleSnsRoleName)
                .build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // mock a InvalidOperationException from the FMS API
        doThrow(InvalidOperationException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the update request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy, times(2)).injectCredentialsAndInvokeV2(captor.capture(), any());
        assertThat(captor.getAllValues()).isEqualTo(Arrays.asList(
                GetNotificationChannelRequest.builder().build(),
                PutNotificationChannelRequest.builder()
                        .snsTopicArn(sampleSnsTopicArn)
                        .snsRoleName(sampleSnsRoleName)
                        .build()
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
}
