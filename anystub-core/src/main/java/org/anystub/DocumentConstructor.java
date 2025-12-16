package org.anystub;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.Map;

/**
 * helper class to deserialize document
 */
public class DocumentConstructor extends Constructor {
    public DocumentConstructor(LoaderOptions options) {
        super(options);
        this.yamlConstructors.put(Tag.MAP, new ConstructDocument());
    }

    private class ConstructDocument extends ConstructYamlMap {
        @Override
        public Object construct(Node node) {

            Object val = super.construct(node);
            if (val instanceof Map) {
                Map<String, Object> map = (Map) val;
                return new Document(map);
            }

            throw new UnsupportedOperationException("failed to build document");
        }
    }
}
