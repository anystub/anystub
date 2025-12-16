package org.anystub;

public class SupplierCached<T extends Object, E extends Throwable> implements Supplier<T, E> {
    private T t = null;
    private final Supplier<T, E> supplier;

    public SupplierCached(Supplier<T, E> supplier) {
        this.supplier = supplier;
    }


    @Override
    public T get() throws E {
        if (t == null) {
            t = supplier.get();
        }
        return t;
    }
}
