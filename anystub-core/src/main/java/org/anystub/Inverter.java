package org.anystub;

@FunctionalInterface
public interface Inverter<T extends Object> {
    T invert(T t, java.util.function.BiFunction<Iterable<String>, Throwable, T> decoderFunction);
}
