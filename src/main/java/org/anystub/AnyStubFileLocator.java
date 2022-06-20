package org.anystub;

import java.lang.reflect.Method;
import java.util.Arrays;

public class AnyStubFileLocator {

    private AnyStubFileLocator() {
    }

    /**
     * looks for runtime data about current stub file in the call point.
     * If you call it in some functions it tracks stackTrace up to the first method or class annotated
     * with @AnystubId and extracts its parameters
     *
     * @return runtime data, if no annotation found returns null
     */
    public static AnyStubId discoverFile() {
        String filename = null;
        AnyStubId id = null;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement s : stackTrace) {
            try {
                if (s.getMethodName().startsWith("lambda$")) {
                    continue;
                }
                Class<?> aClass = Class.forName(s.getClassName());

                id = methodInfo(s, aClass);

                if (id != null) {
                    filename = id.filename().isEmpty() ?
                            s.getMethodName() :
                            id.filename();
                    break;
                } else {
                    id = aClass.getDeclaredAnnotation(AnyStubId.class);
                    if (id != null) {
                        filename = id.filename().isEmpty() ?
                                aClass.getSimpleName() :
                                id.filename();
                        break;
                    }
                }
            } catch (ClassNotFoundException ignored) {
                // it's acceptable that some class/method is not found
                // need to investigate when that happens
            }
        }
        if (id == null) {
            return null;
        }
        if (!filename.endsWith(".yml")) {
            filename += ".yml";
        }

        return new AnyStubIdData(filename,
                id.requestMode(),
                id.requestMasks());
    }

    private static AnyStubId methodInfo(StackTraceElement s, Class<?> aClass) {
        AnyStubId id;
        try {
            Method method;
            method = aClass.getDeclaredMethod(s.getMethodName());
            id = method.getAnnotation(AnyStubId.class);
        } catch (NoSuchMethodException ignored) {
            id = null;
        }
        if (id == null) {
            Method methodStream = Arrays.stream(aClass.getDeclaredMethods())
                    .filter(method -> method.getName().equals(s.getMethodName()))
                    .filter(method -> method.getAnnotation(AnyStubId.class) != null)
                    .findAny().orElse(null);

            if (methodStream != null) {
                id = methodStream.getAnnotation(AnyStubId.class);
            } else {
                id = null;
            }
        }
        return id;
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
        return new AnyStubIdData(filename, s.requestMode(), s.requestMasks());
    }
}
