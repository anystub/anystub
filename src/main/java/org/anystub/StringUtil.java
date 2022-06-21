package org.anystub;

import org.anystub.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Base64;

import static org.anystub.Util.BASE64_PREFIX;
import static org.anystub.Util.TEXT_PREFIX;

/**
 * holds util functions to convert
 */
public class StringUtil {
    /**
     * recover binary data from string from stub file
     *
     * @param in string from stub file
     * @return
     */
    public static byte[] recoverBinaryData(String in) {
        if (in.startsWith(TEXT_PREFIX)) {
            return in.substring(TEXT_PREFIX.length()).getBytes();
        } else if (in.startsWith(BASE64_PREFIX)) {
            String base64Entity = in.substring(BASE64_PREFIX.length());
            return Base64.getDecoder().decode(base64Entity);
        } else {
            return in.getBytes();
        }
    }

    public static String toCharacterString(InputStream inputStream) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int r;
            while ((r = inputStream.read()) != -1) {
                byteArrayOutputStream.write(r);
            }
            return Util.toCharacterString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new UnsupportedOperationException("failed save InputStream");
        }
    }

    public static InputStream recoverInputStream(String in) {
        byte[] bytes = recoverBinaryData(in);
        return new ByteArrayInputStream(bytes);
    }


    public static String toCharacterString(Reader reader) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int r;
            while ((r = reader.read()) != -1) {
                byteArrayOutputStream.write(r);
            }
            return Util.toCharacterString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new UnsupportedOperationException("failed save InputStream");
        }
    }

}
