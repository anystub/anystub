package org.anystub;

import org.anystub.mgmt.BaseManagerFactory;
import org.anystub.mgmt.MTCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static org.anystub.Document.ars;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 *
 */
class BaseTest {

    @Test
    void testSave() throws IOException {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/stubSaveTest.yml");

        base.put(Document.fromArray("123", "321", "123123"));
        base.put(Document.fromArray("1231", "321", "123123"));
        assertEquals("123123", base
                .getVals("123", "321")
                .iterator()
                .next());

        base.clear();
        Optional<String> opt = base.getOpt("123", "321");
        assertFalse(opt.isPresent());

        String request = base.request("123", "321");
        assertEquals("123123", request);
        opt = base.getOpt("123", "321");
        assertTrue(opt.isPresent());

        base.clear();
        base.constrain(RequestMode.rmNone);
        opt = base.getOpt("123", "321");
        assertTrue(opt.isPresent());
    }


    @Test
    void testStringRequest() {
        Base base = BaseManagerFactory.getBaseManager().getBase();
        String request = base.request(() -> "xxx", "qwe", "stringkey");

        assertEquals("xxx", request);
    }

    @Test
    void testRequest() {
        Base base = BaseManagerFactory.getBaseManager().getBase("request.yml");
        base.clear();
        assertTrue(base.isNew());

        String rand = base.request("rand", "1002");

        assertEquals("-1594594225", rand);

        assertFalse(base.isNew());

        int val = base.request2(Base::throwNSE,
                values -> parseInt(values.iterator().next()),
                new Encoder<Integer>() {
                    @Override
                    public Iterable<String> encode(Integer integer) {
                        return Collections.singletonList(integer.toString());
                    }
                },
                "rand", "1002"
        );

        assertEquals(-1594594225, val);

        val = base.request(Integer::parseInt,
                "rand", "1002"
        );

        assertEquals(-1594594225, val);
    }

