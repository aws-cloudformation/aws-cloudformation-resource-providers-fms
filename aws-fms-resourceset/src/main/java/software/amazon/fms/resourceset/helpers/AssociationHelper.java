package software.amazon.fms.resourceset.helpers;

import com.google.common.collect.Iterables;
import software.amazon.awssdk.services.fms.FmsClient;
import software.amazon.awssdk.services.fms.model.BatchAssociateResourceRequest;
import software.amazon.awssdk.services.fms.model.BatchAssociateResourceResponse;
import software.amazon.awssdk.services.fms.model.BatchDisassociateResourceRequest;
import software.amazon.awssdk.services.fms.model.BatchDisassociateResourceResponse;
import software.amazon.awssdk.services.fms.model.ListResourceSetResourcesRequest;
import software.amazon.awssdk.services.fms.model.ListResourceSetResourcesResponse;
import software.amazon.awssdk.services.fms.model.Resource;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AssociationHelper {
    private static final int MAX_ASSOCIATION_CHANGES_PER_REQUEST = 100;

    /**
     * Update a resource set to use a desired set of resources.
     * @param resourceSetId Resource set to update.
     * @param desiredAssociations Resources that should be associated to the resource set.
     * @param fmsClient FMS fmsClient.
     * @param proxy CFN proxy.
     * @param logger CloudWatch logger.
     */
    public static void updateResourceAssociations(
            final String resourceSetId,
            final Set<String> desiredAssociations,
            final FmsClient fmsClient,
            final AmazonWebServicesClientProxy proxy,
            final Logger logger
    ) {
        // list all the resources currently associated with the resource set
        final Set<String> currentAssociations = listResourceAssociations(resourceSetId, fmsClient, proxy);

        // calculate the resources that need to be disassociated from the resource set
        final Set<String> resourcesToDisassociate = calculateResourcesToDisassociate(
                currentAssociations,
                desiredAssociations
        );

        // calculate the resources that need to be associated to the resource set
        final Set<String> resourcesToAssociate = calculateResourcesToAssociate(
                currentAssociations,
                desiredAssociations
        );

        // disassociate the resources from the resource set
        batchDisassociateResources(resourceSetId, resourcesToDisassociate, fmsClient, proxy, logger);

        // associate the resources to the resource set
        batchAssociateResources(resourceSetId, resourcesToAssociate, fmsClient, proxy, logger);
    }

    private static Set<String> listResourceAssociations(
            final String resourceSetId,
            final FmsClient fmsClient,
            final AmazonWebServicesClientProxy proxy
    ) {
        final List<Resource> resources = new ArrayList<>();
        String nextToken = null;
        do {
            ListResourceSetResourcesRequest resourceSetResourcesRequest = ListResourceSetResourcesRequest.builder()
                    .identifier(resourceSetId)
                    .nextToken(nextToken)
                    .build();

            ListResourceSetResourcesResponse resourceSetResourcesResponse = proxy.injectCredentialsAndInvokeV2(
                    resourceSetResourcesRequest,
                    fmsClient::listResourceSetResources);

            resources.addAll(resourceSetResourcesResponse.items());
            nextToken = resourceSetResourcesResponse.nextToken();
        } while (nextToken != null);

        return resources.stream().map(Resource::uri).collect(Collectors.toSet());
    }

    private static Set<String> calculateResourcesToDisassociate(
            final Set<String> currentAssociations,
            final Set<String> desiredAssociations
    ) {
        if (desiredAssociations == null || desiredAssociations.isEmpty()) {
            return currentAssociations;
        }

        // determine resources to remove
        return currentAssociations.stream()
                .filter(resource -> !desiredAssociations.contains(resource))
                .collect(Collectors.toSet());
    }

    private static Set<String> calculateResourcesToAssociate(
            final Set<String> currentAssociations,
            final Set<String> desiredAssociations
    ) {
        if (desiredAssociations == null || desiredAssociations.isEmpty()) {
            return new HashSet<>();
        }

        // determine resources to add
        return desiredAssociations.stream()
                .filter(resource -> !currentAssociations.contains(resource))
                .collect(Collectors.toSet());
    }

    private static void batchAssociateResources(
            final String resourceSetId,
            final Set<String> resources,
            final FmsClient fmsClient,
            final AmazonWebServicesClientProxy proxy,
            final Logger logger
    ) {
        if (resources.isEmpty()) {
            logger.log("No resources to associate");
            return;
        }

        logger.log(String.format("Associating %d resources/s", resources.size()));
        final BatchAssociateResourceRequest associateRequest = BatchAssociateResourceRequest.builder()
                .resourceSetIdentifier(resourceSetId)
                .items(resources)
                .build();
        proxy.injectCredentialsAndInvokeV2(
                associateRequest,
                fmsClient::batchAssociateResource);
        logger.log("Resources associated successfully");
    }

    private static void batchDisassociateResources(
            final String resourceSetId,
            final Set<String> resources,
            final FmsClient fmsClient,
            final AmazonWebServicesClientProxy proxy,
            final Logger logger
    ) {
        // make a disassociate request
        if (resources.isEmpty()) {
            logger.log("No resources to disassociate");
            return;
        }

        logger.log(String.format("Disassociating %d resources/s", resources.size()));
        final BatchDisassociateResourceRequest disassociateRequest = BatchDisassociateResourceRequest.builder()
                .resourceSetIdentifier(resourceSetId)
                .items(resources)
                .build();
        proxy.injectCredentialsAndInvokeV2(
                disassociateRequest,
                fmsClient::batchDisassociateResource);
        logger.log("Resources disassociated successfully");
    }
}
