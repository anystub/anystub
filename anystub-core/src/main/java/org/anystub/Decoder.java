package org.anystub;

/**
 * deserialize objects - from strings to object
 */
@FunctionalInterface
public interface Decoder<T extends Object>{
    T decode(Iterable<String> values);
}
