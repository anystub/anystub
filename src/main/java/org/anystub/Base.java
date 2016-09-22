package org.anystub;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

/**
 * provide basic access to stub-file
 * <p>
 * methods put/get* allow work with in-memory cache
 * methods request* allow get/keep data in file
 * <p>
 * you can control case of using file-cache by constrain:
 * - rmNew  first seeking in cache if failed make real request
 * - rmNone  seeking in cache if failed throw {@link NoSuchElementException}
 * - rmAll  make real request without seeking in cache (use it for logging )
 * <p>
 * * most of the methods return this to cascading operations
 * <p>
 * Created by Kirill on 9/2/2016.
 */
public class Base {

    private static Logger log = Logger.getLogger(Base.class.getName());
    private List<Document> documentList = new ArrayList<>();
    private List<Document> requestHistory = new ArrayList<>();
    private final String filePath;
    private boolean isNew = true;
    private boolean seekInCache = true;
    private boolean writeInCache = true;

    public enum RequestMode {
        /**
         * general using.
         * send all requests to real system that aren't in cache
         */
        rmNew,

        /**
         * strict checking
         * sending requests to real system is forbidden
         */
        rmNone,

        /**
         * logging
         * all requests are sent to real system and written to file
         */
        rmAll
    }

    public Base() {
        filePath = "src/test/resources/anystub/stub.yml";
    }

    /**
     * if filename holds only filename (without path) then creates file in src/test/resources/anystub/
     * examples:
     * - new Base("./stub.yml") uses file in current dir
     * - new Base("stub.yml") uses src/test/resources/anystub/stub.yml
     *
     * @param filename used file name
     */
    public Base(String filename) {
        File file = new File(filename);
        if (!file.getParentFile().getName().isEmpty()) {
            this.filePath = file.getPath();
        } else {
            this.filePath = new File("src/test/resources/anystub") + file.getName();
        }
    }

    public Base(String path, String filename) {
        this.filePath = new File(path).getPath() + new File(filename).getPath();
    }


    /**
     * set constrains for using local cache
     * * if set rmNone loading of file occurs immediately
     *
     * @param requestMode {@link RequestMode}
     * @return this to cascade operations
     */
    public Base constrain(RequestMode requestMode) {
        switch (requestMode) {

            case rmNew:
                seekInCache = true;
                writeInCache = true;
                break;
            case rmNone:
                seekInCache = true;
                writeInCache = false;
                init();
                break;
            case rmAll:
                seekInCache = false;
                writeInCache = true;
                break;
        }

        return this;
    }

    public Base put(Document document) {
        documentList.add(document);
        isNew = false;
        return this;
    }

    /**
     * keeps [0..count-1] as keys, the last element as value
     *
     * @param keysAndValue keys for request2
     * @return this
     */
    public Base put(String... keysAndValue) {
        return put(new Document(Arrays.copyOf(keysAndValue, keysAndValue.length - 1))
                .setValues(keysAndValue[keysAndValue.length - 1]));
    }

    public Base put(Throwable ex, String... keys) {
        return put(new Document(ex, keys));
    }

    public Optional<String> getOpt(String... keys) {
        return documentList.stream()
                .filter(x -> x.keyEqual_to(keys))
                .map(Document::get)
                .findFirst();
    }

    public String get(String... keys) {
        return getVals(keys).next();
    }

    public Iterator<String> getVals(String... keys) throws NoSuchElementException {
        return getDocument(keys)
                .get()
                .getVals();
    }

    private Optional<Document> getDocument(String... keys) {
        return documentList.stream()
                .filter(x -> x.keyEqual_to(keys))
                .findFirst();
    }

    /**
     * requests string from stub
     *
     * @param keys keys for searching response in stub
     * @param <E> type of allowed Exception
     * @return requested response
     * @throws E allowed Exception
     */
    public <E extends Exception> String request(String... keys) throws E {
        return request(Base::throwNSE,
                values -> values,
                Base::throwNSE,
                keys);

    }

