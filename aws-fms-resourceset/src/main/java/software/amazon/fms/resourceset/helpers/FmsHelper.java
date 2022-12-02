package software.amazon.fms.resourceset.helpers;

import software.amazon.awssdk.services.fms.model.*;
import software.amazon.fms.resourceset.ResourceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FmsHelper {

    /**
     * Convert a CFN resource model (from the resource provider) to an FMS resourceSet (from the FMS SDK).
     * @param resourceModel CFN resource model that was converted from.
     * @return FMS resourceSet that was converted to.
     */
    public static ResourceSet convertCFNResourceModelToFMSResourceSet(ResourceModel resourceModel) {
        return convertCFNResourceModelToBuilder(resourceModel).build();
    }

    /**
     * Convert a CFN resource model (from the resource provider) to an FMS resourceSet (from the FMS SDK) and inject a
     * updateToken.
     * @param resourceModel CFN resource model that was converted from.
     * @param updateToken The resourceSet update token to inject into the FMS resourceSet
     * @return FMS resourceSet that was converted to with the policyUpdateToken.
     */
    public static ResourceSet convertCFNResourceModelToFMSResourceSet(
            ResourceModel resourceModel,
            String updateToken
    ) {

        return convertCFNResourceModelToBuilder(resourceModel).updateToken(updateToken).build();
    }

    /**
     * Logic for converting a CFN resource model (from the resource provider) to an FMS resourceSet (from the FMS SDK).
     * @param resourceModel CFN resource model that was converted from.
     * @return FMS resourceSet builder that was converted to.
     */
    private static ResourceSet.Builder convertCFNResourceModelToBuilder(ResourceModel resourceModel) {

        // assemble the resourceSet with the required parameters
        final ResourceSet.Builder resourceSetBuilder = ResourceSet.builder()
                .name(resourceModel.getName())
                .resourceTypeList(resourceModel.getResourceTypeList());

        // add id if present
        if (resourceModel.getId() != null) {
            resourceSetBuilder.id(resourceModel.getId());
        }

        // add description if present
        if (resourceModel.getDescription() != null) {
            resourceSetBuilder.description(resourceModel.getDescription());
        }

        // return the policy builder
        return resourceSetBuilder;
    }

    /**
     * Convert a CFN tag map to an FMS tag list.
     * @param cfnTags Tags from the CFN resource provider request.
     * @return A list of FMS tag objects.
     */
    public static List<Tag> convertCFNTagMapToFMSTagSet(Map<String, String> cfnTags) {

        // construct a new list of FMS tags
        final List<Tag> tags = new ArrayList<>();
        if (cfnTags != null) {
            cfnTags.forEach((k, v) -> tags.add(Tag.builder().key(k).value(v).build()));
        }
        return tags;
    }

    /**
     * Determine the tags that need to be removed from a policy.
     * @param existingTagList The tags that currently exist on the policy.
     * @param desiredTagList The tags that should exist on the policy.
     * @return A list of tag keys to remove from the policy.
     */
    public static List<String> tagsToRemove(List<Tag> existingTagList, Map<String, String> desiredTagList) {

        // format existing and new tags
        final List<Tag> desiredTagListFms = convertCFNTagMapToFMSTagSet(desiredTagList);

        // determine tags to remove
        return existingTagList.stream()
                .filter(tag -> !desiredTagListFms.contains(tag))
                .map(Tag::key)
                .collect(Collectors.toList());
    }

    /**
     * Determine the tags that need to be added to a policy.
     * @param existingTagList The tags that currently exist on the policy.
     * @param desiredTagList The tags that should exist on the policy.
     * @return A list of tags to add to the policy.
     */
    public static List<Tag> tagsToAdd(List<Tag> existingTagList, Map<String, String> desiredTagList) {

        // format existing and new tags
        final List<Tag> desiredTagListFms = convertCFNTagMapToFMSTagSet(desiredTagList);

        // determine tags to add
        return desiredTagListFms.stream()
                .filter(tag -> !existingTagList.contains(tag))
                .collect(Collectors.toList());
    }

    /**
     * Determine the resources that need to be disassociated from a resourceSet.
     * @param existingResourcesList The resources that currently exist on the resourceSet.
     * @param desiredResourcesList The resources that should exist on the resourceSet.
     * @return A list of resources to remove from the resourceSet.
     */
    public static List<String> resourcesToDisassociate(
            List<Resource> existingResourcesList, Set<String> desiredResourcesList
    ) {
        // format existingResourcesList
        List<String> resources = existingResourcesList.stream().map(i -> i.uri()).collect(Collectors.toList());

        if (desiredResourcesList == null || desiredResourcesList.isEmpty()) {
            return resources;
        }

        // determine resources to remove
        return resources.stream()
                .filter(resource -> !desiredResourcesList.contains(resource))
                .collect(Collectors.toList());
    }

    /**
     * Determine the resources that need to be associated to a resourceSet.
     * @param existingResourcesList The resources that currently exist on the resourceSet.
     * @param desiredResourcesList The resources that should exist on the resourceSet.
     * @return A list of resources to associate to the resourceSet.
     */
    public static List<String> resourcesToAssociate(
            List<Resource> existingResourcesList, Set<String> desiredResourcesList
    ) {
        // format existingResourcesList
        Set<String> resources = existingResourcesList.stream().map(i -> i.uri()).collect(Collectors.toSet());

        if (desiredResourcesList == null || desiredResourcesList.isEmpty()) {
            return new ArrayList<String>();
        }

        // determine resources to remove
        return desiredResourcesList.stream()
                .filter(resource -> !resources.contains(resource))
                .collect(Collectors.toList());
    }
}
