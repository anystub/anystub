package org.anystub;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

class StringUtilTest {

    @Test
    void testToCharacterString() {
        String s = StringUtil.toCharacterString("{Привет:}\"".getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals("{Привет:}\"", s);
    }

    @Test
    void testRecoverBinaryData() {
        byte[] tests;
        tests = StringUtil.recoverBinaryData("test");
        Assertions.assertArrayEquals("test".getBytes(), tests);

        tests = StringUtil.recoverBinaryData("TEXT test");
        Assertions.assertArrayEquals("test".getBytes(), tests);

        tests = StringUtil.recoverBinaryData("BASE64 Ym9keSBoZXgC");
        Assertions.assertArrayEquals(("body hex"+(char)2).getBytes(), tests);


    }

    @Test
    void testStreams() throws IOException {
        try (InputStream test = StringUtil.recoverInputStream("test")) {
            String s = StringUtil.toCharacterString(test);
            Assertions.assertEquals("test", s);

        }

        StringReader stringReader = new StringReader("test");
        String s = StringUtil.toCharacterString(stringReader);
        Assertions.assertEquals("test", s);

    }

    @Test
    void testToArray() {
        String[] strings = StringUtil.toArray(null, "", "3");
        Assertions.assertArrayEquals(new String[]{null,"", "3"}, strings);
    }


    @Test
    void testYamlDelimiter() throws IOException {
        File file;
        String s;

        file = new File("src/test/resources/delimiter-empty.yml");
        s = StringUtil.nextYamlDelimiter(file);
        Assertions.assertEquals("", s);
        file = new File("src/test/resources/delimiter-valid.yml");
        s = StringUtil.nextYamlDelimiter(file);
        Assertions.assertEquals("", s);
        file = new File("src/test/resources/delimiter-good.yml");
        s = StringUtil.nextYamlDelimiter(file);
        Assertions.assertEquals("\n", s);

        file = new File("src/test/resources/delimiter-cropped.yml");
        s = StringUtil.nextYamlDelimiter(file);
        Assertions.assertEquals("\n---\n", s);

        file = new File("src/test/resources/delimiter-broken.yml");
        s = StringUtil.nextYamlDelimiter(file);
        Assertions.assertEquals("\n---\n", s);

        file = new File("src/test/resources/delimiter-full.yml");
        s = StringUtil.nextYamlDelimiter(file);
        Assertions.assertEquals("---\n", s);

        Assertions.assertFalse(StringUtil.tailMatch("1".getBytes(), "---\n".getBytes()));

    }

}