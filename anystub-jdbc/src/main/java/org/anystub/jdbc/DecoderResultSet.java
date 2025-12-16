package org.anystub.jdbc;

import org.anystub.Decoder;

import java.sql.ResultSet;

public class DecoderResultSet implements Decoder<ResultSet> {
    @Override
    public ResultSet decode(Iterable<String> values) {
        return ResultSetUtil.decode(values);
    }
}
