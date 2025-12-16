package org.anystub;


/**
 * serialize objects - from domain to strings
 * it won't process nulls
 */
@FunctionalInterface
public interface Encoder<T extends Object> {
    Iterable<String> encode(T t);
}
