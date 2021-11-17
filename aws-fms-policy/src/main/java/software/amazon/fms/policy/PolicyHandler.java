package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.FmsResponse;
import software.amazon.awssdk.services.fms.model.InternalErrorException;
import software.amazon.awssdk.services.fms.model.InvalidInputException;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.InvalidTypeException;
import software.amazon.awssdk.services.fms.model.LimitExceededException;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

abstract class PolicyHandler<ResponseT extends FmsResponse> extends BaseHandler<CallbackContext> {

    /** FMS client instance to make requests on behalf of CloudFormation. */
    protected final FmsClient client;

    /** Constructor for use by CloudFormation, uses default FMS client. */
    PolicyHandler() {

        client = FmsClient.create();
    }

    /**
     * Constructor for use in tests, allows for a mocked client.
     * @param client The FmsClient to use.
     */
    PolicyHandler(final FmsClient client) {

        this.client = client;
    }

    /**
     * Hook called by handleRequest to make the primary action (create, read, etc..) request on the FMS API.
     * @param proxy AWS proxy to make requests.
     * @param request CloudFormation's handler request.
     * @param logger CloudWatch logger.
     * @return Response from the FMS API.
     */
    protected abstract ResponseT makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger);

    /**
     * Hook called by handleRequest to build the ProgressEvent after a successful makeRequest call.
     * @param response Generic type request response from makeRequest call.
     * @param request CloudFormation's handler request.
     * @param proxy AWS proxy to make requests.
     * @return ProgressEvent with Post-action resource state.
     */
    abstract ProgressEvent<ResourceModel, CallbackContext> constructSuccessProgressEvent(
            final ResponseT response,
            final ResourceHandlerRequest<ResourceModel> request,
            final AmazonWebServicesClientProxy proxy);

    /**
     * Logs the requestId of an FmsResponse.
     * @param response FmsResponse to get the requestId from.
     * @param logger CloudWatch logger.
     */
    static void logRequest(final FmsResponse response, Logger logger) {

        String requestId;
        try {
             requestId = response.responseMetadata().requestId();
        } catch (NullPointerException e) {
            requestId = "null";
        }
        logger.log(String.format("%s Id: %s", response.getClass().getSimpleName(), requestId));
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

        final ResponseT response;
        try {
            // make the primary handler request
            response = makeRequest(proxy, request, logger);
        } catch(ResourceNotFoundException e) {
            logger.log(e.toString());
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        } catch(InvalidOperationException | InvalidInputException | InvalidTypeException e) {
            logger.log(e.toString());
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
        } catch(LimitExceededException e) {
            logger.log(e.toString());
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.ServiceLimitExceeded);
        } catch(InternalErrorException e) {
            logger.log(e.toString());
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.ServiceInternalError);
        }

        // let each handler construct its own success progress event with resource model(s)
        return constructSuccessProgressEvent(response, request, proxy);
    }
}
