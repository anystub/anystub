package org.anystub;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

public class ConfigFileUtil {

    private static final ConcurrentHashMap<String, TestSettings> configs = new ConcurrentHashMap<>();


    private ConfigFileUtil() {

    }

    /**
     * returns configuration from a given file
     *
     * @param filename - filename inside resources/anystub/ ; missing yml extension and starting dot will be added
     * @return
     */
    public static TestSettings get(String filename) {
        Objects.requireNonNull(filename);
        return configs.computeIfAbsent(filename, s -> {
            if (!s.startsWith(".")) {
                s = "." + s;
            }
            if (!s.endsWith(".yml")) {
                s = s + ".yml";
            }
            AnystubCfg load = new AnystubCfg();
            try (InputStream resourceAsStream = ConfigFileUtil.class.getClassLoader().getResourceAsStream("anystub/" + s)) {

                load = load(resourceAsStream);
            } catch (IOException ex) {
                Logger.getLogger(ConfigFileUtil.class.getName())
                        .finest(() -> String.format("can't load default properties from '%s': %s", filename, ex.getMessage()));

            } catch (YAMLException ex) {
                java.util.function.Supplier<String> runnable = () -> String.format("can't load default properties from '%s': %s", filename, ex.getMessage());

                if (ex.getCause() instanceof IOException) {
                    Logger.getLogger(ConfigFileUtil.class.getName())
                            .finest(runnable);
                } else {
                    Logger.getLogger(ConfigFileUtil.class.getName())
                            .warning(runnable);
                }
            }
            return TestSettings
                    .builder()
                    .setHeaders(ifNull(load.headers, StringOrArray::get, new String[0]))
                    .setRequestMask(ifNull(load.requestMask, StringOrArray::get, new String[0]))
                    .setBodyTrigger(ifNull(load.bodyTrigger, StringOrArray::get, new String[0]))
                    .setBodyMethods(ifNull(load.bodyMethods,
                            v -> v.get().length == 0 ?
                                    new String[]{"POST", "PUT", "DELETE"} :
                                    v.get(), new String[0]))
                    .setTestFilePrefix(ifNull(load.testFilePrefix, v -> v, true))
                    .build();
        });
    }

    static <T, R> R ifNull(T v, Function<T, R> f, R def) {
        if (v == null) {
            return def;
        }
        return f.apply(v);
    }

    /**
     * loads configuration from given yml file
     *
     * @param s - full path
     * @return config - only present values are defined. Values which missing in the config are null
     * if config is not present or invalid all values are null
     */
    public static AnystubCfg load(String s) {
        try (InputStream input = new FileInputStream(s)) {
            return load(input);
        } catch (IOException ex) {
            Logger.getLogger(ConfigFileUtil.class.getName())
                    .finest(() -> String.format("can't load default properties from %s: %s", s, ex.getMessage()));
        } catch (YAMLException ex) {
            Logger.getLogger(ConfigFileUtil.class.getName())
                    .warning(() -> String.format("can't load default properties from %s: %s", s, ex.getMessage()));
        }
        return new AnystubCfg();
    }

    /**
     * loads configuration from given yml file
     *
     * @param input - stream of the configuration file
     * @return config - only present values are defined. Values which missing in the config are null
     * if config is not present or invalid all values are null
     * <p>
     * note for StringOrArray properties:
     * - if property not defined in config - it is defined empty
     * - if property defined in config but no values - it is set to null
     */
    public static AnystubCfg load(InputStream input) {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(loaderOptions);
        PropertyUtils propertyUtils = new PropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        constructor.setPropertyUtils(propertyUtils);
        Yaml yaml = new Yaml(constructor);
        AnystubCfg anystubCfg = yaml.loadAs(input, AnystubCfg.class);
        if (anystubCfg == null) {
            return new AnystubCfg();
        }
        return anystubCfg;
    }


    static class AnystubCfg {
        public StringOrArray headers = new StringOrArray();
        public StringOrArray bodyTrigger = new StringOrArray();
        public StringOrArray requestMask = new StringOrArray();
        public StringOrArray bodyMethods = new StringOrArray();

        /**
         * whatever add test-class-name as prefix to a sub file
         */
        public Boolean testFilePrefix;

        /**
         * reserved for server storage-mode
         */
        public final Boolean packagePrefix = false;
        /**
         * reserved for server storage-mode
         */
        public final String stubServer = "";

        @Override
        public String toString() {
            return "AnystubCfg{" +
                    "headers=" + headers +
                    ", bodyTrigger=" + bodyTrigger +
                    ", requestMask=" + requestMask +
                    ", bodyMethods=" + bodyMethods +
                    ", testFilePrefix=" + testFilePrefix +
                    '}';
        }
    }

    public static class StringOrArray {
        final String[] data;

        public String[] get() {
            return data;
        }

        public StringOrArray() {
            this.data = new String[0];
        }

        public StringOrArray(String data) {
            if (data == null) {
                this.data = new String[0];
                return;
            }


            this.data = new String[]{data};
        }

        public StringOrArray(String... data) {
            this.data = data;
        }

        public StringOrArray(String data,
                             String data1) {
            this.data = new String[]{data,
                    data1};
        }

        public StringOrArray(String data,
                             String data1,
                             String dataN) {
            this.data = new String[]{data,
                    data1,
                    dataN};
        }

        public StringOrArray(String data,
                             String data1,
                             String data2,
                             String dataN) {
            this.data = new String[]{data,
                    data1,
                    data2,
                    dataN};
        }

        public StringOrArray(String data,
                             String data1,
                             String data2,
                             String data3,
                             String dataN) {
            this.data = new String[]{data,
                    data1,
                    data2,
                    data3,
                    dataN};
        }

        public StringOrArray(String data,
                             String data1,
                             String data2,
                             String data3,
                             String data4,
                             String dataN) {
            this.data = new String[]{data,
                    data1,
                    data2,
                    data3,
                    data4,
                    dataN};
        }

        public StringOrArray(String data,
                             String data1,
                             String data2,
                             String data3,
                             String data4,
                             String data5,
                             String dataN) {
            this.data = new String[]{data,
                    data1,
                    data2,
                    data3,
                    data4,
                    data5,
                    dataN};
        }

        public StringOrArray(String data,
                             String data1,
                             String data2,
                             String data3,
                             String data4,
                             String data5,
                             String data6,
                             String dataN) {
            this.data = new String[]{data,
                    data1,
                    data2,
                    data3,
                    data4,
                    data5,
                    data6,
                    dataN};
        }

        public StringOrArray(String data,
                             String data1,
                             String data2,
                             String data3,
                             String data4,
                             String data5,
                             String data6,
                             String data7,
                             String dataN) {
            this.data = new String[]{data,
                    data1,
                    data2,
                    data3,
                    data4,
                    data5,
                    data6,
                    data7,
                    dataN};
        }

        public StringOrArray(String data,
                             String data1,
                             String data2,
                             String data3,
                             String data4,
                             String data5,
                             String data6,
                             String data7,
                             String data8,
                             String dataN) {
            this.data = new String[]{data,
                    data1,
                    data2,
                    data3,
                    data4,
                    data5,
                    data6,
                    data7,
                    data8,
                    dataN};
        }

        @Override
        public String toString() {
            return "StringOrArray{" +
                    "data=" + Arrays.toString(data) +
                    '}';
        }
    }


}
