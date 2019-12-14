package software.amazon.fms.notificationchannel;

import org.mockito.ArgumentMatchers;
import software.amazon.awssdk.services.fms.model.*;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    private CreateHandler handler;
    private String sampleSnsTopicArn;
    private String sampleSnsRoleName;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        handler = new CreateHandler();
        sampleSnsTopicArn = "arn:aws:sns:us-east-1:012345678901:test-topic";
        sampleSnsRoleName = "arn:aws:iam::012345678901:role/aws-service-role/fms.amazonaws.com/AWSServiceRoleForFMS";

        // model the expected post-request resource state
        model = ResourceModel.builder()
                .snsTopicArn(sampleSnsTopicArn)
                .snsRoleName(sampleSnsRoleName)
                .build();
    }

    @Test
    public void handleRequestSuccess() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder().build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the create request
        final PutNotificationChannelResponse describePutResponse = PutNotificationChannelResponse.builder().build();
        doReturn(describePutResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the create request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

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
    public void handleRequestReadResourceAlreadyExists() {
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

        // create the create request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.AlreadyExists);
    }

    @Test
    public void handleRequestCreateNotFoundException() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder().build();
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

        // create the create request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

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
    public void handleRequestCreateInvalidOperationException() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder().build();
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

        // create the create request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

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
    public void handleRequestCreateFmsException() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder().build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // mock a FmsException from the FMS API
        doThrow(FmsException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(PutNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the create request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }
}