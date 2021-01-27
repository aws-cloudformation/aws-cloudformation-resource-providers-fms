package software.amazon.fms.policy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.FmsRequest;
import software.amazon.awssdk.services.fms.model.InternalErrorException;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.InvalidTypeException;
import software.amazon.awssdk.services.fms.model.ListPoliciesRequest;
import software.amazon.awssdk.services.fms.model.ListPoliciesResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.fms.policy.helpers.CfnSampleHelper;
import software.amazon.fms.policy.helpers.FmsSampleHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {
    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private FmsClient client;

    @Mock
    private Logger logger;

    @Captor
    private ArgumentCaptor<FmsRequest> captor;

    private Configuration configuration;
    private ListHandler handler;

    @BeforeEach
    void setup() {

        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        configuration = new Configuration();
        handler = new ListHandler(client);
    }

    @Test
    void handleRequestWithoutNextTokenSuccess() {

        // stub the response for the list request
        final ListPoliciesResponse describeResponse = FmsSampleHelper.sampleListPolicies(null);
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListPoliciesRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);
        final ResourceModel expectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);
        expectedModel.setExcludeResourceTags(null);
        expectedModel.setSecurityServicePolicyData(SecurityServicePolicyData.builder().managedServiceData(null).type(expectedModel.getSecurityServicePolicyData().getType()).build());

        // create the read request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );

        ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder().nextToken(null)
                .maxResults(50)
                .build();
        assertThat(captor.getValue()).isEqualTo(listPoliciesRequest);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels().size()).isEqualTo(1);
        assertThat(response.getResourceModels().get(0)).isEqualTo(expectedModel);
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestWithNextTokenSuccess() {

        final String requestToken = "requestToken";
        final String returnToken = "returnToken";
        // stub the response for the list request
        final ListPoliciesResponse describeResponse = FmsSampleHelper.sampleListPolicies(returnToken);
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListPoliciesRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);
        final ResourceModel expectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);
        expectedModel.setExcludeResourceTags(null);
        expectedModel.setSecurityServicePolicyData(SecurityServicePolicyData.builder().managedServiceData(null).type(expectedModel.getSecurityServicePolicyData().getType()).build());

        // create the list request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .nextToken(requestToken)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );

        ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder().nextToken(requestToken)
                .maxResults(50)
                .build();
        assertThat(captor.getValue()).isEqualTo(listPoliciesRequest);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels().size()).isEqualTo(1);
        assertThat(response.getResourceModels().get(0)).isEqualTo(expectedModel);
        assertThat(response.getNextToken()).isEqualTo(returnToken);
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestEmptyResponseSuccess() {

        // stub the response for the list request
        final ListPoliciesResponse describeResponse = ListPoliciesResponse.builder()
                .nextToken(null)
                .build();
        doReturn(describeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListPoliciesRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);

        // create the read request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );

        ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder().nextToken(null)
                .maxResults(50)
                .build();
        assertThat(captor.getValue()).isEqualTo(listPoliciesRequest);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels().size()).isEqualTo(0);
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    void handleRequestWithInvalidOperationException() {

        // stub the response for the list request
        doThrow(InvalidOperationException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListPoliciesRequest.class),
                        ArgumentMatchers.any()
                );

        // model the pre-request and post-request resource state
        final ResourceModel requestModel = CfnSampleHelper.sampleBareResourceModel(true);
        final ResourceModel expectedModel = CfnSampleHelper.sampleRequiredParametersResourceModel(true, false, false);
        expectedModel.setExcludeResourceTags(null);
        expectedModel.setSecurityServicePolicyData(SecurityServicePolicyData.builder().managedServiceData(null).type(expectedModel.getSecurityServicePolicyData().getType()).build());

        // create the list request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(requestModel)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // verify stub calls
        verify(proxy).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );

        ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder().nextToken(null)
                .maxResults(50)
                .build();
        // assertions
        assertThat(captor.getAllValues().get(0)).isEqualTo(listPoliciesRequest);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getNextToken()).isEqualTo(null);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

    @Test
    void handleRequestInvalidTypeException() {

        // mock an InvalidTypeException from the FMS API
        doThrow(InvalidTypeException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListPoliciesRequest.class),
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
        verify(proxy).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder().nextToken(null)
                .maxResults(50)
                .build();
        assertThat(captor.getValue()).isEqualTo(listPoliciesRequest);

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

        // mock an InvalidTypeException from the FMS API
        doThrow(InternalErrorException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(ListPoliciesRequest.class),
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
        verify(proxy).injectCredentialsAndInvokeV2(
                captor.capture(),
                ArgumentMatchers.any()
        );
        ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder().nextToken(null)
                .maxResults(50)
                .build();
        assertThat(captor.getValue()).isEqualTo(listPoliciesRequest);

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
