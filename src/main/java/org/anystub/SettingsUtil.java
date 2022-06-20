package org.anystub;

import org.anystub.http.AnySettingsHttpExtractor;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.anystub.http.HttpUtil.globalBodyMask;
import static org.anystub.http.HttpUtil.globalBodyTrigger;

public class SettingsUtil {

    public static boolean matchBodyRule(String url) {
        Set<String> currentBodyTriggers = new HashSet<>();

        AnySettingsHttp settings = AnySettingsHttpExtractor.discoverSettings();

        if (settings != null) {
            currentBodyTriggers.addAll(asList(settings.bodyTrigger()));
        }

        if ((settings == null || !settings.overrideGlobal()) && globalBodyTrigger != null) {
            currentBodyTriggers.addAll(asList(globalBodyTrigger));
        }


        return currentBodyTriggers.stream()
                .anyMatch(url::contains);
    }

    public static String maskBody(String s) {
        Set<String> currentBodyMask = new HashSet<>();

        AnySettingsHttp settings = AnySettingsHttpExtractor.discoverSettings();
        if (settings != null) {
            currentBodyMask.addAll(asList(settings.bodyMask()));
        }

        if ((settings != null || !settings.overrideGlobal()) && globalBodyMask != null) {
            currentBodyMask.addAll(asList(globalBodyMask));
        }

        return currentBodyMask.stream()
                .reduce(s, (r, m) -> r.replaceAll(m, "..."));
    }

}
