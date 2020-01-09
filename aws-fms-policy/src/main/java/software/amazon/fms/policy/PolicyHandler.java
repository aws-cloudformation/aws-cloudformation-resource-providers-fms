package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.InvalidInputException;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.InvalidTypeException;
import software.amazon.awssdk.services.fms.model.LimitExceededException;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;

abstract class PolicyHandler<ResponseT> extends BaseHandler<CallbackContext> {

    /** FMS client instance to make requests on behalf of CloudFormation. */
    protected final FmsClient client;

    /** Constructor. */
    PolicyHandler() {

        client = FmsClient.create();
    }

    /**
     * Hook called by handleRequest to make the primary action (create, read, etc..) request on the FMS API.
     * @param proxy AWS proxy to make requests.
     * @param request CloudFormation's handler request.
     * @return Response from the FMS API.
     */
    protected abstract ResponseT makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request);

    /**
     * Hook called by handleRequest to build the resource state after a successful makeRequest call.
     * @param response Generic type request response from makeRequest call.
     * @param request CloudFormation's handler request.
     * @return Post-action resource state or null.
     */
    ResourceModel constructSuccessResourceModel(ResponseT response, ResourceHandlerRequest<ResourceModel> request) {

        return null;
    }

    /**
     * Hook called by handleRequest to build the multi-resource state after a successful makeRequest call.
     * @param response Generic type request response from makeRequest call.
     * @return Post-action multi-resource state or null.
     */
    List<ResourceModel> constructSuccessResourceModels(ResponseT response) {

        return null;
    }

    /**
     * Hook called by CloudFormation to run resource management actions.
     * @param proxy AWS proxy to make requests.
     * @param request CloudFormation's requested resource state.
     * @param callbackContext Resource's post-action context.
     * @param logger CloudWatch logger.
     * @return Success event with new resource state or failure event with handler error code.
     */
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        ResponseT response;
        try {
            // make the primary handler request
            response = makeRequest(proxy, request);
        } catch(ResourceNotFoundException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.NotFound, null);
        } catch(InvalidOperationException | InvalidInputException | InvalidTypeException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.InvalidRequest, null);
        } catch(LimitExceededException e) {
            return ProgressEvent.failed(null, callbackContext, HandlerErrorCode.ServiceLimitExceeded, null);
        }

        // let each handler construct its own success resource model
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(OperationStatus.SUCCESS)
                .resourceModel(constructSuccessResourceModel(response, request))
                .resourceModels(constructSuccessResourceModels(response))
                .callbackContext(callbackContext)
                .build();
    }
}
