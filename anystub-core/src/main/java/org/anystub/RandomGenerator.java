package org.anystub;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * Utility class for generating random test data.
 * <p>
 * NOTE: This class uses pseudorandom number generators (ThreadLocalRandom/Random)
 * for TEST DATA GENERATION ONLY. It is NOT suitable for cryptographic purposes,
 * security tokens, passwords, or any security-sensitive operations.
 * </p>
 */
public class RandomGenerator {

    private static final Logger log = Logger.getLogger(RandomGenerator.class.getName());
    // Use ThreadLocalRandom for better performance and to satisfy security scanners
    // Can be overridden with a seeded Random via initRandomizer() for deterministic testing
    private static volatile Random random = null;

    private RandomGenerator() {
    }

    public static <R> R g(Class<R> genClass) {
        if (genClass == null) {
            return null;
        }

        if (genClass.isPrimitive()) {
            return gPrimitive(genClass);
        }

        R r;
        r = gBasic(genClass);
        if (r != null) {
            return r;
        }

        r = gTime(genClass);
        if (r != null) {
            return r;
        }

        if (genClass.isArray()) {
            try {
                r = ObjectMapperFactory.get().readValue("[]", genClass);
            } catch (JsonProcessingException e) {
                log.finest(() -> String.format("cannot build empty array for %s", genClass.getName()));
            }
            return r;
        }


        try {
            r = ObjectMapperFactory.get().readValue("{}", genClass);
            populate(r);
            return r;
        } catch (JacksonException ex) {
            log.finest(() -> String.format("cannot build empty object for %s", genClass.getName()));
        }

        try {
            // attempt to create an empty list
            r = ObjectMapperFactory.get().readValue("[]", genClass);
            return r;
        } catch (JacksonException ex) {
            log.finest(() -> String.format("cannot build an empty list for %s", genClass.getName()));
        }

        return null;
    }

    public static <R> R gPrimitive(Class<R> genClass) {
        if (genClass.equals(int.class)) {
            Object o = gInt();
            return (R) o;
        }
        if (genClass.equals(byte.class)) {
            Object o = (byte) gInt();
            return (R) o;
        }
        if (genClass.equals(char.class)) {
            Object o = (char) gInt();
            return (R) o;
        }
        if (genClass.equals(boolean.class)) {
            Object o = getRandom().nextBoolean();
            return (R) o;
        }
        if (genClass.equals(double.class)) {
            Object o = gDouble();
            return (R) o;
        }
        if (genClass.equals(short.class)) {
            Object o = (short) gInt();
            return (R) o;
        }
        if (genClass.equals(long.class)) {
            Object o = (long) gInt();
            return (R) o;
        }
        if (genClass.equals(float.class)) {
            Object o = (float) gDouble();
            return (R) o;
        }

        return null;
    }

    public static <R> R gBasic(Class<R> genClass) {
        if (genClass.equals(String.class)) {
            return (R) gString();
        }
        if (genClass.equals(Integer.class)) {
            return (R) (Object) gInt();
        }
        if (genClass.equals(Double.class)) {
            return (R) (Double) gDouble();
        }
        if (genClass.equals(Long.class)) {
            return (R) (Long) (long) gInt();
        }
        if (genClass.equals(Float.class)) {
            return (R) (Float) (float) gDouble();
        }
        if (genClass.equals(Byte.class)) {
            return (R) (Byte) (byte) gInt();
        }
        if (genClass.equals(byte[].class)) {
            byte[] n = new byte[getRandom().nextInt(10) + 10];
            getRandom().nextBytes(n);
            return (R) n;
        }
        if (genClass.isEnum()) {
            return gEnum(genClass);
        }
        return null;
    }

    public static <R> R gTime(Class<R> genClass) {
        if (genClass.equals(LocalDateTime.class)) {
            return (R) gDateTime();
        }
        if (genClass.equals(LocalDate.class)) {
            return (R) gDate();
        }

        if (genClass.equals(OffsetDateTime.class)) {
            return (R) gOffsetDateTime();
        }

        if (genClass.equals(OffsetTime.class)) {
            return (R) gOffsetTime();
        }
        if (genClass.equals(ZonedDateTime.class)) {
            return (R) gZonedDateTime();
        }
        return null;
    }

    public static <R> void populate(R a) {
        Class<?> aClass = a.getClass();
        for (Method m : aClass.getMethods()) {
            if (!m.getName().startsWith("set") ||
                    m.getParameterTypes().length != 1) {
                continue;
            }
            Object g = g(m.getParameterTypes()[0]);
            try {
                m.invoke(a, g);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.finest(() -> String.format("cannot set generated value with %s", m.getName()));
            }
        }
    }

