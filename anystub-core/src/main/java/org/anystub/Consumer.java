package org.anystub;

@FunctionalInterface
public interface Consumer<E extends Throwable> {
    void run() throws E;
}
