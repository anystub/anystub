package org.anystub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.anystub.RequestMode.rmAll;
import static org.anystub.RequestMode.rmFake;
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
 */
public class Base {

    private static final Logger log = Logger.getLogger(Base.class.getName());
    private final DocumentListI documentList = new DocumentList();
    private TrackedDocumentList documentListTrackIterator;
    private final List<Document> requestHistory = new ArrayList<>();
    private final String filePath;
    /**
     * shows if any document already saved in the file
     */
    private boolean isNew = true;
    private RequestMode requestMode = rmNew;

    /**
     * delimiter between last element in existing file and new elements
     * stub could be manually modified so there could be several possible variants to make the yaml valid
     * Base calculates it on load
     * resets after the first use
     */
    private String firstYamlDelimiter = "";

    /**
     * creates stub in specific path.
     * in your test you do not need to create it directly.
     * Use org.anystub.mgmt.BaseManagerFactory.getStub() or org.anystub.mgmt.BaseManagerFactory.locate()
     * to get stub related to your context
     *
     * <p>
     * Note: Consider using  instead
     *
     * @param path the path to stub file
     */
    public Base(String path) {
        this.filePath = path;
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
                    purge();
                    break;
                case rmTrack:
                    try {
                        List<Document> documents = loadStub(filePath);
                        if (documents.isEmpty()) {
                            documentListTrackIterator = null;
                        } else {
                            documentListTrackIterator = new TrackedDocumentList(documents);
                        }
                    } catch (IOException e) {
                        documentListTrackIterator = null;
                    }
                    break;
                default:
                    break;
            }
        } else if (this.requestMode != requestMode) {
            log.warning(() -> String.format("Stub constrains change after creation for %s. Consider to split stub-files", filePath));
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
        save(document);
        return document;
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
        return documentList.getDocument(keys)
                .map(Document::get);
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
        return documentList.getDocument(keys);
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
                String.class,
                Arrays.stream(keys).toArray());
    }

    /**
     * Requests an object. It looks for a document in a stub file
     * If it is not found then requests the value from the supplier.
     * Keys and response saves as json-strings
     * Supports response generating: look at RequestMode.rmFake
     *
     * @param supplier      method which is able to return an actual response
     * @param responseClass type of the response
     * @param keys          all arguments of requested function
     * @param <R>           return type
     * @param <E>           controlled exception generator could generate
     * @return returns a response from system or recovered response from a stub
     * @throws E generates an exception if it comes from supplier or recorded in the stub
     */
    public <R, E extends Exception> R request(Supplier<R, E> supplier, Class<R> responseClass, Object... keys) throws E {

        return request(() -> {
                    try {
                        return supplier.get();
                    } catch (Exception e) {
                        if (requestMode == rmFake) {
                            return RandomGenerator.g(responseClass);
                        } else {
                            throw e;
                        }
                    }
                },
                new DecoderJson<R>(responseClass),
                new EncoderJson<>(),
                StringUtil.toArray(keys));
    }

    /**
     * Requests an object. It looks for a document in a stub file
     * If it is not found then requests the value from the supplier.
     * Keys and response saves as json-strings.
     * Supports response generating: look at RequestMode.rmFake
     *
     * @param supplier   method which is able to return an actual response
     * @param returnType type of the response
     * @param keys       all arguments of requested function
     * @param <R>        return type
     * @param <E>        controlled exception generator could generate
     * @return returns a response from system or recovered response from a stub
     * @throws E generates an exception if it comes from supplier or recorded in the stub
     */
    public <R, E extends Exception> R request(Supplier<R, E> supplier, TypeReference<R> returnType, Object... keys) throws E {
        DecoderSimple<R> d = new DecoderSimple<R>() {
            final ObjectMapper objectMapper = ObjectMapperFactory.get();

            @Override
            public R decode(String values) {
                try {
                    return objectMapper.readValue(values, returnType);
                } catch (JsonProcessingException | RuntimeException e) {
                    log.finest(() -> String.format("cannot recover object %s from %s", returnType, values));
                }
                return null;
            }
        };


        return request(() -> {
                    try {
                        return supplier.get();
                    } catch (Exception e) {
                        if (requestMode == rmFake) {
                            return RandomGenerator.g(returnType);
                        }
                        throw e;
                    }
                },
                d,
                new EncoderJson<>(),
                StringUtil.toArray(keys));


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
                Boolean.class,
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
                Integer.class,
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
     * @deprecated use request instead
     */
    @Deprecated(since = "0.7.0")
    public <T extends Serializable, E extends Exception> T requestSerializable(Supplier<T, E> supplier, String... keys) throws E {
        return request(supplier,
                StringUtil::decode,
                StringUtil::encode,
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
     * Requests an Object which could be kept in stub-file as a single string
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
     * and results in the stub-file. It uses keys to match the request in the stub-files.
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

        return request2(supplier,
                decoder,
                (t, data) -> {
                    Iterable<String> encode = encoder.encode(t);
                    return data.apply(encode, null);
                },
                keyGen);
    }

    private final Object request2lock = new Object();

    /**
     * Looks for an Object in stub-file or gets it from the supplier.
     * Uses inverter to encode to response to strings, call decodingAndSave to recover and save response
     * and decoder to convert the request and results in the stub-file.
     * Uses keysGen to get keys to match the request in the stub-files
     *
     * @param supplier - provides the value from an external system
     * @param decoder  - recovers result from stub
     * @param inverter - converts result to strings for stub-file
     * @param keyGen   - provides keys to match requested document
     * @param <T>      - type of requested object
     * @param <E>      - allowed exception
     * @return an object from stub or an external system
     * @throws E generates the exception if an external system generated it
     */
    public <T, E extends Throwable> T request2(Supplier<T, E> supplier,
                                               Decoder<T> decoder,
                                               Inverter<T> inverter,
                                               KeysSupplier keyGen) throws E {
        T t;
        synchronized (request2lock) {
            t = request2Synchronized(supplier, decoder, inverter, keyGen);
        }

        return t;
    }

    private <T, E extends Throwable> T request2Synchronized(Supplier<T, E> supplier,
                                                            Decoder<T> decoder,
                                                            Inverter<T> inverter,
                                                            KeysSupplier keyGen) throws E {

        if (requestMode == rmPassThrough) {
            return supplier.get();
        }
        KeysSupplier keyGenCashed = new KeysSupplierCashed(keyGen);

        log.finest(() -> String.format("request executing: %s", String.join(",", keyGenCashed.get())));

        if (seekInCache()) {
            init();

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
            Document next = documentListTrackIterator.extractDocument(keyGenCashed.get());
            if (next !=null && next.keyEqual_to(keyGenCashed.get())) {
                requestHistory.add(next);
                return decoder.decode(next.getVals());
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
            Document exceptionalDocument = put(new Document(ex, keyGenCashed.get()));
            requestHistory.add(exceptionalDocument);
            throw ex;
        }

        if (res == null) {
            // store values
            Document retrievedDocument;
            retrievedDocument = new Document(keyGenCashed.get());
            put(retrievedDocument);
            requestHistory.add(retrievedDocument);
            return null;
        }

        return inverter.invert(res, (responseData, ex) -> {
            if (ex != null) {
                Document exceptionalDocument = put(new Document(ex, keyGenCashed.get()));
                requestHistory.add(exceptionalDocument);
                return res;
            }
            ArrayList<String> values = new ArrayList<>();
            for (String responseDatum : responseData) {
                values.add(responseDatum);
            }
            Document retrievedDocument;
            retrievedDocument = new Document(keyGenCashed.get(), values.toArray(new String[0]));
            put(retrievedDocument);
            requestHistory.add(retrievedDocument);
            return decoder.decode(responseData);
        });
    }

    public <E extends Exception> void post(Consumer<E> consumer, Object... keys) throws E {
        request(() -> {
            consumer.run();
            return null;
        }, Void.class, keys);
    }

    /**
     * loads stub-file if required
     * NB: IOException exceptions are suppressed
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
     * if any document is loaded it is marked as non-new
     *
     * @throws IOException due to file access error
     */
    private void load() throws IOException {
        if (!isNew) {
            return;
        }
        synchronized (request2lock) {
            if (isNew) {
                try {
                    Iterable<Document> load = loadStub(filePath);

                    clear();

                    load.forEach(d -> {
                        documentList.add(d);
                        isNew = false;
                        try {
                            firstYamlDelimiter = StringUtil.nextYamlDelimiter(new File(filePath));
                        } catch (IOException e) {
                            log.severe(()-> String.format("failed to calculate yaml delimiter: %s", e.getMessage()));
                        }
                    });
                } catch (FileNotFoundException e) {
                    log.info(() -> String.format("stub file %s is not found: %s", new File(filePath).getAbsolutePath(), e));
                }
            }
        }
    }

    private static List<Document> loadStub(String filePath) throws IOException {
        File file = new File(filePath);
        try (InputStream inputStream = new FileInputStream(file);
             InputStreamReader input = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            LoaderOptions options = new LoaderOptions();

            Yaml yaml = new Yaml(new DocumentConstructor(options));
            Iterable<Object> load = yaml.loadAll(input);
            ArrayList<Document> res = new ArrayList<>();
            load.forEach(d-> {
                if (d instanceof Document) {
                    res.add((Document) d);
                }
            });
            return res;
        }
    }


    /**
     * saves document into current stub file
     * append document at the end, if stub marks as new override existing file
     *
     * @param document document to add
     * @throws IOException
     */
    private void save(Document document) {
        synchronized (request2lock) {
            File file = new File(filePath);
            File path = file.getParentFile();

            if (path != null
                    && !path.exists()
                    && path.mkdirs()) {
                log.info(() -> "dirs created");
            }

            boolean doAppend = !isNew;
            try (FileOutputStream out = new FileOutputStream(file, doAppend);
                 OutputStreamWriter output = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

                if (doAppend) {
                    String delimited = "---\n";
                    if (!firstYamlDelimiter.isEmpty()) {
                        output.append(firstYamlDelimiter);
                        firstYamlDelimiter = "";
                    }
                    output.append(delimited);

                }
                DumperOptions options = new DumperOptions();
                options.setExplicitStart(true);
                options.setExplicitEnd(true);

                Yaml yaml = new Yaml(new DocumentRepresent(options));
                yaml.dump(document, output);
                output.flush();
                isNew = false;
            } catch (IOException e) {
                log.severe(String.format("failed to record %s: %s", document.key_to_string(), e.getMessage()));
            }
        }
    }

    /**
     * if previous load() is successful of file not found then isNew returns false
     *
     * @return true if the stub-file is not loaded in memory
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * clears history and documents, sets isNew to true, which causes loading existing stub-file on the next request
     * doesn't touch appropriate file (a note: just remove a file manually if you do not need the data anymore)
     * doesn't clean properties
     */
    public void clear() {
        documentList.clear();
        requestHistory.clear();
        isNew = true;
    }

    public void purge() {
        clear();

        try {
            Files.deleteIfExists(new File(getFilePath()).toPath());
        } catch (IOException e) {
            log.finest("no file deleted on purge for: " + getFilePath());
        }

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
     * finds requests in the stub-file by keys
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
     * finds requests in the stub-file by keys. the same as {#match } but matches each string in the key using regex
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
     * amount of keys no less than in keys array.
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
     * amount of keys no less than in keys array.
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
     * amount of keys no less than in keys array.
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

    /**
     * @return returns true if it is expected to find result in cache before hitting actual system
     */
    public boolean seekInCache() {
        return requestMode == rmNew ||
                requestMode == rmNone ||
                requestMode == rmFake;
    }

    private boolean writeInCache() {
        return requestMode == rmNew ||
                requestMode == rmFake ||
                requestMode == rmAll ||
                (requestMode == rmTrack && documentListTrackIterator == null);
    }

    private boolean isTrackCache() {
        return requestMode == rmTrack && documentListTrackIterator != null;
    }

}
