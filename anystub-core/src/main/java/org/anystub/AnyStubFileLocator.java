package org.anystub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class AnyStubFileLocator {

    private AnyStubFileLocator() {
    }

    /**
     * looks for runtime data about current stub file in the call point.
     * It tracks stack-trace up to the first method or class/method annotated
     * with @AnystubId and extracts defined parameters
     *
     *
     * @return runtime data, if no annotation found returns null
     */
    public static AnyStubId discoverFile() {
        AnyStubId effectiveId = null;
        String filename = null;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement s : stackTrace) {
            if (s.getMethodName().startsWith("lambda$")) {
                continue;
            }
            try {
                Class<?> aClass = Class.forName(s.getClassName());

                AnyStubId methodId = methodInfo(s, aClass);

                if (methodId != null) {
                    if (methodId.filename().isEmpty()) {
                        boolean testFilePrefix = ConfigFileUtil.get(methodId.config()).testFilePrefix;
                        if (testFilePrefix) {
                            AnyStubId classId = aClass.getDeclaredAnnotation(AnyStubId.class);
                            String prefix = (classId == null || classId.filename().isEmpty()) ?
                                    aClass.getSimpleName() :
                                    classId.filename();
                            filename = prefix + "-" + s.getMethodName();
                        } else {
                            filename = s.getMethodName();
                        }
                    } else {
                        filename = methodId.filename();
                    }
                    effectiveId = methodId;
                } else {
                    AnyStubId classId = aClass.getDeclaredAnnotation(AnyStubId.class);
                    if (classId != null) {
                        String prefix = classId.filename().isEmpty() ?
                                aClass.getSimpleName() :
                                classId.filename();
                        String suffix = s.getMethodName().startsWith("<") ||
                                !ConfigFileUtil.get(classId.config()).testFilePrefix ?
                                "":
                                "-"+s.getMethodName();
                        filename = prefix + suffix;
                        effectiveId = classId;
                    }
                }
            } catch (ClassNotFoundException ignored) {
                // it's acceptable that some class/method is not found
                // need to investigate when that happens
            }
            if (filename != null) {
                break;
            }
        }
        if (effectiveId == null) {
            return null;
        }

        if (!filename.endsWith(".yml")) {
            filename += ".yml";
        }


        TestSettings testSettings = ConfigFileUtil.get(effectiveId.config());
        String [] effectiveMasks = combineArrays(effectiveId.requestMasks(), testSettings.requestMask);

        return AnyStubIdData.builder()
                .setFilename(filename)
                .setRequestMode(effectiveId.requestMode())
                .setParamMasks(effectiveMasks)
                .setConfig(effectiveId.config())
                .build();
    }

    public static String[] combineArrays(String [] one, String[] two) {
        if (two.length == 0) {
            return one;
        }
        List<String> strings = new ArrayList<>();
        strings.addAll(asList(one));
        strings.addAll(asList(two));
        return strings.stream().distinct().toArray(String[]::new);
    }

    /**
     *
     * @param s current stack trace element
     * @param aClass related class for the stack trace element
     * @return method's annotation
     */
    private static AnyStubId methodInfo(StackTraceElement s, Class<?> aClass) {

        return Arrays.stream(aClass.getDeclaredMethods())
                .filter(method -> method.getName().equals(s.getMethodName()))
                .filter(method -> method.getAnnotation(AnyStubId.class) != null)
                .findFirst()
                .map(method -> method.getAnnotation(AnyStubId.class))
                .orElse(null);
    }

    /**
     * looks for runtime data about current stub file in the call point with discoverFile().
     * if the runtime data found update the filename with given suffix
     *
     * @param stubSuffix suffix to be added to stub's filename
     * @return runtime data with filename added suffix, null if no metadata found
     */
    public static AnyStubId discoverFile(String stubSuffix) {
        AnyStubId s = discoverFile();

        if (s == null || stubSuffix == null) {
            return s;
        }
        String filename = s.filename();
        if (filename.endsWith(".yml")) {
            filename = String.format("%s-%s.yml", filename.substring(0, filename.length() - 4), stubSuffix);
        } else {
            filename = String.format("%s-%s", s, stubSuffix);
        }
        return AnyStubIdData.builder()
                .setConfig(filename)
                .setRequestMode(s.requestMode())
                .setParamMasks(s.requestMasks())
                .setConfig(s.config())
                .build();
    }
}
