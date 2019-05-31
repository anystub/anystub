package org.anystub;

import org.anystub.mgmt.BaseManagerImpl;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;
import static org.anystub.RequestMode.rmAll;
import static org.anystub.RequestMode.rmNew;
import static org.anystub.RequestMode.rmNone;
import static org.anystub.RequestMode.rmPassThrough;
import static org.anystub.RequestMode.rmTrack;

/**
 * provides basic access to stub-file
 * <p>
 * methods put/get* allow work with in-memory cache
 * methods request* allow get/keep data in file
 * <p>
 * Check {@link RequestMode} to find options to control get access to external system and store requests strategy
 * <p>
 */
public class Base {

    private static Logger log = Logger.getLogger(Base.class.getName());
    private List<Document> documentList = new ArrayList<>();
    private Iterator<Document> documentListTrackIterator;
    private List<Document> requestHistory = new ArrayList<>();
    private final String filePath;
    private boolean isNew = true;
    private RequestMode requestMode = rmNew;
    private List<Document> properties = new ArrayList<>();

    public Base() {
        filePath = BaseManagerImpl.getFilePath();
        BaseManagerImpl.instance().register(this);

    }

    /**
     * if filename holds only filename (without path) then creates file in src/test/resources/anystub/
     * examples:
     * * new Base("./stub.yml") uses file in current dir
     * * new Base("stub.yml") uses src/test/resources/anystub/stub.yml
     * <p>
     * Note: Consider using {@link BaseManagerImpl} instead
     *
     * @param filename used file name
     */
    public Base(String filename) {
        this.filePath = BaseManagerImpl.getFilePath(filename);
        BaseManagerImpl.instance().register(this);
    }

    /**
     * Note: Consider using {@link BaseManagerImpl} instead
     *
     * @param path     dir
     * @param filename file
     */
    public Base(String path, String filename) {
        this.filePath = BaseManagerImpl.getFilePath(path, filename);
        BaseManagerImpl.instance().register(this);
    }

    /**
     * if filename holds only filename (without path) then creates file in src/test/resources/anystub/
     * examples:
     * * new Base("./stub.yml") uses file in current dir
     * * new Base("stub.yml") uses src/test/resources/anystub/stub.yml
     * <p>
     * Note: Consider using {@link BaseManagerImpl} instead
     *
     * @param filename used file name
     * @param notRegistered must be true
     */
    public Base(String filename, boolean notRegistered) {
        if (!notRegistered) {
            throw new UnsupportedOperationException();
        }
        this.filePath = BaseManagerImpl.getFilePath(filename);
    }


