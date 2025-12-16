package org.anystub;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

/**
 * helper class to serialize documents
 */
public class DocumentRepresent extends Representer {
    public DocumentRepresent(DumperOptions options) {
        super(options);
        this.representers.put(Document.class, new RepresentDocument());
    }

    private class RepresentDocument implements Represent {
        public Node representData(Object data) {
            if (data instanceof Document) {
                Document document = (Document) data;
                return representMapping(Tag.MAP, document.toMap(), DumperOptions.FlowStyle.AUTO);
            }
            return representScalar(new Tag("!unknown"), data.toString());
        }
    }
}
