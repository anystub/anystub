package org.anystub.http;

import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpUtilTest {
    
    @Test
    public void testEncodeBodyLikeHttpHeader() {

        List<String> strings = asList("HTTP/1.1", "200", "OK", "Content-Type: application/text;charset=UTF-8",
                "Date: Tue, 04 Jun 2019 19:35:46 GMT", "X-Vcap-Request-Id: d42ad690-5e17-4321-7fd4-e3e5d65156e9",
                "Content-Length: 177", "Connection: keep-alive");

        HttpResponse decode = HttpUtil.decode(strings);

        BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("body: http-header like content".getBytes(StandardCharsets.UTF_8));
        basicHttpEntity.setContent(inputStream);
        decode.setEntity(basicHttpEntity);
        List<String> encode = HttpUtil.encode(decode);

        assertEquals("TEXT body: http-header like content", encode.get(encode.size() - 1));


    }

}