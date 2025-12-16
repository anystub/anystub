package org.anystub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomGeneratorTest {
    static public enum En {
        Test,
        Prod;


    }

    static public class A {
        int i;
        byte bb;
        char cc;
        boolean bbb;
        double ddd;
        short aShort;
        long aLong;
        float aFloat;
        byte [] bytes;

        Integer II;
        Double d;
        String s;
        Long aLong1;
        Float aFloat1;
        Byte aByte1;

        String[] sa;
        List<String> ls;
        B b;
        List<B> bl;
        En en;
        LocalDate localDate;
        LocalDateTime localDateTime;
        OffsetTime offsetTime;
        OffsetDateTime offsetDateTime;
        ZonedDateTime zonedDateTime;



        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public Integer getII() {
            return II;
        }

        public void setII(Integer II) {
            this.II = II;
        }

        public Double getD() {
            return d;
        }

        public void setD(Double d) {
            this.d = d;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public String[] getSa() {
            return sa;
        }

        public void setSa(String[] sa) {
            this.sa = sa;
        }

        public List<String> getLs() {
            return ls;
        }

        public void setLs(List<String> ls) {
            this.ls = ls;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }

        public List<B> getBl() {
            return bl;
        }

        public void setBl(List<B> bl) {
            this.bl = bl;
        }

        public En getEn() {
            return en;
        }

        public void setEn(En en) {
            this.en = en;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public OffsetTime getOffsetTime() {
            return offsetTime;
        }

        public void setOffsetTime(OffsetTime offsetTime) {
            this.offsetTime = offsetTime;
        }

        public OffsetDateTime getOffsetDateTime() {
            return offsetDateTime;
        }

        public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
            this.offsetDateTime = offsetDateTime;
        }

        public ZonedDateTime getZonedDateTime() {
            return zonedDateTime;
        }

        public void setZonedDateTime(ZonedDateTime zonedDateTime) {
            this.zonedDateTime = zonedDateTime;
        }

        public byte getBb() {
            return bb;
        }

        public void setBb(byte bb) {
            this.bb = bb;
        }

        public char getCc() {
            return cc;
        }

        public void setCc(char cc) {
            this.cc = cc;
        }

        public boolean isBbb() {
            return bbb;
        }

        public void setBbb(boolean bbb) {
            this.bbb = bbb;
        }

        public double getDdd() {
            return ddd;
        }

        public void setDdd(double ddd) {
            this.ddd = ddd;
        }

        public short getaShort() {
            return aShort;
        }

        public void setaShort(short aShort) {
            this.aShort = aShort;
        }

        public long getaLong() {
            return aLong;
        }

        public void setaLong(long aLong) {
            this.aLong = aLong;
        }

        public float getaFloat() {
            return aFloat;
        }

        public void setaFloat(float aFloat) {
            this.aFloat = aFloat;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public Long getaLong1() {
            return aLong1;
        }

        public void setaLong1(Long aLong1) {
            this.aLong1 = aLong1;
        }

        public Float getaFloat1() {
            return aFloat1;
        }

        public void setaFloat1(Float aFloat1) {
            this.aFloat1 = aFloat1;
        }

        public Byte getaByte1() {
            return aByte1;
        }

        public void setaByte1(Byte aByte1) {
            this.aByte1 = aByte1;
        }
    }

    static public class B {
        int val;

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }
    }

    @Test
    void t2() throws JsonProcessingException {

        A a = RandomGenerator.g(A.class);
        String s = ObjectMapperFactory.get().writeValueAsString(a);

        assertFalse(s.contains("null"), s);
    }

    @Test
    void t3() throws JsonProcessingException {
        TypeReference<A> type = new TypeReference<A>() {
        };
        A a = RandomGenerator.g(type);
        String s = ObjectMapperFactory.get().writeValueAsString(a);

        assertFalse(s.contains("null"), s);
    }

    @Test
    void enumTest() {
        En en = RandomGenerator.gEnum(En.class);
        assertNotNull(en);
    }

    @Test
    void arrayLoopTest() throws JsonProcessingException {


        String[] t =new String[]{"abc", "xyz"};

        String s = ObjectMapperFactory.get().writeValueAsString(t);
        String[] recoveredT = ObjectMapperFactory.get().readValue(s, String[].class);

        assertArrayEquals(t, recoveredT);



    }

    enum AZ {
        aa,bb
    }

    @Test
    void TypeNameGen() {
        RandomGenerator.g(new TypeReference<Integer>() {
        });
        RandomGenerator.g(new TypeReference<String>() {
        });
        RandomGenerator.g(new TypeReference<Double>() {
        });
        RandomGenerator.g(new TypeReference<Long>() {
        });
        RandomGenerator.g(new TypeReference<Float>() {
        });
        RandomGenerator.g(new TypeReference<Byte>() {
        });
        byte[] g = RandomGenerator.g(new TypeReference<byte[]>() {
        });
        assertTrue(g.length >= 10);
        RandomGenerator.g(new TypeReference<AZ>() {
        });

        RandomGenerator.g(new TypeReference<LocalDateTime>() {
        });
        RandomGenerator.g(new TypeReference<LocalDate>() {
        });
        RandomGenerator.g(new TypeReference<OffsetDateTime>() {
        });
        RandomGenerator.g(new TypeReference<OffsetTime>() {
        });
        RandomGenerator.g(new TypeReference<ZonedDateTime>() {
        });
    }
}