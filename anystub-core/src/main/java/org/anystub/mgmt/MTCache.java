package org.anystub.mgmt;


import org.anystub.AnySettingsHttp;
import org.anystub.AnySettingsHttpExtractor;
import org.anystub.AnyStubFileLocator;
import org.anystub.AnyStubId;

import java.util.logging.Logger;

/**
 * shared point to keep test setting
 * keeps only one copy of the settings to make it accessible from non-main thread
 * not thread safe - set on start of a test, reset after finish in main thread
 */
public class MTCache {
    private static AnyStubId fallbackBase = null;
    private static AnySettingsHttp fallbackHttpSettings = null;

    private MTCache() {
        
    }

    public static void resetMtFallback() {
        fallbackBase = null;
        fallbackHttpSettings = null;
    }

    /**
     *
     * @return
     */
    public static AutoCloseable setMtFallback() {
        fallbackBase = AnyStubFileLocator.discoverFile();
        if (fallbackBase == null) {
            Logger log = Logger.getLogger(BaseManagerFactory.class.getName());
            log.warning("Anystub cannot discover a test stub. Default stub will be used in the test.");
            return () -> {
            };
        }
        fallbackHttpSettings = AnySettingsHttpExtractor.httpSettings();

        return MTCache::resetMtFallback;
    }

    public static AnyStubId getFallbackBase() {
        return fallbackBase;
    }

    public static AnySettingsHttp getFallbackHttpSettings() {
        return fallbackHttpSettings;
    }
}
