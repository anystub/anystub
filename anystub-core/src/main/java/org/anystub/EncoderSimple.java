package org.anystub;

/**
 * functional interface
 */
@FunctionalInterface
public interface EncoderSimple<T extends Object> {
    String encode(T t);
}
