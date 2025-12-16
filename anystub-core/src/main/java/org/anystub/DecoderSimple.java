package org.anystub;

/**
 *
 */
@FunctionalInterface
public interface DecoderSimple<T extends Object>{
    T decode(String values);
}