    public static String gString() {
        String abc = "ABCDEFG abcdef 1234567890";
        StringBuilder sb = new StringBuilder();
        int len = getRandom().nextInt(100) + 3;
        for (int i = 0; i < len; i++) {
            int p = getRandom().nextInt(abc.length());
            sb.append(abc.charAt(p));
        }
        return sb.toString();
    }

    public static double gDouble() {

        return getRandom().nextDouble() * 100;
    }

    public static int gInt() {
        return getRandom().nextInt();
    }

    public static LocalDate gDate() {
        return gDateTime().toLocalDate();
    }

    public static LocalDateTime gDateTime() {
        return gOffsetDateTime().toLocalDateTime();
    }

    public static OffsetDateTime gOffsetDateTime() {
        return OffsetDateTime.now()
                .plusDays(getRandom().nextInt(100) - 200L)
                .plusHours(getRandom().nextInt(3))
                .plusMinutes(getRandom().nextInt(10))
                .plusSeconds(getRandom().nextInt(60));
    }

    public static OffsetTime gOffsetTime() {
        return gOffsetDateTime().toOffsetTime();
    }

    public static ZonedDateTime gZonedDateTime() {
        return gOffsetDateTime().toZonedDateTime();
    }

    public static <R> R gEnum(Class<R> genClass) {
        if (!genClass.isEnum()) {
            return null;
        }
        try {
            Method values;
            values = genClass.getDeclaredMethod("values");
            Object invoke = values.invoke(genClass);
            Object[] instances = (Object[]) invoke;
            return (R) instances[getRandom().nextInt(instances.length)];
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.finest(() -> String.format("cannot get instance of enum %s", genClass.getName()));
        }
        return null;
    }

    public static <R> R g(TypeReference<R> returnType) {
        if(returnType==null) {
            return null;
        }
        String typeName = returnType.getType().getTypeName();
        R r = gBasic(typeName);
        if (r != null) {
            return r;
        }
        r = gTime(typeName);

        try {
            r = ObjectMapperFactory.get().readValue("{}", returnType);
            populate(r);
            return r;
        } catch (JacksonException ex) {
            log.finest(() -> String.format("cannot build empty object for %s", typeName));
        }

        try {
            // attempt to create an empty list
            r = ObjectMapperFactory.get().readValue("[]", returnType);
            return r;
        } catch (JacksonException ex) {
            log.finest(() -> String.format("cannot build an empty list for %s", typeName));
        }

        return r;

    }



    public static <R> R gBasic(String genClass) {
        if (genClass.equals(String.class.getName())) {
            return (R) gString();
        }
        if (genClass.equals(Integer.class.getName())) {
            return (R) (Object) gInt();
        }
        if (genClass.equals(Double.class.getName())) {
            return (R) (Double) gDouble();
        }
        if (genClass.equals(Long.class.getName())) {
            return (R) (Long) (long) gInt();
        }
        if (genClass.equals(Float.class.getName())) {
            return (R) (Float) (float) gDouble();
        }
        if (genClass.equals(Byte.class.getName())) {
            return (R) (Byte) (byte) gInt();
        }
        if (genClass.equals("byte[]")) {
            byte[] n = new byte[getRandom().nextInt(10) + 10];
            getRandom().nextBytes(n);
            return (R) n;
        }
        return null;
    }

    public static <R> R gTime(String genClass) {
        if (genClass.equals(LocalDateTime.class.getName())) {
            return (R) gDateTime();
        }
        if (genClass.equals(LocalDate.class.getName())) {
            return (R) gDate();
        }

        if (genClass.equals(OffsetDateTime.class.getName())) {
            return (R) gOffsetDateTime();
        }

        if (genClass.equals(OffsetTime.class.getName())) {
            return (R) gOffsetTime();
        }
        if (genClass.equals(ZonedDateTime.class.getName())) {
            return (R) gZonedDateTime();
        }
        return null;
    }

    @SuppressWarnings("java:S2245") // Pseudorandom generator is safe for test data generation (not security-sensitive)
    public static Random getRandom() {
        Random r = random;
        // Use ThreadLocalRandom by default for non-cryptographic random generation
        // This is thread-safe, efficient, and appropriate for test data generation
        return r != null ? r : ThreadLocalRandom.current();
    }

    /**
     * Initialize with a seeded Random for deterministic testing.
     * This allows tests to produce reproducible results.
     * @param seed the seed value
     * @return the initialized Random instance
     */
    public static Random initRandomizer(int seed) {
        random = new Random(seed);
        return random;
    }

    /**
     * Reset to using ThreadLocalRandom (removes any seeded Random).
     */
    public static void resetRandomizer() {
        random = null;
    }
}
