package org.anystub;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Document for keeping requests/response
 */
public class Document {

    private static final Logger logger = Logger.getLogger(Document.class.getName());
    private final List<String> keys = new ArrayList<>();
    private final List<String> exception = new ArrayList<>();
    private final List<String> values = new ArrayList<>();

    public Document() {
        // an explicit declaration. to be consistent
    }

    /**
     * creates a Document with nullValue
     * @param keys list of strings to add to a document as a key
     */
    public Document(String... keys) {
        this.keys.addAll(asList(keys));

    }

    public Document(Throwable ex, String... keys) {
        this.keys.addAll(asList(keys));

        this.exception.add(ex.getClass().getCanonicalName());
        this.exception.add(ex.getMessage());
    }

    public Document(Map<String, Object> document) {
        this.assign(document);
    }

    /**
     *
     * @param keys list of strings to add to a document as a key
     * @param values if values == null the Document will include null as a value
     */
    public Document(String[] keys, String[] values) {
        this.keys.addAll(asList(keys));
        this.values.addAll(asList(values));
    }

    public List<String> getKey() {
        return new DocumentKey(this.keys);
    }

    /**
     * Creates new Document.
     * * treats to keysAndValue[0..count-1] as keys of new Document, the last element as the value of the Document
     *
     * @param keysAndValue
     * @return
     */
    public static Document fromArray(String... keysAndValue) {
        return new Document(Arrays.copyOf(keysAndValue, keysAndValue.length - 1), Arrays.copyOfRange(keysAndValue, keysAndValue.length - 1, keysAndValue.length));
    }

    /**
     * for internal use
     *
     * @return description of Exception
     */
    public Iterable<String> getException() {
        return exception;
    }

    private Document setNull() {
        this.values.clear();
        return this;
    }

    public boolean isNullValue() {
        return this.values.isEmpty()  && this.exception.isEmpty();
    }

    /**
     * returns the first value from value array
     *
     * @return
     */
    public String get() {
        return getVals().iterator().next();
    }

    /**
     * returns all values as string
     * if document holds an exception throw the exception
     * if document is empty (no values, no exceptions) returns null
     * @return
     * @param <E>
     * @throws E
     */
    public <E extends Throwable> Iterable<String> getVals() throws E {
        return getValues();
    }
    private  <E extends Throwable> List<String> getValues() throws E {
        if (exception.isEmpty()) {
            if (isNullValue()) {
                return null;
            }
            return values;
        }

        try {
            Class<?> aClass = Class.forName(exception.get(0));

            Constructor<?> constructor = aClass.getConstructor(String.class);
            Object o = constructor.newInstance(exception.get(1));
            throw (E) o;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            String msg = String.format("exception class %s not found, RuntimeException is thrown", exception.get(0));
            logger.warning(() -> msg);
            throw new RuntimeException(exception.get(1), e);
        }

    }

    /**
     * returns value of the key on given position
     * @param pos can be negative, then calculates from the end, -1 points the last element
     * @return
     */
    public String getVal(int pos) {
        if (pos>=this.values.size() ||
                pos < -this.values.size()) {
            return "";
        }
        List<String> strings = getValues();
        if (strings == null) {
            return "";
        }
        if (pos<0) {
            return strings.get(this.values.size()+pos);
        }
        return strings.get(pos);
    }

    public boolean keyEqual_to(String... keys) {
        return this.keys.equals(asList(keys));
    }

    /**
     * exact matching the document to given key.
     * the document is not matched to 'keys' if:
     * - it has fewer values in key then argument array
     * - it has at least one value in its key that's not equal to correspondent non-null value in argument array
     * * null values in argument array aren't used for matching
     *
     * @param keys keys for matching (null values are skipped from matching but length of key is compared)
     * @return true if document is matched
     */
    public boolean match_to(String... keys) {
        return Document.match_to(this.keys, keys);
    }

