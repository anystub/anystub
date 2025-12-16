package org.anystub.jdbc;

import java.sql.RowId;
import java.util.Arrays;

public class StubRowId implements RowId {
    private final byte[] rowid;

    public StubRowId(byte[] rowid) {
        this.rowid = rowid;
    }

    @Override
    public byte[] getBytes() {
        return rowid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StubRowId stubRowId = (StubRowId) o;
        return Arrays.equals(rowid, stubRowId.rowid);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(rowid);
    }
}
