package org.anystub;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

public class DocumentList implements DocumentListI {

    private final ConcurrentHashMap<List<String>, Document> index = new ConcurrentHashMap<>();

    private final Object request2lock = new Object();

    @Override
    public void add(Document document) {
        synchronized (request2lock) {
            index.putIfAbsent(document.getKey(), document);
        }
    }

    @Override
    public void clear() {
        index.clear();
    }

    @Override
    public Optional<Document> getDocument(String[] keys) {
        Document document = index.get(asList(keys));
        return document != null ? Optional.of(document) : Optional.empty();
    }


}
