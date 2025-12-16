package org.anystub;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class KeysSupplierCashedTest {


    @Test
    void testSet() {

        int[] i = new int[]{0};

        KeysSupplier keysSupplier = new KeysSupplierCashed(() -> {
            i[0] = i[0] + 1;
            return new String[]{String.valueOf(i[0])};
        });


        assertArrayEquals(new String[]{"1"}, keysSupplier.get());
        assertArrayEquals(new String[]{"1"}, keysSupplier.get());

    }

    @Test
    @AnyStubId(requestMasks = "test1")
    void keyMask() {
        KeysSupplier keysSupplier = new KeysSupplierCashed(() -> {

            return new String[]{"test", "test1", "hello from test1 to test and test3"};
        });


        assertArrayEquals(new String[]{"test", "...", "hello from ... to test and test3"}, keysSupplier.get());

    }
    @Test
    @AnyStubId(requestMasks = "t.*s")
    void keyMaskRegexp() {
        KeysSupplier keysSupplier = new KeysSupplierCashed(() -> {

            return new String[]{"test", "test1", "hello from test1 to test and test3"};
        });


        assertArrayEquals(new String[]{"...t", "...t1", "hello from ...t3"}, keysSupplier.get());

    }
}