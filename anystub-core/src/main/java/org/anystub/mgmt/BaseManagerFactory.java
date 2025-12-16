package org.anystub.mgmt;

import org.anystub.AnyStubFileLocator;
import org.anystub.AnyStubId;
import org.anystub.Base;

public final class BaseManagerFactory {
    private static BaseManager baseManager = null;

    private BaseManagerFactory() {
    }

    public static BaseManager getBaseManager() {
        if (baseManager == null) {
            baseManager = BaseManagerImpl.instance();
        }
        return baseManager;
    }

    public static Base getStub() {
        return BaseManagerFactory.getBaseManager().getStub();
    }

    public static Base getStub(String suffix) {
        return BaseManagerFactory.getBaseManager().getStub(suffix);
    }




    public static Base locate() {
        return BaseManagerFactory.locate(null);
    }

    public static Base locate(String fallback) {
        AnyStubId s = AnyStubFileLocator.discoverFile();
        Base discovered = baseFromSettings(s);
        if(discovered != null) {
            return discovered;
        }

        discovered = baseFromSettings(MTCache.getFallbackBase());
        if(discovered != null) {
            return discovered;
        }

        return BaseManagerFactory
                .getBaseManager()
                .getBase(fallback);
    }

    /**
     * builds base from given settings
     * @param s settings
     * @return base if settings provided, null if s is null
     */
    public static Base baseFromSettings(AnyStubId s){
        if (s == null) {
            return null;
        }
        return BaseManagerFactory
                .getBaseManager()
                .getBase(s.filename(), base -> base.constrain(s.requestMode()));

    }
}