    /**
     * set constrains for using cache and getting access a source system
     *
     * @param requestMode {@link RequestMode}
     * @return this to cascade operations
     */
    public Base constrain(RequestMode requestMode) {
        if (isNew()) {
            this.requestMode = requestMode;
            switch (requestMode) {
                case rmNone:
                    init();
                    break;
                case rmAll:
                    isNew = false;
                    break;
                case rmTrack:
                    init();
                    if (documentList.isEmpty()) {
                        documentListTrackIterator = null;
                    } else {
                        documentListTrackIterator = documentList.iterator();
                    }
                    break;
                default:
                    break;
            }
        } else {
            if (this.requestMode != requestMode) {
                log.warning(() -> String.format("Stub constrains change after creation for %s. Consider to split stub-files", filePath));
            }
            this.requestMode = requestMode;
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
     * treat keysAndValue[0..count-1] as keys of new Document, the last element as the value of the Document
     *
     * @param keysAndValue keys for request2
     * @return new Document
     */
    public Document put(String... keysAndValue) {
        return put(documentFromArray(keysAndValue));
    }

    public static Document documentFromArray(String... keysAndValue) {
        return new Document(Arrays.copyOf(keysAndValue, keysAndValue.length - 1))
                .setValues(keysAndValue[keysAndValue.length - 1]);
    }

    /**
     * Creates and keeps a new Document in cache.
     * Document includes request and exception as a response.
     *
     * @param ex   exception is kept in document
     * @param keys key for the document
     * @return inserted document
     */
    public Document put(Throwable ex, String... keys) {
        return put(new Document(ex, keys));
    }

    /**
     * Finds document with given keys.
     * if document is found then it returns an Optional containing the first value from the response.
     * If document is not found then it returns empty Optional.
     * If found document contains an exception the exception will be
     * raised.
     *
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
        return getVals(keys).iterator().next();
    }

    /**
     * Finds document with the given key. If document found then returns iterator to the values from the document
     *
     * @param keys for search document
     * @return values of requested document
     * @throws NoSuchElementException throws when document is not found
     */
    public Iterable<String> getVals(String... keys) throws NoSuchElementException {
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
     * Requests a string from stub.
     * If this document is absent in cache throws {@link NoSuchElementException}
     *
     * @param keys keys for searching response in a stub-file
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
     * Requests a string. It looks for a Document in a stub-file
     * If it is not found then requests the value from the supplier.
     * supplier could request the string from an external system.
     *
     * @param supplier method to obtain response
     * @param keys     keys for document and parameters for request real system
     * @param <E>      some
     * @return response from real system
     * @throws E type of expected exception
     */
    public <E extends Exception> String request(Supplier<String, E> supplier, String... keys) throws E {
        return request(supplier,
                values -> values,
                s -> s,
                keys);
    }

    /**
     * Requests Boolean
     *
     * @param supplier
     * @param keys
     * @param <E>
     * @return
     * @throws E
     */
    public <E extends Exception> Boolean requestB(Supplier<Boolean, E> supplier, String... keys) throws E {
        return request(supplier,
                Boolean::parseBoolean,
                String::valueOf,
                keys);
    }

    /**
     * requests Integer
     *
     * @param supplier
     * @param keys
     * @param <E>
     * @return
     * @throws E
     */
    public <E extends Exception> Integer requestI(Supplier<Integer, E> supplier, String... keys) throws E {
        return request(supplier,
                Integer::parseInt,
                String::valueOf,
                keys);
    }

    /**
     * Requests serializable object
     *
     * @param supplier provides requested object
     * @param keys     keys for document and parameters for request real system
     * @param <T>      expected type for requested object
     * @param <E>      expected exception
     * @return recovered object
     * @throws E expected exception
     */
    public <T extends Serializable, E extends Exception> T requestSerializable(Supplier<T, E> supplier, String... keys) throws E {
        return request(supplier,
                Util::decode,
                Util::encode,
                keys);
    }

    /**
     * Requests an array of string from stub.
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
     * Requests an array of string.
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
     * Requests an Object from stub-file.
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
     * Requests an Object which is could be kept in stub-file as a single string
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
     * Looks for an Object in stub-file or gets it from the supplier. Uses encoder and decoder to convert the request
     * and results in the stub-file. Uses keys to match the request in the stub-files
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
        return request2(supplier,
                decoder,
                encoder,
                () -> keys);
    }

    /**
     * Looks for an Object in stub-file or gets it from the supplier. Uses encoder and decoder to convert the request
     * and results in the stub-file. Uses keysGen to get keys to match the request in the stub-files
     *
     * @param supplier - provides the value from an external system
     * @param decoder  - recovers result from stub
     * @param encoder  - converts result to strings for stub-file
     * @param keyGen   - provides keys to match requested document
     * @param <T>      - type of requested object
     * @param <E>      - allowed exception
     * @return an object from stub or an external system
     * @throws E generates the exception if an external system generated it
     */
    public <T, E extends Throwable> T request2(Supplier<T, E> supplier,
                                               Decoder<T> decoder,
                                               Encoder<T> encoder,
                                               KeysSupplier keyGen) throws E {

        if (requestMode == rmPassThrough) {
            return supplier.get();
        }
        KeysSupplier keyGenCashed = new KeysSupplierCashed(keyGen);

        log.finest(() -> String.format("request executing: %s", String.join(",", keyGenCashed.get())));

        if (isNew()) {
            init();
        }

        if (seekInCache()) {

            Optional<Document> storedDocument = getDocument(keyGenCashed.get());
            if (storedDocument.isPresent()) {
                requestHistory.add(storedDocument.get());
                if (storedDocument.get().isNullValue()) {
                    // it's not necessarily to decode null objects
                    return null;
                }
                return decoder.decode(storedDocument.get().getVals());
            }
        } else if (isTrackCache()) {
            if (documentListTrackIterator.hasNext()) {
                Document next = documentListTrackIterator.next();
                if (next.keyEqual_to(keyGenCashed.get())) {
                    requestHistory.add(next);
                    return decoder.decode(next.getVals());
                }
            }
        }

        if (!writeInCache()) {
            throwNSE(Arrays.toString(keyGenCashed.get()));
        }

        // execute
        // it could raise any exception so need to catch Throwable
        T res;
        try {
            res = supplier.get();
        } catch (Throwable ex) {
            Document exceptionalDocument = put(ex, keyGenCashed.get());
            requestHistory.add(exceptionalDocument);
            try {
                save();
            } catch (IOException ioEx) {
                log.warning(() -> "exception information is not saved into stub: " + ioEx);
            }
            throw ex;
        }

        // keep values
        Document retrievedDocument = new Document(keyGenCashed.get());

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
            log.warning(() -> "exception information is not saved into stub: " + ex);
        }
        if (responseData == null) {
            return null;
        }
        return decoder.decode(responseData);
    }


    /**
     * reloads stub-file - IOException exceptions are suppressed
     */
    private void init() {
        try {
            load();
        } catch (IOException e) {
            log.warning(() -> "loading failed: " + e);
        }
    }

    /**
     * cleans history, reloads stub-file
     *
     * @throws IOException due to file access error
     */
    private void load() throws IOException {
        File file = new File(filePath);
        try (InputStream input = new FileInputStream(file)) {
            Yaml yaml = new Yaml(new SafeConstructor());
            Object load = yaml.load(input);

            if (load instanceof Map) {
                clear();
                Map<String, Object> map = (Map<String, Object>) load;
                map.forEach((k, v) -> documentList
                        .add(new Document((Map<String, Object>) v)));
                isNew = false;
            }
        } catch (FileNotFoundException e) {
            log.info(() -> String.format("stub file %s is not found: %s", file.getAbsolutePath(), e));
        }
    }


    /**
     * rewrites stub-file
     *
     * @throws IOException due to file access error
     */
    public void save() throws IOException {
        File file = new File(filePath);
        File path = file.getParentFile();

        if (path != null && !path.exists()) {
            if (path.mkdirs())
                log.info(() -> "dirs created");
            else
                throw new IOException("dirs for stub isn't created");
        }
        if (!file.exists()) {
            if (file.createNewFile())
                log.info(() -> "stub file is created:" + file.getAbsolutePath());
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
     * if previous load() is successful then isNew returns false
     *
     * @return true if the stub-file is not load in memory
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * clears buffer, set isNew to true
     * doesn't touch appropriate file (a note: just remove a file you need to remove manually)
     * doesn't clean properties
     */
    public void clear() {
        documentList.clear();
        requestHistory.clear();
        isNew = true;
    }

    /**
     * equal to: throw new NoSuchElementException(e.toString());
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
     * throw new NoSuchElementException(e.toString());
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
     * finds requests in the stub-file by keys keys
     * * if no keys provided then it returns all requests.
     * * one or more of the keys could be null. That means the matching by the key is omitted.
     * * match(null) and match(null,null) are different, match(null) searches requests with at least one string as the key
     * match(null, null) looks requests with at least two strings as the key
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
     * finds requests in the stub-file by keys keys. the same as {#match } but matches each string in the key using regex
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
     * finds requests in the stub-file, the same as {#matchEx} but uses keys and result fields to match
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
     * number of requests with given keys
     * * if no keys provided then number of all requests.
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
     * number of requests with given keys
     * * if no keys provided then number of all requests.
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
     * number of requests with given keys
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

    public String getFilePath() {
        return filePath;
    }

    private boolean seekInCache() {
        return requestMode == rmNew || requestMode == rmNone;
    }

    private boolean writeInCache() {
        return requestMode == rmNew ||
                requestMode == rmAll ||
                (requestMode == rmTrack && documentListTrackIterator == null);
    }

    private boolean isTrackCache() {
        return requestMode == rmTrack && documentListTrackIterator != null;
    }

    public Stream<Document> getProperty(String... keys) {
        return properties
                .stream()
                .filter(x -> x.match_to(keys));

    }

    /**
     * adds property as a document,
     * @param property
     */
    public void addProperty(String... property) {
        this.properties.add(documentFromArray(property));
    }
}
