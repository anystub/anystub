package org.anystub;

import java.util.Optional;

public interface DocumentListI {

    void add(Document document);

    void clear();

    Optional<Document> getDocument(String[] keys);
}
