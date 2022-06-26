package org.anystub.jdbc;

import org.anystub.StringUtil;
import org.anystub.StringUtil;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

public class SqlTypeEncoder {



    private SqlTypeEncoder() {
    }

    public static Blob decodeBlob(String next) {
        byte[] bytes = StringUtil.recoverBinaryData(next);
        try {
            return new SerialBlob(bytes);
        } catch (SQLException e) {
            throw new UnsupportedOperationException("failed to recover blob", e);
        }
    }


    public static String encodeBlob(Blob blob) {
        try {
            byte[] bytes = blob.getBytes(1, (int) blob.length());

            String s = StringUtil.toCharacterString(bytes);
            blob.free();
            return s;
        } catch (SQLException e) {
            throw new UnsupportedOperationException("failed to extract blob", e);
        }
    }

    public static Clob decodeClob(String next) {
        byte[] bytes = StringUtil.recoverBinaryData(next);
        try (CharArrayWriter charArrayWriter = new CharArrayWriter()) {
            for (byte b : bytes) {
                charArrayWriter.write(b);
            }
            return new SerialClob(charArrayWriter.toCharArray());
        } catch (SQLException e) {
            throw new UnsupportedOperationException("failed to recover blob", e);
        }
    }

    public static String encodeClob(Clob clob) {
        try (Reader characterStream = clob.getCharacterStream();
             CharArrayWriter charArrayWriter = new CharArrayWriter()) {

            int i;
            while ((i = characterStream.read()) != -1) {
                charArrayWriter.write(i);
            }

            return  StringUtil.toCharacterString(charArrayWriter.toString().getBytes());
        } catch (SQLException | IOException e) {
            throw new UnsupportedOperationException("failed to extract clob", e);
        }
    }

  public static NClob decodeNClob(String next) {
        return (NClob) decodeClob(next);
    }

    public static String encodeNClob(NClob clob) {
        return encodeClob(clob);
    }


    public static RowId decodeRowid(String next) {
        byte[] bytes = StringUtil.recoverBinaryData(next);
        return new StubRowId(bytes);
    }

    public static String encodeRowid(RowId rowId) {
        return StringUtil.toCharacterString(rowId.getBytes());
    }

    public static SQLXML decodeSQLXML(String s) {
        return null;
    }

    public static String encodeSQLXML(SQLXML sqlxml) {
        try {
            return sqlxml.getString();
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