    /**
     * exact matching a List to given matchedKeys.
     * the baseList is not matched to 'matchedKeys' if:
     * - it has fewer values in then matchedKeys
     * - it has at least one value in its key that's not equal to correspondent non-null value in matchedKeys
     * * null values in matchedKeys aren't used for matching
     *
     * @param baseList
     * @param matchedKeys for matching (null values are skipped from matching but length of key is compared)
     * @return true if document is matched
     */
    public static boolean match_to(List<String> baseList, String[] matchedKeys) {
        if (baseList.size() < matchedKeys.length) {
            return false;
        }
        for (int i = 0; i < matchedKeys.length; i++) {
            if (matchedKeys[i] == null) {
                continue;
            }
            if (!matchedKeys[i].equals(baseList.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * match documents using keys
     * matching uses regexp
     *
     * @param keys keys for matching
     * @return true id document is matched
     */
    public boolean matchEx_to(String... keys) {
        if (keys == null || keys.length == 0) {
            return true;
        }
        if (this.keys.size() < keys.length) {
            return false;
        }
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null) {
                continue;
            }
            if (!Pattern.matches(keys[i], this.keys.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * match documents using keys and values
     * matching uses regexp
     *
     * @param keys   values for matching by keys
     * @param values values for matching by values
     * @return true if document is matched
     */
    public boolean matchEx_to(String[] keys, String[] values) {
        if (isNullValue()) {
            return values == null;
        }

        if (this.values.size() < values.length) {
            return false;
        }
        if (!matchEx_to(keys)) {
            return false;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                continue;
            }
            if (!Pattern.matches(values[i], this.values.get(i))) {
                return false;
            }
        }
        return true;
    }

    public void assert_to(String... keys) {
        if (!match_to(keys)) {
            fail(keys);
        }
    }

    public void assertEx_to(String... keys) {
        if (!matchEx_to(keys)) {
            fail(keys);
        }
    }

    public void assertEx_to(String[] keys, String[] values) {
        if (!matchEx_to(keys, values)) {
            if (!matchEx_to(keys)) {
                fail(keys);
            } else {
                fail_value(values);
            }
        }
    }

    private void fail(String... keys) {
        String msg;

        if (this.keys.size() < keys.length) {
            msg = "number values in key expected: "
                    + keys.length + " and more"
                    + " but was: "
                    + this.keys.size();
        } else {
            msg = "expected: "
                    + key_to_string(keys)
                    + " but was: "
                    + key_to_string();
        }
        throw new AssertionError(msg);
    }

    private void fail_value(String... values) {
        String msg;

        if (this.keys.size() < values.length) {
            msg = "number values in value expected: "
                    + values.length + " and more"
                    + " but was: "
                    + this.keys.size();
        } else {
            msg = "expected value: "
                    + key_to_string(values)
                    + " but was: "
                    + key_to_string();
        }
        throw new AssertionError(msg);
    }

    /**
     * returns value of the key on given position
     * @param pos can be negative, then calculates from the end, -1 points the last element
     * @return
     */
    public String getKey(int pos) {
        if (pos>=this.keys.size() || pos < -this.keys.size()) {
            return "";
        }
        if (pos<0) {
            return this.keys.get(this.keys.size()+pos);
        }
        return this.keys.get(pos);
    }

    public String key_to_string() {
        if (isNullValue()) {
            return "null";
        }
        return key_to_string(keys.toArray(new String[0]));
    }

    public static String key_to_string(String... keys) {
        if (keys == null) {
            return "null";
        } else if (keys.length == 0) {
            return "[]";
        } else if (keys.length == 1) {
            return keys[0];
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(String.join(", ", keys));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        Document that = (Document) other;

        if (keys.isEmpty() && that.keys.isEmpty()) {
            return true;
        }
        return keys.equals(that.keys);
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    /**
     * returns an array of strings
     * if keys is null or an empty array return an empty array
     *
     * @param keys values of array
     * @return array of string
     */
    public static String[] ars(String... keys) {
        if (keys == null || keys.length == 0) {
            return new String[0];
        }
        return keys;
    }

    /**
     * @return a single element array with the element null
     */
    public static String[] arsNull() {
        return new String[]{null};
    }


    /**
     * return array of string
     *
     * @param keys values for result string, strings are copied into result array, Integers are transformed to
     *             correspondent amount of null values
     * @return array of string
     * @throws UnsupportedOperationException occurs if there is an key with type rather then String or Integer
     */
    public static String[] aro(Object... keys) throws UnsupportedOperationException {
        if (keys == null || keys.length == 0) {
            return new String[0];
        }
        ArrayList<String> result = new ArrayList<>();
        for (Object o : keys) {
            if (o instanceof String) {
                result.add((String) o);
            } else if (o instanceof Integer) {
                result.addAll(asList(new String[(Integer) o]));
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return result.toArray(new String[0]);
    }


    public Map<String, Object> toMap() {
        TreeMap<String, Object> res = new TreeMap<>();
        if (keys.size() == 1) {
            res.put("keys", keys.get(0));
        } else {
            res.put("keys", keys);
        }

        if (isNullValue()) {
            res.put("values", null);
        } else if (values.size() == 1 && values.get(0) != null) {
            res.put("values", values.get(0));
        } else {
            res.put("values", values);
        }

        res.put("exception", exception);

        return res;
    }


    private void assign(Map<String, Object> document) {
        if (document.get("keys") instanceof Iterable) {
            ((Iterable) document.get("keys"))
                    .forEach(x -> this.keys.add((String) x));
        } else {
            this.keys.add((String) document.get("keys"));
        }

        if (document.get("values") == null) {
            this.setNull();
        } else if (document.get("values") instanceof Iterable) {
            ((Iterable) document.get("values"))
                    .forEach(x -> this.values.add((String) x));
        } else {
            this.values.add((String) document.get("values"));
        }

        if (document.get("exception") instanceof Iterable) {
            ((Iterable) document.get("exception"))
                    .forEach(x -> this.exception.add((String) x));
        } else {
            this.exception.add((String) document.get("exception"));
        }

    }

    public static class DocumentKey extends AbstractList<String> {

        private final List<String> list;

        public DocumentKey(List<String> list) {
            this.list = list;
        }

        @Override
        public String get(int index) {
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

}
