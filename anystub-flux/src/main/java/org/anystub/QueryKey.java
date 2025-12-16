package org.anystub;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class QueryKey {
    String filename;
    List<String> keys;

    public QueryKey(String filename, List<String> keys) {
        this.filename = filename;
        this.keys = keys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryKey queryKey = (QueryKey) o;

        if (!Objects.equals(filename, queryKey.filename)) return false;
        return keys.equals(queryKey.keys);
    }

    @Override
    public int hashCode() {
        int result = filename != null ? filename.hashCode() : 0;
        result = 31 * result + (keys != null ? Arrays.hashCode(keys.toArray(new String[0])) : 0);
        return result;
    }
}