    public <E extends Exception> String request(Supplier<String, E> supplier, String... keys) throws E {
        return request(supplier,
                values -> values,
                s -> s,
                keys);
    }

    public <E extends Exception> String[] requestArray(String... keys) throws E {
        return request2(Base::throwNSE,
                values -> StreamSupport.stream(values.spliterator(), false).collect(Collectors.toList()).toArray(new String[0]),
                Base::throwNSE,
                keys);

    }

    public <E extends Exception> String[] requestArray(Supplier<String[], E> supplier, String... keys) throws E {
        return request2(supplier,
                values -> StreamSupport.stream(values.spliterator(), false).collect(Collectors.toList()).toArray(new String[0]),
                Arrays::asList,
                keys);

    }

    /**
     * Only recover object from stub
     *
     * @param decoder recover object from strings
     * @param keys key for creating request
     * @param <T> type of requested object
     * @param <E> type of thrown Exception by {@link java.util.function.Supplier}
     * @return requested object
     * @throws E thrown Exception by {@link java.util.function.Supplier}
     */
    public <T, E extends Throwable> T request(DecoderSimple<T> decoder,
                                              String... keys) throws E {
        return request2(Base::throwNSE,
                values -> decoder.decode(values.iterator().next()),
                Base::throwNSE,
                keys
        );
    }


    /**
     * use the method to serialize object to one line
     *
     * @param supplier provide real answer
     * @param decoder  create object from one line
     * @param encoder  serialize object to one line
     * @param keys     key of object
     * @param <T>      Type of Object
     * @param <E>      thrown exception by supplier
     * @return result from recovering from stub or from supplier
     * @throws E exception from stub or from supplier
     */
    public <T, E extends Throwable> T request(Supplier<T, E> supplier,
                                              DecoderSimple<T> decoder,
                                              EncoderSimple<T> encoder,
                                              String... keys) throws E {
        return request2(supplier,
                values -> decoder.decode(values.iterator().next()),
                t -> asList(encoder.encode(t)),
                keys
        );
    }

    /**
     * use the method to serialize object to multi lines
     *
     * @param supplier provide real answer
     * @param decoder  create object from values
     * @param encoder  serialize object
     * @param keys     key of object
     * @param <T>      Type of Object
     * @param <E>      thrown exception by supplier
     * @return result from recovering from stub or from supplier
     * @throws E exception from stub or from supplier
     */
    public <T, E extends Throwable> T request2(Supplier<T, E> supplier,
                                               Decoder<T> decoder,
                                               Encoder<T> encoder,
                                               String... keys) throws E {

        log.finest(() -> String.format("request executing: %s", Arrays.stream(keys).collect(Collectors.joining(","))));

        if (isNew()) {
            init();
        }

        requestHistory.add(new Document(keys));

        if (seekInCache) {

            Optional<Document> opt = getDocument(keys);
            if (opt.isPresent()) {
                ArrayList<String> ar = new ArrayList<>();
                opt.get().getVals().forEachRemaining(ar::add);
                return decoder.decode(ar);
            }
        }

        if (!writeInCache) {
            throwNSE();
        }

        // execute
        T res;
        try {
            res = supplier.get();
        } catch (Throwable ex) {
            put(ex, keys);
            throw ex;
        }

        // extract values
        put(new Document(keys).setValues(encoder.encode(res)));
        try {
            save();
        } catch (IOException ex) {
            log.warning("keep data failed: " + ex.getMessage());
        }
        return res;
    }


    /**
     * reload stub-file - IOException exceptions are suppressed
     */
    private void init() {
        try {
            load();
        } catch (IOException e) {
            log.info("init: loading failed");
        }
    }

