package software.amazon.fms.resourceset.helpers;

import software.amazon.awssdk.services.fms.model.ResourceSet;
import software.amazon.fms.resourceset.ResourceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CfnHelper {

    /**
     * Convert a resourceSet (from the FMS SDK) to a CFN resource models (from the resource provider).
     * @param resourceSet FMS resourceSet that was converted from.
     * @param resources FMS resources that was converted from.
     * @param tags FMS tags that was converted from.
     * @return CFN resource model that was converted to.
     */
    public static ResourceModel convertResourceSetToCFNResourceModel(
            final ResourceSet resourceSet,
            final Set<String> resources,
            final List<software.amazon.awssdk.services.fms.model.Tag> tags
    ) {

        // assemble the resource model with the required parameters
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder()
                .id(resourceSet.id())
                .name(resourceSet.name())
                .description(resourceSet.description())
                .resourceTypeList(resourceSet.resourceTypeList());

        if (resources != null && !resources.isEmpty()) {
            resourceModelBuilder.resources(resources);
        }

        if (tags != null && !tags.isEmpty()) {
            final List<software.amazon.fms.resourceset.Tag> modelTags = new ArrayList<>();
            tags.forEach(tag -> modelTags.add(new software.amazon.fms.resourceset.Tag(tag.key(), tag.value())));
            resourceModelBuilder.tags(modelTags);
        }

        // build and return the resource model
        return resourceModelBuilder.build();
    }


    /**
     * Convert a list of resourceSet summary (from the FMS SDK) to a list of CFN resource models (from the resource provider).
     * @param resourceSetSummary FMS resourceSetSummary that was converted from.
     * @return CFN resource model that was converted to.
     */
    public static ResourceModel convertResourceSetSummaryToCFNResourceModel(
            final software.amazon.awssdk.services.fms.model.ResourceSetSummary resourceSetSummary
    ) {
        // assemble the resource model with the required parameters
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder()
                .id(resourceSetSummary.id())
                .name(resourceSetSummary.name())
                .description(resourceSetSummary.description());

        // build and return the resource model
        return resourceModelBuilder.build();
    }
}
