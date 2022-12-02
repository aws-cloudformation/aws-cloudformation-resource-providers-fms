package software.amazon.fms.resourceset;

import java.util.Map;
import java.util.stream.Collectors;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-fms-resourceset.json");
    }

    /**
     * Extract tags from resource model to be included in the resource provider request.
     * @param resourceModel The request resource model with user defined tags.
     * @return A map of key/value pairs representing tags from the request resource model.
     */
    public Map<String, String> resourceDefinedTags(final ResourceModel resourceModel) {

        if (resourceModel.getTags() == null) {
            return null;
        } else {
            return resourceModel.getTags().stream().collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        }
    }
}
