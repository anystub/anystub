package org.anystub;

import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static java.util.Arrays.asList;
import static org.anystub.Document.ars;
import static org.anystub.Document.arsNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * test for document class
 */
public class DocumentTest {

    @Test
    public void equalTest() {
        assertEquals(new Document("qwe", "ewwq", "123"), new Document("qwe", "ewwq", "123"));
        assertEquals(new Document(), new Document());
        assertNotEquals(new Document("qwe", "ewwq", "1234"), new Document("qwe", "ewwq", "123"));
        assertNotEquals(new Document("qwe", "ewwq", "1234"), new Document("qwe", "ewwq"));
        assertNotEquals(new Document("qwe", "ewwq"), new Document("qwe", "ewwq", "123"));
        assertNotEquals(new Document("qwe", "ewwq"), new Document("qwe", null, "123"));
    }

    @Test
    public void equalToTest() {
        assertTrue(new Document("123", "321").keyEqual_to("123", "321"));
        assertTrue(new Document("123", "321", "asd").keyEqual_to("123", "321", "asd"));
        assertFalse(new Document("123", "321").keyEqual_to("123"));
    }

    @Test
    public void match_toTest() {
        assertTrue(new Document(ars("qwe"),ars("321")).match_to());
        assertTrue(new Document(ars("qwe"),ars("321")).match_to("qwe"));
        assertFalse(new Document(ars("qwe"),ars("321")).match_to("q"));
        assertFalse(new Document(ars("qwe"),ars("321")).match_to("qwe", null));
    }

    @Test
    public void matchEx_toTest() {
        assertTrue(new Document(ars("qwe"),ars("321")).matchEx_to(ars()));
        assertTrue(new Document(ars("qwe"),ars("321")).matchEx_to());
        assertTrue(new Document(ars("qwe"),ars("321")).matchEx_to("qwe"));
        assertTrue(new Document(ars("qwe"),ars("321")).matchEx_to("q.*"));
        assertTrue(new Document(ars("qwe"),ars("321")).matchEx_to(".*w.*"));
        assertTrue(new Document(ars("qwe"),ars("321")).matchEx_to(".*e$"));
        assertTrue(new Document(ars("qwe"),ars("321")).matchEx_to("^q.*"));
        assertTrue(new Document(ars("qwe", "321"),ars("321")).matchEx_to("qwe"));
        assertTrue(new Document(ars("qwe", "321"),ars("321")).matchEx_to("qwe", null));
        assertTrue(new Document(ars("qwe"),ars("321")).matchEx_to(ars("qwe"), ars("3.*")));
        assertTrue(new Document(ars("qwe"),ars("321")).matchEx_to(ars("qwe"), ars(".2.")));
        assertFalse(new Document(ars("qwe"),ars("321")).matchEx_to(ars("qwe"), ars("^2.*")));
        assertFalse(new Document(ars("qwe"),ars("321")).matchEx_to("f"));
        assertFalse(new Document(ars("qwe"),ars("321")).matchEx_to(ars(), ars("qwe", null)));
    }

    @Test(expected = AssertionError.class)
    public void assert_toTest() {
        // expected two values in key. first one is equal to "qwe", second one is any value
        new Document(ars("qwe"), ars("321")).assert_to("qwe", null);
    }

    @Test(expected = AssertionError.class)
    public void assert_to2Test() {
        // expected two values in key are equal to two tested values
        new Document(ars("qwe", "asd", "123"), ars("321")).assert_to("qwe", "dsa");
    }

    @Test(expected = AssertionError.class)
    public void assertEx_toTest() {
        // expected two values in key are matched to two tested values
        new Document(ars("qwe", "asd", "123"), ars("321")).assertEx_to("qwe", ".X.");
    }

    @Test(expected = AssertionError.class)
    public void assertEx_to2Test() {
        // expected value contains two values "qwe" and any other. actually it has only one value "321"
        new Document(ars("qwe"), ars("321")).assertEx_to(ars(), ars("qwe", null));
    }

    @Test(expected = NoSuchElementException.class)
    public void exceptionTest() {
        Document doc = new Document(new NoSuchElementException("aaaa"), "123");
        Iterator<String> exception = doc.getException().iterator();
        assertTrue(exception.next().contains("NoSuchElementException"));
        assertEquals("aaaa", exception.next());
        assertFalse(exception.hasNext());
        doc.getVals();
        fail();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void exceptionWMessageTest() {
        new Document(new IndexOutOfBoundsException("aaaa"), "123").getVals();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void exceptionWoMessageTest() {
        new Document(new IndexOutOfBoundsException(), "123").getVals();
    }

    @Test(expected = RuntimeException.class)
    public void exceptionNotFountTest() {

        TreeMap<String, Object> res = new TreeMap<>();
        res.put("keys", "123");
        res.put("values", asList("nonexistentException", "msg"));
        Document document = new Document(res);
        document.getVals();
    }

    @Test
    public void aroTest() {
        String[] aro = Document.aro("sdf", 2, "ssdf");
        assertArrayEquals(ars("sdf", null, null, "ssdf"), aro);
        aro = Document.aro("sdf", 0, "ssdf");
        assertArrayEquals(ars("sdf", "ssdf"), aro);
    }

    @Test
    public void nullHolder() {
        Map<String, Object> map = new Document(ars("12")).toMap();

        Document document = new Document(map);
        assertNull(document.getVals());
    }

    @Test
    public void emptyHolder() {
        Map<String, Object> map = new Document(ars("12"), arsNull()).toMap();

        Document document = new Document(map);

        assertNull(document.get());
        assertNull(document.getVals().iterator().next());

    }

    @Test
    public void fromArrayTest() {
        Document document = Document.fromArray("11", "22", "333");

        assertEquals("333", document.get());
        assertTrue(document.keyEqual_to("11", "22"));

    }

    @Test
    public void isNullValueTest() {
        assertTrue(new Document("1","2").isNullValue());
    }
}