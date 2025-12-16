package org.anystub;

/**
 * functional interface.
 */
@FunctionalInterface
public interface Supplier<T extends Object, E extends Throwable> {
    /**
     * returns response of expected type
     * @return
     * @throws E
     */
    T get() throws E;

}