    @Test()
    void testRequestException() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase();
        base.clear();
        assertTrue(base.isNew());

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            base.request("rand", "1002", "notakey");
        });
    }

    @Test
    void testBinaryDataTest() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/stubBin.yml");
        base.clear();

        byte[] arr = new byte[256];
        IntStream.range(0, 256).forEach(x -> arr[x] = (byte) (x));
        base.request(() -> arr,
                new DecoderSimple<byte[]>() {
                    @Override
                    public byte[] decode(String values) {
                        return
                                Base64.getDecoder().decode(values);
                    }
                },
                new EncoderSimple<byte[]>() {
                    @Override
                    public String encode(byte[] values) {
                        return
                                Base64.getEncoder().encodeToString(values);
                    }
                }
                ,
                "binaryDataB64");


        base.clear();
        byte[] arr1 = base.request(Base::throwNSE,
                s -> Base64.getDecoder().decode(s),
                Base::throwNSE,
                "binaryDataB64");


        assertArrayEquals(arr, arr1);
        arr1 = base.request(s -> Base64.getDecoder().decode(s),
                "binaryDataB64");


        assertArrayEquals(arr, arr1);

    }

    @Test
    void testRestrictionTest() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("restrictionTest.yml");
        base.clear();
        base.constrain(RequestMode.rmNone);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            base.request("restrictionTest");
        });
    }


    static class Human {
        Integer id;
        Integer height;
        Integer age;
        Integer weight;
        String name;

        public Human(int id, int height, int age, int weight, String name) {
            this.height = height;
            this.age = age;
            this.weight = weight;
            this.name = name;
            this.id = id;
        }

        public List<String> toList() {
            ArrayList<String> res = new ArrayList<>();
            res.add(id.toString());
            res.add(height.toString());
            res.add(age.toString());
            res.add(weight.toString());
            res.add(name);
            return res;
        }
    }


    @Test
    void testRequestNull() {

        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/NullObj.yml");
        Human human = base.request2(() -> null,
                values -> null,
                x -> emptyList(),
                "13"
        );
        assertNull(human);
    }

    @Test
    void testRequestComplexObject() {
        Human h = new Human(13, 180, 30, 60, "i'm");

        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/complexObject.yml");
        base.clear();

        Human human = base.request2(() -> h,
                values -> {
                    Iterator<String> v = values.iterator();
                    return new Human(parseInt(v.next()),
                            parseInt(v.next()),
                            parseInt(v.next()),
                            parseInt(v.next()),
                            v.next());
                },
                Human::toList

                ,
                "13"
        );

        assertEquals(180, (int) human.height);
        assertEquals(30, (int) human.age);
        assertEquals(60, (int) human.weight);
        assertEquals("i'm", human.name);
        assertEquals(13, (int) human.id);


        base.clear();

        human = base.request2(Base::throwNSE,
                values -> {
                    Iterator<String> v = values.iterator();
                    return new Human(parseInt(v.next()),
                            parseInt(v.next()),
                            parseInt(v.next()),
                            parseInt(v.next()),
                            v.next());
                },
                Human::toList,
                "13"
        );

        assertEquals(180, (int) human.height);
        assertEquals(30, (int) human.age);
        assertEquals(60, (int) human.weight);
        assertEquals("i'm", human.name);
        assertEquals(13, (int) human.id);
    }

    @Test
    void testHistoryCheck() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/historyCheck.yml");
        base.clear();

        assertEquals(0L, base.times());

        base.request(() -> "okok", "2", "3", "3");
        base.request(() -> "okok", "2", "3", "4");
        base.request(() -> "okok", "2", "3", "4");
        base.request(() -> "okok", "5", "3", "4");
        base.request(() -> "okok", "5");

        assertEquals(5L, base.times());
        assertEquals(5L, base.history().count());
        assertEquals(1L, base.history("5").count());
        assertEquals(2L, base.times("2", "3", "4"));
        assertEquals(1L, base.times("5", "3", "4"));
        assertEquals(3L, base.match("2").count());
        assertEquals(3L, base.times("2"));
        assertEquals(4L, base.times(null, null));
        assertEquals(3L, base.times(null, null, "4"));
    }

    @Test
    void testNullMatching() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/historyCheck.yml");
        base.clear();
        base.constrain(RequestMode.rmNew);

        assertEquals(0L, base.times());

        base.request(() -> "okok", "", "3", "3");
        String v = null;
        base.request(() -> "okok", v, "3", "4");

        assertEquals(2, base.times());
        assertEquals(1, base.times(""));
        assertEquals(2, base.times(null, null));
    }

    @Test
    void testRegexpMatching() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/historyCheck.yml");
        base.clear();

        base.request(() -> "okok", "2222", "3", "3");
        base.request(() -> "okok", "2321", "3345", "4");
        base.request(() -> "okok", "532", "3", "4");
        base.request(() -> "okok", "5456456");

        assertEquals(4, base.matchEx(ars(), ars(".*ko.*")).count());
        assertEquals(4, base.matchEx().count());
        assertEquals(4, base.timesEx());
        assertEquals(3, base.matchEx(null, "3.*").count());
        assertEquals(1, base.matchEx(".*56.*").count());
        assertEquals(1, base.timesEx(".*56.*"));
        assertEquals(4, base.timesEx(ars(), ars(".ko.")));

        assertEquals(4, base.history().count());

    }

    @Test
    void testExceptionTest() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/exceptionStub.yml");
        base.clear();

        boolean exceptionCaught = false;
        try {
            base.request(() -> {
                throw new IndexOutOfBoundsException("for test");
            }, "key");
        } catch (IndexOutOfBoundsException ex) {
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);

        base.clear();
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            base.request(() -> {
                throw new IndexOutOfBoundsException("for test");
            }, "key");
        });

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            base.request(() -> "okok", "key");
        });
    }

    @Test
    void testNullReturning() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/nullReturning.yml");
        base.clear();

        Integer emptyResult = base.requestI(() -> null,
                "nullKey");

        assertNull(emptyResult);

        emptyResult = base.requestI(() -> {
                    throw new NoSuchElementException();
                },
                "nullKey");
        assertNull(emptyResult);

        assertNull(base.request("nullKey"));


    }

    @Test
    void testRequest_oneway_object() throws IOException {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/streams.yml")
                .constrain(RequestMode.rmAll);
        base.purge();


        BufferedReader v1 = base.request(
                (Supplier<BufferedReader, IOException>) () -> new BufferedReader(new StringReader("test")),
                values -> new BufferedReader(new StringReader(values)),
                bufferedReader -> {
                    try {
                        return bufferedReader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException("", e);
                    }
                },
                "21");

        assertEquals("test", v1.readLine());

    }

    static class AAA implements Serializable {
        int aaa = 1;
        Integer s = 15;
    }

    @Test
    void testRequestSerializableTest() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/serialize.yml");
        base.clear();

        AAA aaa = base.requestSerializable(() -> new AAA(), "123");
        assertEquals(1, aaa.aaa);
        assertEquals(Integer.valueOf(15), aaa.s);
        aaa = base.requestSerializable(() -> null, "123");
        assertEquals(1, aaa.aaa);
        assertEquals(Integer.valueOf(15), aaa.s);
    }

    @Test
    void testFileInResourcesTest() {
        Base base = BaseManagerFactory.getBaseManager()
                .getBase("in-res.yml");
        base.clear();

        String test = base.request(() -> "xxx", "test");
        assertEquals("xxx", test);
    }

    @Test
    void testPunctuationInStub() {

        Base base = BaseManagerFactory.getBaseManager()
                .getBase("tmp/punctuation.yml");
        base.clear();

        String request = base.request(() -> "[][!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-]", "[][!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-]");

        assertEquals("[][!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-]", request);
    }


    @RepeatedTest(1)
    @AnyStubId(requestMode = RequestMode.rmAll)
    void testRequestO() {
        Base locate = BaseManagerFactory.locate();
        String s;
        s = locate.request(() -> "test", String.class, "method", null, "another key");
        assertEquals("test", s);

        s = locate.request(() -> "test2", String.class, "method2", null, "another key");
        assertEquals("test2", s);


    }


    @Test
    void testCS() throws IOException {
        Base locate = new Base("src/test/resources/anystub/testCS.yml");
        String s = locate.request(() -> "Привет привет", String.class, 1);
        assertEquals("Привет привет", s);

        locate = new Base("src/test/resources/anystub/testCS.yml").constrain(RequestMode.rmNone);
        s = locate.request(() -> "Привет привет", String.class, 1);
        assertEquals("Привет привет", s);

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void testAsync() throws Exception {

        try (AutoCloseable x = MTCache.setMtFallback()) {
            CompletableFuture<String> resp = CompletableFuture.supplyAsync(() -> {
                return BaseManagerFactory.locate()
                        .request(() -> "test", "testAsync");
            });


            CompletableFuture<String> resp2 = CompletableFuture.supplyAsync(() -> {
                return BaseManagerFactory.locate()
                        .request(() -> "testX", "testAsync");
            });


            CompletableFuture.allOf(resp, resp2).join();

            Assertions.assertEquals("test", resp.get());
            Assertions.assertEquals("testX", resp2.get());
            long callsCount = BaseManagerFactory.locate()
                    .history("testAsync").count();

            Assertions.assertEquals(2, callsCount);
        }

    }


    @Test
    @AnyStubId(requestMode = RequestMode.rmNew)
    void testAsyncWithNew() throws Exception {

        AtomicInteger realCalls = new AtomicInteger();

        BaseManagerFactory.locate().purge();

        try (AutoCloseable x = MTCache.setMtFallback()) {
            CompletableFuture<String> resp = CompletableFuture.supplyAsync(() -> {
                return BaseManagerFactory.locate()
                        .request(() -> {
                            realCalls.incrementAndGet();
                            return "test";
                        }, "testAsync");
            });


            CompletableFuture<String> resp2 = CompletableFuture.supplyAsync(() -> {
                return BaseManagerFactory.locate()
                        .request(() -> {
                            realCalls.incrementAndGet();
                            return "testX";
                        }, "testAsync");
            });


            CompletableFuture.allOf(resp, resp2).join();

            Assertions.assertEquals(resp.get(), resp2.get());
            long callsCount = BaseManagerFactory.locate()
                    .history("testAsync").count();

            Assertions.assertEquals(2, callsCount);
            Assertions.assertEquals(1, realCalls.get());
        }

    }


    @Test
    void testTrackStub() {
        Base base = new Base("tmp/TrackStub.yml");
        new File(base.getFilePath()).delete();
        base.constrain(RequestMode.rmTrack);

        String resp;
        base.request(()->"1", "key1-1");
        base.request(()->"2", "key2");
        resp = base.request(() -> "1-2", "key1-1");

        assertEquals("1-2", resp);

        base = new Base("tmp/TrackStub.yml")
                .constrain(RequestMode.rmTrack);
        resp = base.request(()->"1", "key1-1");
        assertEquals("1", resp);
        resp = base.request(() -> "1-2", "key1-1");
        assertEquals("1-2", resp);
        resp = base.request(()->"2", "key2");
        assertEquals("2", resp);

    }

}
