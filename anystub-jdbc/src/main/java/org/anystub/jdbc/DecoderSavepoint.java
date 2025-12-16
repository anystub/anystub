package org.anystub.jdbc;

import org.anystub.Decoder;

import java.sql.Savepoint;
import java.util.Iterator;

public class DecoderSavepoint implements Decoder<Savepoint> {
    @Override
    public Savepoint decode(Iterable<String> values) {
        Iterator<String> iterator = values.iterator();
        iterator.next();
        return new StubSavepoint(Integer.parseInt(iterator.next()), iterator.next());
    }
}