    /**
     * reload file
     * history and kept documents are removed
     *
     * @throws IOException due to file access error
     */
    public void load() throws IOException {

        clear();
        try (InputStream input = new FileInputStream(new File(filePath))) {
            Constructor constructor = new Constructor(Document.class);
            TypeDescription docDescription = new TypeDescription(Document.class);
            docDescription.putListPropertyType("keys", String.class);
            docDescription.putListPropertyType("values", String.class);
            docDescription.putListPropertyType("exception", String.class);
            constructor.addTypeDescription(docDescription);
            Yaml yaml = new Yaml(constructor);
            yaml.loadAll(input)
                    .forEach(x -> documentList.add((Document) x));

            isNew = false;
        } catch (FileNotFoundException e) {
            log.info("stub file not found: " + filePath);
        }
    }


    /**
     * rewrite stub file
     *
     * @throws IOException due to file access error
     */
    public void save() throws IOException {
        File file = new File(filePath);
        File path = file.getParentFile();

        if (path != null && !path.exists()) {
            if (path.mkdirs())
                log.info("dirs created");
            else
                throw new RuntimeException("dirs for stub isn't created");
        }
        if (!file.exists()) {
            if (file.createNewFile())
                log.info("stub file is created");
            else
                throw new RuntimeException("stub file isn't created");
        }

        try (FileWriter output = new FileWriter(file)) {
            Yaml yaml = new Yaml(new Constructor(Document.class));
            yaml.dumpAll(documentList.iterator(), output);
        }
    }

    /**
     * during invoke requests correspondent file is loaded. if load is successful - isNew returns false
     *
     * @return true if buffer is clean, file isn't loaded and no data keeps in it
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * clear buffer, set isNew to true
     */
    public void clear() {
        documentList.clear();
        requestHistory.clear();
        isNew = true;
    }

    /**
     * use it instead of {@link EncoderSimple}
     * @param e nothing
     * @param <T> nothing
     * @param <E> nothing
     * @return nothing
     */
    public static <T, E> T throwNSE(E e) {
        throw new NoSuchElementException();
    }

    /**
     * use it instead of your {@link java.util.function.Supplier} in requests
     * @param <T> type for matching
     * @return nothing
     */
    public static <T> T throwNSE() {
        throw new NoSuchElementException();
    }

    /**
     *
     * @return stream of all requests
     */
    public Stream<Document> history() {
        return requestHistory.stream();
    }

    /**
     * requests that exa
     * @param keys keys for searching requests (exactly matching)
     * @return stream of requests
     */
    public Stream<Document> history(String... keys) {
        return history()
                .filter(x -> x.keyEqual_to(keys));
    }

    /**
     * requests with given keys
     * * if no keys then amount of all requests.
     * * key could be skipped if you set correspondent value to null.
     * * times(null) and times(null,null) are different, cause looking for requests with
     * amount of keys no less then in keys array.
     *
     * @param keys keys for matching requests
     * @return stream of matched requests
     */
    public Stream<Document> match(String... keys) {
        if (keys == null || keys.length == 0) {
            return history();
        }
        return history()
                .filter(x -> x.match_to(keys));
    }

    /**
     * amount of requests with given keys
     * * if no keys then amount of all requests.
     * * key could be skipped if you set correspondent value to null.
     * * times(null) and times(null,null) are different, cause looking for requests with
     * amount of keys no less then in keys array.
     *
     * @param keys keys for matching requests
     * @return amount of matched requests
     */
    public long times(final String... keys) {
        return match(keys)
                .count();
    }


    /**
     * invokes request, uses reflection to serialize/deserialize object
     * * {@link UnsupportedOperationException}
     * @param supplier produce response (ex. query real remote system)
     * @param keys id of request
     * @param <T> type of produced result
     * @param <E> type of allowed Exception
     * @return requested object
     * @throws E occurs in real system or created from
     */
    public <T, E extends Throwable> T requestMapped(Supplier<T, E> supplier,
                                                    String... keys) throws E {
        throw new UnsupportedOperationException();
    }

}
