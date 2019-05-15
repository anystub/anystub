package org.anystub;

import org.anystub.mgmt.BaseManagerImpl;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static org.anystub.Document.ars;
import static org.junit.Assert.*;

/**
 */
public class BaseTest {

    @Test
    public void save() throws IOException {
        Base base = BaseManagerImpl.instance()
                .getBase("./stubSaveTest.yml");

        base.put("123", "321", "123123");
        base.put("1231", "321", "123123");
        assertEquals("123123", base.get("123", "321"));
        base.save();

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
    public void stringRequest() {
        Base base = BaseManagerImpl.instance().getBase();
        String request = base.request(() -> "xxx", "qwe", "stringkey");

        assertEquals("xxx", request);
    }

    @Test
    public void request() {
        Base base = BaseManagerImpl.instance().getBase("request.yml");
        base.clear();
        assertTrue(base.isNew());

        String rand = base.request("rand", "1002");

        assertEquals("-1594594225", rand);

        assertFalse(base.isNew());

        String[] rands = base.requestArray(Base::throwNSE, "rand", "1002");

        assertEquals("-1594594225", rands[0]);
        assertEquals("asdqwe", rands[1]);

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

    @Test(expected = NoSuchElementException.class)
    public void requestException() {
        Base base = BaseManagerImpl.instance()
                .getBase();
        base.clear();
        assertTrue(base.isNew());

        base.request("rand", "1002", "notakey");
    }

    @Test
    public void binaryDataTest() {
        Base base = BaseManagerImpl.instance()
                .getBase("./stubBin.yml");
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

    @Test(expected = NoSuchElementException.class)
    public void restrictionTest() {
        Base base = BaseManagerImpl.instance()
                .getBase("restrictionTest.yml");
        base.clear();
        base.constrain(RequestMode.rmNone);

        base.request("restrictionTest");
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
    public void requestNull() {

        Base base = BaseManagerImpl.instance()
                .getBase("./NullObj.yml");
        Human human = base.request2(() -> null,
                values -> null,
                x -> emptyList(),
                "13"
        );
        assertNull(human);
    }

    @Test
    public void requestComplexObject() {
        Human h = new Human(13, 180, 30, 60, "i'm");

        Base base = BaseManagerImpl.instance()
                .getBase("./complexObject.yml");
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
    public void historyCheck() {
        Base base = BaseManagerImpl.instance()
                .getBase("./historyCheck.yml");
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
    public void nullMatching() {
        Base base = BaseManagerImpl.instance()
                .getBase("./historyCheck.yml");
        base.clear();
        base.constrain(RequestMode.rmNew);

        assertEquals(0L, base.times());

        base.request(() -> "okok", "", "3", "3");
        base.request(() -> "okok", null, "3", "4");

        assertEquals(2, base.times());
        assertEquals(1, base.times(""));
        assertEquals(2, base.times(null, null));
    }

    @Test
    public void regexpMatching() {
        Base base = BaseManagerImpl.instance()
                .getBase("./historyCheck.yml");
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

    @Test(expected = IndexOutOfBoundsException.class)
    public void exceptionTest() {
        Base base = BaseManagerImpl.instance()
                .getBase("./exceptionStub.yml");
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
        exceptionCaught = false;
        try {
            base.request(() -> {
                throw new IndexOutOfBoundsException("for test");
            }, "key");
        } catch (IndexOutOfBoundsException ex) {
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);
        base.request(() -> "okok", "key");
    }

    @Test
    public void nullReturning() {
        Base base = BaseManagerImpl.instance()
                .getBase("./nullReturning.yml");
        base.clear();

        String[] emptyResult = base.requestArray(() -> null,
                "nullKey");

        assertNull(emptyResult);

        emptyResult = base.requestArray(() -> {
                    throw new NoSuchElementException();
                },
                "nullKey");
        assertNull(emptyResult);

        assertNull(base.request("nullKey"));
    }

    @Test
    public void request_oneway_object() throws IOException {
        Base base = BaseManagerImpl.instance()
                .getBase("./streams.yml")
                .constrain(RequestMode.rmAll);
        base.clear();
        base.save();


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
    public void requestSerializableTest() {
        Base base = BaseManagerImpl.instance()
                .getBase("./serialize.yml");
        base.clear();

        AAA aaa = base.requestSerializable(() -> new AAA(), "123");
        assertEquals(1, aaa.aaa);
        assertEquals(Integer.valueOf(15), aaa.s);
        aaa = base.requestSerializable(() -> null, "123");
        assertEquals(1, aaa.aaa);
        assertEquals(Integer.valueOf(15), aaa.s);
    }

    @Test
    public void fileInResourcesTest() {
        Base base = BaseManagerImpl.instance()
                .getBase("in-res.yml");
        base.clear();

        String test = base.request(() -> "xxx", "test");
        assertEquals("xxx", test);
    }

    @Test
    public void punctuationInStub() {

        Base base = BaseManagerImpl.instance()
                .getBase("./punctuation.yml");
        base.clear();

        String request = base.request(() -> "[][!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-]", "[][!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-]");

        assertEquals("[][!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-]", request);
    }


    @Test
    @AnyStubId
    public void propertyTest() {
        Base stub = BaseManagerImpl.getStub();

        stub.addProperty("test", "1", "a");
        stub.addProperty("test", "1", "b");
        stub.addProperty("xxx", "2");


        Document xxx;
        List<Document> test;
        xxx = stub.getProperty("xxx").findFirst().get();
        assertEquals("2", xxx.getVals().iterator().next());
        test = stub.getProperty("test", "1").collect(Collectors.toList());
        assertEquals(2, test.size());
        assertEquals("a", test.get(0).getVals().iterator().next());
        assertEquals("b", test.get(1).getVals().iterator().next());

        test = stub.getProperty("test", "1", "X").collect(Collectors.toList());
        assertTrue(test.isEmpty());

    }


}
