package software.amazon.fms.resourceset.helpers;

import software.amazon.fms.resourceset.ResourceModel;
import software.amazon.fms.resourceset.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CfnSampleHelper extends BaseSampleHelper {

    /**
     * Assembles a sample resource model builder with only the required read/write parameters.
     * @param includeIdentifiers Should the ResourceSet identifiers be included.
     * @param includeTag1 Should the ResourceSet have unique tag 1.
     * @param includeTag2 Should the ResourceSet have unique tag 2.
     * @return The assembled resource model builder.
     */
    private static ResourceModel.ResourceModelBuilder sampleRequiredParametersResourceModelBuilder(
            final boolean includeIdentifiers,
            final boolean includeEmptyResourceTypeList,
            final boolean includeTag1,
            final boolean includeTag2) {

        // assemble a sample resource model with only the required parameters
        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder()
                .name(sampleResourceSetName);
        if (includeEmptyResourceTypeList) {
            resourceModelBuilder.resourceTypeList(null);
        } else {
            resourceModelBuilder.resourceTypeList((Arrays.asList(sampleResourceTypeListElement)));
        }

        // optionally include the ResourceSet id
        if (includeIdentifiers) {
            resourceModelBuilder.id(sampleResourceSetId);
        }

        // optionally include the ResourceSet tags
        List<Tag> sampleTags = new ArrayList<>();
        if (includeTag1) {
            sampleTags.add(Tag.builder()
                    .key(String.format("%s%s", sampleTagKey, "1"))
                    .value(sampleTagValue)
                    .build());
        }
        if (includeTag2) {
            sampleTags.add(Tag.builder()
                    .key(String.format("%s%s", sampleTagKey, "2"))
                    .value(sampleTagValue)
                    .build());
        }

        // dont include an empty tags list
        if (includeTag1 || includeTag2) {
            resourceModelBuilder.tags(sampleTags);
        }

        return resourceModelBuilder;
    }

    /**
     * Assembles a sample resource model with only the SampleResourceSetId parameter.
     * @param includeIdentifiers Should the ResourceSet identifiers be included.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleBareResourceModel(final boolean includeIdentifiers) {

        final ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();

        // optionally include the resourceSet id
        if (includeIdentifiers) {
            resourceModelBuilder.id(sampleResourceSetId);
        }

        return resourceModelBuilder.build();
    }

    /**
     * Assembles a sample resource model with only the required read/write parameters.
     * @param includeIdentifiers Should the ResourceSet identifiers be included.
     * @param includeTag1 Should the ResourceSet have unique tag 1.
     * @param includeTag2 Should the ResourceSet have unique tag 2.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleRequiredParametersResourceModel(
            final boolean includeIdentifiers,
            final boolean includeEmptyResourceTypeList,
            final boolean includeTag1,
            final boolean includeTag2) {

        return sampleRequiredParametersResourceModelBuilder(
                includeIdentifiers, includeEmptyResourceTypeList, includeTag1, includeTag2).build();
    }

    /**
     * Assembles a sample resource model with all possible read/write parameters.
     * @param includeIdentifiers Should the ResourceSet identifiers be included.
     * @param includeTag1 Should the ResourceSet have unique tag 1.
     * @param includeTag2 Should the ResourceSet have unique tag 2.
     * @return The assembled resource model.
     */
    public static ResourceModel sampleAllParametersResourceModel(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2) {

        return sampleAllParametersResourceModelBuilder(includeIdentifiers, includeTag1, includeTag2).build();
    }

    /**
     * Assembles a sample resource model builder with all possible read/write parameters.
     * @param includeIdentifiers Should the ResourceSet identifiers be included.
     * @param includeTag1 Should the ResourceSet have unique tag 1.
     * @param includeTag2 Should the ResourceSet have unique tag 2.
     * @return The assembled resource model builder.
     */
    private static ResourceModel.ResourceModelBuilder sampleAllParametersResourceModelBuilder(
            final boolean includeIdentifiers,
            final boolean includeTag1,
            final boolean includeTag2) {
        Set<String> resources = new HashSet<>();
        resources.add(sampleResourceUri);

        // assemble sample ResourceSet with all possible parameters
        return sampleRequiredParametersResourceModelBuilder(includeIdentifiers, false, includeTag1, includeTag2)
                .resources(resources)
                .description(sampleResourceSetDescription);
    }
}
