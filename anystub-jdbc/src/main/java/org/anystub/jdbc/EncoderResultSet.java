package org.anystub.jdbc;

import org.anystub.Encoder;

import java.sql.ResultSet;

public class EncoderResultSet implements Encoder<ResultSet> {
    @Override
    public Iterable<String> encode(ResultSet resultSet) {
        return ResultSetUtil.encode(resultSet);
    }
}
