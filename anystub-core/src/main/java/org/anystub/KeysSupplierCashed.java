package org.anystub;

import static java.util.Arrays.stream;

public class KeysSupplierCashed implements KeysSupplier {
    private String[] keys = null;

    private final KeysSupplier keysSupplier;

    public KeysSupplierCashed(KeysSupplier keysSupplier) {
        this.keysSupplier = keysSupplier;
    }

    @Override
    public String[] get() {
        if (keys == null) {
            keys = keysSupplier.get();
            keys = mask(keys);
        }
        return keys;
    }

    private String[] mask(String[] keys) {
        AnyStubId anyStubId = AnyStubFileLocator.discoverFile();
        if (anyStubId == null || anyStubId.requestMasks() == null || anyStubId.requestMasks().length == 0) {
            return keys;
        }

        return stream(keys)
                .map(k -> stream(anyStubId.requestMasks())
                        .reduce(k, (r, m) -> r.replaceAll(m, "...")))
                .toArray(String[]::new);

    }
}
