package org.anystub;

import org.anystub.mgmt.BaseManagerImpl;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;

/**
 * provide basic access to stub-file
 *
 * methods put/get* allow work with in-memory cache
 * methods request* allow get/keep data in file
 *
 * you can control case of using file-cache by constrain:
 * - rmNew  first seek in cache if failed make real request
 * - rmNone  first seek in cache if failed throw {@link NoSuchElementException}
 * - rmAll  makes real request without seeking in cache (use it for logging), keep all requests in the stub
 *
 * * most of the methods return this to cascading operations
 *
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
         * Use case: general using of cache.
         * request is sent to real system if it is not found in cache
         */
        rmNew,

        /**
         * Use case: strict checking.
         * sending requests to real system is forbidden.
         * cache is loaded immediately. if request is not found in cache then exception is thrown
         */
        rmNone,

        /**
         * Use case: cache logging from upstream.
         * all requests are sent to real system. all responses recorded to cache
         */
        rmAll
    }

    public Base() {
        filePath = "src/test/resources/anystub/stub.yml";
        BaseManagerImpl.instance().register(this);

    }

    /**
     * if filename holds only filename (without path) then creates file in src/test/resources/anystub/
     * examples:
     * * new Base("./stub.yml") uses file in current dir
     * * new Base("stub.yml") uses src/test/resources/anystub/stub.yml
     *
     * @param filename used file name
     */
    public Base(String filename) {
        File file = new File(filename);
        if (file.getParentFile()==null || file.getParent().isEmpty()) {
            this.filePath = "src/test/resources/anystub/" + file.getName();
        } else {
            this.filePath = file.getPath();
        }
        BaseManagerImpl.instance().register(this);
    }

    /**
     * new Base("", "stub.yml") equal to new Base("./stub.yml")
     * @param path dir
     * @param filename file
     */
    public Base(String path, String filename) {
        this.filePath = new File(path).getPath() + new File(filename).getPath();
        BaseManagerImpl.instance().register(this);
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


    /**
     * Keeps a document in cache.
     * initialize cache
     *
     * @param document for keeping
     * @return inserted document
     */
    public Document put(Document document) {
        documentList.add(document);
        isNew = false;
        return document;
    }

    /**
     * Creates and keeps a new Document in cache.
     * considers keysAndValue[0..count-1] as keys of new Document, the last element as a value
     *
     * @param keysAndValue keys for request2
     * @return new Document
     */
    public Document put(String... keysAndValue) {
        return put(new Document(Arrays.copyOf(keysAndValue, keysAndValue.length - 1))
                .setValues(keysAndValue[keysAndValue.length - 1]));
    }

    /**
     * Creates and keeps a new Document in cache.
     * Document includes request and exception as  a response
     *
     * @param ex   exception is kept in document
     * @param keys key for the document
     * @return inserted document
     */
    public Document put(Throwable ex, String... keys) {
        return put(new Document(ex, keys));
    }

    /**
     * Finds document with given keys. if document is found then returns the 1st value from it's response.
     * If document is not found then returns empty Optional. If found document contains exception the exception will be
     * raised.
     * @param keys for search of the document
     * @return first value from document's response or empty
     */
    public Optional<String> getOpt(String... keys) {
        return documentList.stream()
                .filter(x -> x.keyEqual_to(keys))
                .map(Document::get)
                .findFirst();
    }

    public String get(String... keys) {
        return getVals(keys).next();
    }

    /**
     * Finds document with given key. If document found then returns iterator to values from the document
     *
     * @param keys for search document
     * @return values of requested document
     * @throws NoSuchElementException throws when document is not found
     */
    public Iterator<String> getVals(String... keys) throws NoSuchElementException {
        return getDocument(keys)
                .orElseThrow(NoSuchElementException::new)
                .getVals();
    }

    private Optional<Document> getDocument(String... keys) {
        return documentList.stream()
                .filter(x -> x.keyEqual_to(keys))
                .findFirst();
    }

    /**
     * Requests string from stub.
     * If this document is absent in cache throws {@link NoSuchElementException}
     *
     * @param keys keys for searching response in stub
     * @return requested response
     * @throws NoSuchElementException if document if not found in cache
     */
    public String request(String... keys) throws NoSuchElementException {
        return request(Base::throwNSE,
                values -> values,
                Base::throwNSE,
                keys);

    }

    /**
     * Requests string. looking Document in cache. If it is not found then gets value using supplier.
     * Use supplier to request real system. Use this method if response just a {@link String}

     * @param supplier method to obtain response
     * @param keys keys for document and parameters for request real system
     * @param <E> some
     * @return response from real system
     * @throws E type of expected exception
     */
    public <E extends Exception> String request(Supplier<String, E> supplier, String... keys) throws E {
        return request(supplier,
                values -> values,
                s -> s,
                keys);
    }

    public <E extends Exception> Boolean requestB(Supplier<Boolean, E> supplier, String... keys) throws E {
        return request(supplier,
                Boolean::parseBoolean,
                String::valueOf,
                keys);
    }

    public <E extends Exception> Integer requestI(Supplier<Integer, E> supplier, String... keys) throws E {
        return request(supplier,
                Integer::parseInt,
                String::valueOf,
                keys);
    }

    /**
     * requests serializable object
     * @param supplier provides requested object
     * @param keys keys for document and parameters for request real system
     * @param <T> expected type for requested object
     * @param <E> expected exception
     * @return recovered object
     * @throws E expected exception
     */
    public <T extends Serializable, E extends Exception> T requestSerializable(Supplier<T, E> supplier, String... keys) throws E {
        return request(supplier,
                Base::decode,
                Base::encode,
                keys);
    }

    /**
     * Requests array of string from stub.
     * If this document is absent in cache throws {@link NoSuchElementException}
     *
     * @param keys keys for searching response in stub
     * @param <E>  type of allowed Exception
     * @return requested response
     * @throws E if document if not found in cache
     */
    public <E extends Exception> String[] requestArray(String... keys) throws E {
        return request2(Base::throwNSE,
                values -> values == null ? null : StreamSupport.stream(values.spliterator(), false).collect(Collectors.toList()).toArray(new String[0]),
                Base::throwNSE,
                keys);

    }

    /**
     * Requests array of string. looking Document in cache. If it is not found then gets value using supplier.
     * Use supplier to request real system. Use this method if response String[]
     *
     * @param supplier provide string array from system
     * @param keys     keys for request
     * @param <E>      expected exception
     * @return string array. it could be null;
     * @throws E expected exception
     */
    public <E extends Exception> String[] requestArray(Supplier<String[], E> supplier, String... keys) throws E {
        return request2(supplier,
                values -> values == null ? null : StreamSupport.stream(values.spliterator(), false).collect(Collectors.toList()).toArray(new String[0]),
                Arrays::asList,
                keys);

    }

    /**
     * Requests from stub.
     * If Document is found uses {@link DecoderSimple} to build result. It could build object of any class
     * If this document is absent in cache throws {@link NoSuchElementException}
     *
     * @param decoder recover object from strings
     * @param keys    key for creating request
     * @param <T>     type of requested object
     * @param <E>     type of thrown Exception by {@link java.util.function.Supplier}
     * @return requested object
     * @throws E thrown Exception by {@link java.util.function.Supplier}
     */
    public <T, E extends Throwable> T request(DecoderSimple<T> decoder,
                                              String... keys) throws E {
        return request2(Base::throwNSE,
                values -> values == null ? null : decoder.decode(values.iterator().next()),
                null,
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
                values -> values == null ? null : decoder.decode(values.iterator().next()),
                t -> t == null ? null : singletonList(encoder.encode(t)),
                keys
        );
    }

    /**
     * use the method to request real system and serialize/deserialize object to multi values
     *
     * @param supplier provide real answer
     * @param decoder  create object from values
     * @param encoder  serialize object
     * @param keys     key of object
     * @param <T>      Type of Object
     * @param <E>      thrown exception by supplier
     * @return result from recovering from stub or from supplier, it could return null if it gets null from upstream and decoded
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

        if (seekInCache) {

            Optional<Document> storedDocument = getDocument(keys);
            if (storedDocument.isPresent()) {
                requestHistory.add(storedDocument.get());
                if (storedDocument.get().isNullValue()) {
                    // it's not necessarily to decode null objects
                    return null;
                }
                ArrayList<String> ar = new ArrayList<>();
                storedDocument.get().getVals().forEachRemaining(ar::add);
                return decoder.decode(ar);
            }
        }

        if (!writeInCache) {
            throwNSE();
        }

        // execute
        // it could raise any exception so need to catch Throwable
        T res;
        try {
            res = supplier.get();
        } catch (Throwable ex) {
            Document exceptionalDocument = put(ex, keys);
            requestHistory.add(exceptionalDocument);
            try {
                save();
            } catch (IOException ioEx) {
                log.warning(()->"exception information is not saved into stub: " + ioEx);
            }
            throw ex;
        }

        // keep values
        Document retrievedDocument = new Document(keys);

        Iterable<String> responseData;
        if (res == null) {
            responseData = null;
            retrievedDocument.setNull();
        } else {
            responseData = encoder.encode(res);
            retrievedDocument.setValues(responseData);
        }
        put(retrievedDocument);
        requestHistory.add(retrievedDocument);
        try {
            save();
        } catch (IOException ex) {
            log.warning(()->"exception information is not saved into stub: " + ex);
        }
        if (responseData == null) {
            return null;
        }
        return decoder.decode(responseData);
    }


    /**
     * reload stub-file - IOException exceptions are suppressed
     */
    private void init() {
        try {
            load();
        } catch (IOException e) {
            log.warning(()->"loading failed: " + e);
        }
    }

    /**
     * cleans history, reloads stub-file
     * @throws IOException due to file access error
     */
    private void load() throws IOException {
        clear();
        File file = new File(filePath);
        try (InputStream input = new FileInputStream(file)) {
            Yaml yaml = new Yaml(new SafeConstructor());
            Object load = yaml.load(input);

            if (load instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) load;
                map.forEach((k,v)-> documentList
                        .add(new Document((Map<String, Object>) v)));
            }
            isNew = false;
        }catch (FileNotFoundException e) {
            log.info(()->String.format("stub file %s is not found: %s", file.getAbsolutePath(), e));
        }
    }


    /**
     * rewrite stub-file
     *
     * @throws IOException due to file access error
     */
    public void save() throws IOException {
        File file = new File(filePath);
        File path = file.getParentFile();

        if (path != null && !path.exists()) {
            if (path.mkdirs())
                log.info(()->"dirs created");
            else
                throw new IOException("dirs for stub isn't created");
        }
        if (!file.exists()) {
            if (file.createNewFile())
                log.info(()->"stub file is created:" + file.getAbsolutePath());
            else
                throw new IOException("stub file isn't created");
        }

        try (FileWriter output = new FileWriter(file)) {
            Yaml yaml = new Yaml(new SafeConstructor());
            Map<String, Object> saveList = new LinkedHashMap<>();

            for (int i = 0; i < documentList.size(); i++) {
                saveList.put(String.format("request%d", i), documentList.get(i).toMap());
            }
            yaml.dump(saveList, output);
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
     *
     * @param e   nothing
     * @param <T> nothing
     * @param <E> nothing
     * @return nothing
     */
    public static <T, E> T throwNSE(E e) {
        throw new NoSuchElementException(e.toString());
    }

    /**
     * use it instead of your {@link java.util.function.Supplier} in requests
     *
     * @param <T> type for matching
     * @return nothing
     */
    public static <T> T throwNSE() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    /**
     * @return stream of all requests
     */
    public Stream<Document> history() {
        return requestHistory.stream();
    }

    /**
     * requests that exa
     *
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
     * requests with given keys, requests match using regexp
     *
     * @param keys keys for matching
     * @return stream of matched documents from history
     */
    public Stream<Document> matchEx(String... keys) {
        if (keys == null || keys.length == 0) {
            return history();
        }
        return history()
                .filter(x -> x.matchEx_to(keys));
    }

    /**
     * requests with given keys, requests match using regexp
     *
     * @param keys   keys for matching
     * @param values keys for matching
     * @return stream of matched documents from history
     */
    public Stream<Document> matchEx(String[] keys, String[] values) {
        return history()
                .filter(x -> x.matchEx_to(keys, values));
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
     * amount of requests with given keys
     * * if no keys then amount of all requests.
     * * key could be skipped if you set correspondent value to null.
     * * times(null) and times(null,null) are different, cause looking for requests with
     * amount of keys no less then in keys array.
     *
     * @param keys keys for matching requests
     * @return amount of matched requests
     */
    public long timesEx(final String... keys) {
        return matchEx(keys)
                .count();
    }

    /**
     * amount of requests with given keys
     * * if no keys then amount of all requests.
     * * key could be skipped if you set correspondent value to null.
     * * times(null) and times(null,null) are different, cause looking for requests with
     * amount of keys no less then in keys array.
     *
     * @param keys   values for matching requests by keys
     * @param values values for matching requests by value
     * @return amount of matched requests
     */
    public long timesEx(final String[] keys, final String[] values) {
        return matchEx(keys, values)
                .count();
    }


    /**
     * invokes request, uses reflection to serialize/deserialize object
     * * {@link UnsupportedOperationException}
     *
     * @param supplier produce response (ex. query real remote system)
     * @param keys     id of request
     * @param <T>      type of produced result
     * @param <E>      type of allowed Exception
     * @return requested object
     * @throws E occurs in real system or created from
     */
    public <T, E extends Throwable> T requestMapped(Supplier<T, E> supplier,
                                                    String... keys) throws E {
        throw new UnsupportedOperationException();
    }


    public static String encode(Serializable s) {
        try (ByteArrayOutputStream of = new ByteArrayOutputStream();
             ObjectOutputStream so = new ObjectOutputStream(of)) {
            so.writeObject(s);
            so.flush();
            return Base64.getEncoder().encodeToString(of.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Serializable> T decode(String s){
        byte[] decode = Base64.getDecoder().decode(s);
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);
            ObjectInputStream si = new ObjectInputStream(byteArrayInputStream)){
            return (T) si.readObject();
        } catch (ClassNotFoundException| IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isText(String text) {
        return text.matches("\\p{Print}*");
    }

    public String getFilePath() {
        return filePath;
    }
}
