package org.anystub.mgmt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MTCacheTest {

    @Test
    void setMtFallback() throws Exception {
        try(AutoCloseable x = MTCache.setMtFallback()) {
            Assertions.assertNotNull(x);
        }
    }
}