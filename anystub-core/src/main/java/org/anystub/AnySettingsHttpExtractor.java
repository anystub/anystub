package org.anystub;

import org.anystub.mgmt.MTCache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

/**
 * extracts http settings from @AnySettingsHttp
 */
public class AnySettingsHttpExtractor {

    private AnySettingsHttpExtractor() {

    }

    /**
     * fetches settings from annotations in a test
     * @return settings based on annotations
     */
    public static AnySettingsHttp discoverSettings() {
        return discoverSettingsEx().anySettingsHttp;
    }

    private static class SearchResults {
        AnySettingsHttp anySettingsHttp;
        String config;
    }

    /**
     * discovers anystub settings
     * finds an annotated method or method in annotated class
     * @return settings - never null
     */
    private static SearchResults discoverSettingsEx() {
        SearchResults searchResults = new SearchResults();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement s : stackTrace) {
            if (s.getMethodName().startsWith("lambda$")) {
                continue;
            }
            Class<?> aClass;
            try {
                aClass = Class.forName(s.getClassName());
            } catch (ClassNotFoundException ignored) {
                aClass = null;
            }
            if (aClass == null) {
                continue;
            }
            Method methodStream = stream(aClass.getDeclaredMethods())
                    .filter(method -> method.getName().equals(s.getMethodName()))
                    .filter(method -> method.getAnnotation(AnySettingsHttp.class) != null ||
                            method.getAnnotation(AnyStubId.class) != null)
                    .findFirst().orElse(null);

            if (methodStream != null) {
                searchResults.anySettingsHttp = methodStream.getAnnotation(AnySettingsHttp.class);
                searchResults.config = getAnystubId(methodStream, aClass);
            }
            if (searchResults.anySettingsHttp != null) {
                break;

            }
            searchResults.anySettingsHttp = aClass.getDeclaredAnnotation(AnySettingsHttp.class);
            AnyStubId declaredAnnotation = aClass.getDeclaredAnnotation(AnyStubId.class);
            if (searchResults.anySettingsHttp == null) {
                if (declaredAnnotation != null) {
                    searchResults.config = declaredAnnotation.config();
                    break;
                }
            } else {
                if (declaredAnnotation != null) {
                    searchResults.config = declaredAnnotation.config();
                }
                break;
            }
        }
        return searchResults;
    }

    private static String getAnystubId(Method methodStream, Class<?> aClass) {
        AnyStubId res = methodStream.getAnnotation(AnyStubId.class);
        if (res == null) {
            res = aClass.getDeclaredAnnotation(AnyStubId.class);
        }

        return res != null ? res.config() : null;
    }



    /**
     * fetches settings from annotations in a test
     * (*tries MtCache if not found)
     * enrich with setting file if allowed
     * @return settings based on annotations
     */
    public static AnySettingsHttp httpSettings() {
        boolean overrideGlobal = false;
        boolean allHeaders = false;
        final List<String> headers = new ArrayList<>();
        final List<String> bodyTrigger = new ArrayList<>();
        final List<String> bodyMethods = new ArrayList<>();


        SearchResults searchResults = AnySettingsHttpExtractor.discoverSettingsEx();
        AnySettingsHttp settings = searchResults.anySettingsHttp;
        if(settings == null) {
            settings = MTCache.getFallbackHttpSettings();
        }
        if (settings != null) {
            overrideGlobal = settings.overrideGlobal();
            allHeaders = settings.allHeaders();
            headers.addAll(asList(settings.headers()));
            bodyTrigger.addAll(asList(settings.bodyTrigger()));
            bodyMethods.addAll(asList(settings.bodyMethods()));
        }

        if (settings == null || !settings.overrideGlobal()) {
            String config = searchResults.config != null ? searchResults.config : "config";
            TestSettings testSettings = ConfigFileUtil.get(config);
            headers.addAll(asList(testSettings.headers));
            bodyTrigger.addAll(asList(testSettings.bodyTrigger));
            bodyMethods.addAll(asList(testSettings.bodyMethods));
        }

        boolean overrideGlobalF = overrideGlobal;
        boolean allHeadersF = allHeaders;


        return new AnySettingsHttp(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return AnySettingsHttp.class;
            }

            @Override
            public boolean overrideGlobal() {
                return overrideGlobalF;
            }

            @Override
            public boolean allHeaders() {
                return allHeadersF;
            }

            @Override
            public String[] headers() {
                return headers.stream().distinct().toArray(String[]::new);
            }

            @Override
            public String[] bodyTrigger() {
                return bodyTrigger.stream().distinct().toArray(String[]::new);
            }

            @Override
            public String[] bodyMethods() {
                return bodyMethods.stream().distinct().toArray(String[]::new);
            }
        };
    }
}
