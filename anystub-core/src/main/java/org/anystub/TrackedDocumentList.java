package org.anystub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;

public class TrackedDocumentList {

    private final ConcurrentHashMap<List<String>, List<Document>> index = new ConcurrentHashMap<>();

    public TrackedDocumentList(List<Document> documents) {

        documents.forEach(document -> index.compute(document.getKey(), new BiWrite(document)));

    }

    static class BiWrite implements BiFunction<List<String>, List<Document>, List<Document>> {
        final Document document;

        public BiWrite(Document document) {

            this.document = document;
        }

        @Override
        public List<Document> apply(List<String> key, List<Document> cVal) {
            if (cVal == null) {
                return asList(document);
            }
            ArrayList<Document> res = new ArrayList<>(cVal);
            res.add(document);
            return res;
        }


    }

    static class BiExtract implements BiFunction<List<String>, List<Document>, List<Document>> {
        private Document res = null;

        @Override
        public List<Document> apply(List<String> key, List<Document> documents) {
            if (documents.isEmpty()) {
                return documents;
            }
            res = documents.get(0);
            return documents.subList(1, documents.size());
        }
    }

    public Document extractDocument(String[] keys) {

        BiExtract bi = new BiExtract();
        index.computeIfPresent(asList(keys), bi);
        return bi.res;
    }
}
