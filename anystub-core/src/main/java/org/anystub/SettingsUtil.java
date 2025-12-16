package org.anystub;


import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

public class SettingsUtil {

    private SettingsUtil() {
    }

    /**
     * checks if URL should trigger saving request body
     * finds settings in the stack
     * @param method method of the request
     * @param url url to test
     * @return true if method and url match setting
     */
    public static boolean matchBodyRule(String method, String url) {
        return matchBodyRule(method, url, AnySettingsHttpExtractor.httpSettings());
    }

    /**
     * checks if URL should trigger saving request body
     * get settings from parameters
     * @param url url to test
     * @param settings test settings
     * @return
     */
    public static boolean matchBodyRule(String httpMethod, String url, AnySettingsHttp settings) {
        if(!asList(settings.bodyMethods()).contains(httpMethod)) {
            return false;
        }

        List<String> matchTrigger = stream(settings.bodyTrigger())
                .filter(rule -> !rule.startsWith("-"))
                .collect(Collectors.toList());

        if (!matchTrigger.isEmpty()) {
            boolean b = matchTrigger
                    .stream().anyMatch(url::contains);
            if (!b) {
                return false;
            }
        }

        return stream(settings.bodyTrigger())
                .filter(rule -> rule.startsWith("-"))
                .map(rule -> rule.substring(1))
                .noneMatch(url::contains);
    }

    /**
     * masks a string
     * @param s string to mask
     * @return masked string
     */
    public static String maskBody(String s) {
        return maskBody(s, AnyStubFileLocator.discoverFile());
    }
    /**
     * combines all available rules from GlobalSettings.globalBodyMask and AnySettingsHttp.bodyMask
     * and replace match with ellipsis (...).
     * If rules not specified - no replacements performed
     * @param s string to mask
     * @param settings test settings
     * @return masked string
     */
    public static String maskBody(String s, AnyStubId settings) {

        if (settings==null || settings.requestMasks().length == 0) {
            return s;
        }

        String combinedRule;
        if (settings.requestMasks().length>1) {
            combinedRule = stream(settings.requestMasks()).
                    map(r->String.format("(%s)", r))
                    .collect(Collectors.joining("|"));
        } else {
            combinedRule = settings.requestMasks()[0];
        }

        return s.replaceAll(combinedRule, "...");
    }

}
