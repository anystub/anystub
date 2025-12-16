package org.anystub;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrackedDocumentListTest {


    @Test
    void testEmptyFetch() {

        TrackedDocumentList list = new TrackedDocumentList(asList());
        Document document = list.extractDocument(new String[]{""});
        assertNull(document);
    }
    @Test
    void testAddFetch() {

        Document d1 = Document.fromArray("d1", "i1");
        Document d2 = Document.fromArray("d1", "i2");
        Document d3 = Document.fromArray("d2", "i1 of d2");
        Document d4 = Document.fromArray("d1", "i3");

        TrackedDocumentList list = new TrackedDocumentList(asList(d1, d2, d3, d4));
        Document document;
        document = list.extractDocument(new String[]{"d2"});
        assertEquals("i1 of d2", document.get());
        document = list.extractDocument(new String[]{"d1"});
        assertEquals("i1", document.get());
        document = list.extractDocument(new String[]{"d1"});
        assertEquals("i2", document.get());
        document = list.extractDocument(new String[]{"d1"});
        assertEquals("i3", document.get());
        document = list.extractDocument(new String[]{"d1"});
        assertNull(document);
        document = list.extractDocument(new String[]{"d2"});
        assertNull(document);
        document = list.extractDocument(new String[]{"d2"});
        assertNull(document);
        document = list.extractDocument(new String[]{"d3"});
        assertNull(document);
    }

}