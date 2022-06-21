package org.anystub.http;


import org.anystub.AnySettingsHttpExtractor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class AnySettingsHttpExtractor2Test {

    @Test
    public void testDiscoverSettings() {
        assertNull(AnySettingsHttpExtractor.discoverSettings());
    }

}