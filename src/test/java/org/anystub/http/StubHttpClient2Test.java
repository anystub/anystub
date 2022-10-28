package org.anystub.http;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.anystub.AnySettingsHttp;
import org.anystub.AnyStubId;
import org.anystub.Document;
import org.anystub.RequestMode;
import org.anystub.StringUtil;
import org.anystub.mgmt.BaseManagerFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

@WireMockTest(httpPort = 8080)
class StubHttpClient2Test {

    HttpClient httpClient;

    @BeforeEach
    void setup() {
        CloseableHttpClient realHttpclient = HttpClients.createDefault();
        httpClient = new StubHttpClient(realHttpclient);

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void TestGetRequest2(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {

        stubFor(WireMock.get("/").willReturn(ok()
                .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();


        HttpGet httpGet = new HttpGet("http://localhost:"+port);
        HttpResponse response1 = httpClient.execute(httpGet);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        HttpEntity entity = response1.getEntity();

        byte[] bytes = StringUtil.readStream(entity.getContent());
        String block = new String(bytes, StandardCharsets.UTF_8);


        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = BaseManagerFactory.locate()
                .times();
        Assertions.assertEquals(1, times);

    }


    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    @AnySettingsHttp(headers = "Accept")
    void testSavingHeaders(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.get("/").willReturn(ok()
                .withHeader("x-forward", "test")
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        HttpGet httpGet = new HttpGet("http://localhost:"+port);
        httpGet.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpGet.setHeader("Accept", "application/x-ndjson, plain/text, */*");

        HttpResponse response1 = httpClient.execute(httpGet);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        HttpEntity entity = response1.getEntity();

        byte[] bytes = StringUtil.readStream(entity.getContent());
        String block = new String(bytes, StandardCharsets.UTF_8);

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = BaseManagerFactory.locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = BaseManagerFactory.locate()
                .history()
                .findFirst().get();

        Assertions.assertTrue(document.matchEx_to(null, null, "Accept:.*"));

        ArrayList<String> objects = new ArrayList<>();
        document.getVals().forEach(objects::add);

        String s1;
        s1 = objects.stream().filter(s -> s.startsWith("Content-Type"))
                .findFirst().get();
        Assertions.assertEquals("Content-Type: application/json", s1);

        s1 = objects.stream().filter(s -> s.startsWith("x-forward"))
                .findFirst().get();
        Assertions.assertEquals("x-forward: test", s1);

    }


    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    @AnySettingsHttp(headers = "Accept", bodyTrigger = "")
    void testSavingRequestBody(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/")
                .willReturn(ok()
                        .withHeader("x-forward", "test")
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"test\":\"ok\"}")));


        String msg = "test msg";


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        HttpPost httpPost = new HttpPost("http://localhost:"+port);
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpPost.setHeader("Accept", "application/x-ndjson, plain/text, */*");
        httpPost.setEntity(new StringEntity(msg));

        HttpResponse response1 = httpClient.execute(httpPost);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        HttpEntity entity = response1.getEntity();

        byte[] bytes = StringUtil.readStream(entity.getContent());
        String block = new String(bytes, StandardCharsets.UTF_8);

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = BaseManagerFactory.locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = BaseManagerFactory.locate()
                .history()
                .findFirst().get();

//        Assertions.assertTrue(document.matchEx_to(null, null, "Accept:.*"));

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmNone)
    @AnySettingsHttp(headers = "Accept", bodyTrigger = "")
    void testUseSaved(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/")
                .willReturn(ok()
                        .withHeader("x-forward", "test")
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"test\":\"ok\"}")));


        String msg = "test msg";


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        HttpPost httpPost = new HttpPost("http://localhost:"+port);
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpPost.setHeader("Accept", "application/x-ndjson, plain/text, */*");
        httpPost.setEntity(new StringEntity(msg));

        HttpResponse response1 = httpClient.execute(httpPost);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        HttpEntity entity = response1.getEntity();

        byte[] bytes = StringUtil.readStream(entity.getContent());
        String block = new String(bytes, StandardCharsets.UTF_8);

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = BaseManagerFactory.locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = BaseManagerFactory.locate()
                .history()
                .findFirst().get();

    }


    @Test
    @AnyStubId(requestMode = RequestMode.rmAll, requestMasks = {"secret", "password", "....-.*\\.\\d{2,10}", "\\d{4}-\\d{1,2}-\\d{1,2}"})
    @AnySettingsHttp(bodyTrigger = "")
    void testMaskRequest(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/")
                .willReturn(ok()
                        .withHeader("x-forward", "test")
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"test\":\"ok\"}")));



        String msg = String.format("hypothetical request containing a secret data like a password, "+
                        "or a variable timestamp: %s in the middle of request. date\":[%s]",
                LocalDateTime.now().toString(), LocalDate.now().toString());


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        HttpPost httpPost = new HttpPost("http://localhost:"+port);
        httpPost.setHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE);
        httpPost.setHeader("Accept", "application/x-ndjson, plain/text, */*");

        httpPost.setEntity(new StringEntity(msg));

        HttpResponse response1 = httpClient.execute(httpPost);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        HttpEntity entity = response1.getEntity();

        byte[] bytes = StringUtil.readStream(entity.getContent());
        String block = new String(bytes, StandardCharsets.UTF_8);

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = BaseManagerFactory.locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = BaseManagerFactory.locate()
                .history()
                .findFirst().get();

        Assertions.assertTrue(document.key_to_string().contains("containing a ... data like a ..., or a variable timestamp: ... in"));
        Assertions.assertTrue(document.key_to_string().contains("date\":[...]"), document.key_to_string());
    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    @AnySettingsHttp(bodyTrigger = "")
    void testBase64RequestResponse(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/")
                .willReturn(ok()
                        .withHeader("x-forward", "test")
                        .withHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .withBody("line1\nline2"+(char)0x01+"eom")));


        String msg = "body hex"+(char)0x2;
        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        HttpPost httpPost = new HttpPost("http://localhost:"+port);
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpPost.setHeader("Accept", "application/x-ndjson, plain/text, */*");
        httpPost.setEntity(new StringEntity(msg));

        HttpResponse response1 = httpClient.execute(httpPost);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        HttpEntity entity = response1.getEntity();

        byte[] bytes = StringUtil.readStream(entity.getContent());
        String block = new String(bytes, StandardCharsets.UTF_8);

        Assertions.assertEquals("line1\nline2"+(char)0x01+"eom", block);

        long times = BaseManagerFactory.locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = BaseManagerFactory.locate()
                .history()
                .findFirst().get();

        Assertions.assertTrue(document.getKey(-1).startsWith("BASE64"));
        Assertions.assertTrue(document.getVal(-1).startsWith("BASE64"));

    }


    @RepeatedTest(3)
    @AnyStubId(requestMode = RequestMode.rmNew)
    @AnySettingsHttp(bodyTrigger = "-")
    void testRMNewMode(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        BaseManagerFactory.locate().clear();
        String filePath = BaseManagerFactory.locate().getFilePath();
        Files.deleteIfExists(new File(filePath).toPath());

        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/").willReturn(ok()
                .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        HttpPost httpPost = new HttpPost("http://localhost:"+port);
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpPost.setHeader("Accept", "application/x-ndjson, plain/text, */*");

        HttpResponse response1 = httpClient.execute(httpPost);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        HttpEntity entity = response1.getEntity();

        byte[] bytes = StringUtil.readStream(entity.getContent());
        String block = new String(bytes, StandardCharsets.UTF_8);

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        String block2 = null;
        {
            HttpResponse response2 = httpClient.execute(httpPost);
            Assertions.assertEquals(200, response2.getStatusLine().getStatusCode());

            HttpEntity entity2 = response2.getEntity();

            byte[] bytes2 = StringUtil.readStream(entity2.getContent());
            block2 = new String(bytes2, StandardCharsets.UTF_8);
        }
        Assertions.assertEquals(block, block2);

        long times = BaseManagerFactory.locate()
                .times();
        Assertions.assertEquals(2, times);

        long count = BaseManagerFactory.locate()
                .history()
                .count();

        Assertions.assertEquals(2,count);

        verify(1,postRequestedFor(urlPathEqualTo("/")));

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void testRMAllMode(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.get("/").willReturn(ok()
                .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        HttpGet httpGet = new HttpGet("http://localhost:"+port);
        HttpResponse response1 = httpClient.execute(httpGet);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        HttpEntity entity = response1.getEntity();

        byte[] bytes = StringUtil.readStream(entity.getContent());
        String block = new String(bytes, StandardCharsets.UTF_8);

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        String block2 = null;
        {  HttpGet httpGet2 = new HttpGet("http://localhost:"+port);
            HttpResponse response2 = httpClient.execute(httpGet);
            Assertions.assertEquals(200, response2.getStatusLine().getStatusCode());

            HttpEntity entity2 = response2.getEntity();

            byte[] bytes2 = StringUtil.readStream(entity2.getContent());
            block2 = new String(bytes2, StandardCharsets.UTF_8);
        }

        Assertions.assertEquals(block, block2);

        long times = BaseManagerFactory.locate()
                .times();
        Assertions.assertEquals(2, times);

        long count = BaseManagerFactory.locate()
                .history()
                .count();

        Assertions.assertEquals(2,count);

        verify(2,getRequestedFor(urlPathEqualTo("/")));
    }


    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    @AnySettingsHttp(bodyTrigger = {"-auth", "t"})
    void testIncludeExcludeBody(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/auth")
                .willReturn(ok()
                        .withHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .withBody("ok")));
        stubFor(WireMock.post("/test")
                .willReturn(ok()
                        .withHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .withBody("ok")));



        int port = wmRuntimeInfo.getHttpPort();
        HttpPost httpPost = new HttpPost("http://localhost:"+port+"/auth");
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpPost.setHeader("Accept", "plain/text, */*");
        httpPost.setEntity(new StringEntity("test"));

        HttpResponse response1 = httpClient.execute(httpPost);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        httpPost = new HttpPost("http://localhost:"+port+"/test");
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpPost.setHeader("Accept", "plain/text, */*");
        httpPost.setEntity(new StringEntity("test"));

        response1 = httpClient.execute(httpPost);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());


        long times = BaseManagerFactory.locate()
                .times();
        Assertions.assertEquals(2, times);

        Document document;
        document = BaseManagerFactory.locate()
                .history()
                .findFirst()
                .get();

        Assertions.assertTrue(document.getKey(-1).startsWith("http://localhost:8080/auth"));
        document = BaseManagerFactory.locate()
                .history()
                .skip(1)
                .findFirst()
                .get();
        Assertions.assertTrue(document.getKey(-2).startsWith("http://localhost:8080/test"));
        Assertions.assertTrue(document.getKey(-1).startsWith("test"));

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void testPostIncludesBody(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/auth")
                .willReturn(ok()
                        .withHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .withBody("ok")));

        int port = wmRuntimeInfo.getHttpPort();
        HttpPost httpPost = new HttpPost("http://localhost:"+port+"/auth");
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpPost.setHeader("Accept", "plain/text, */*");
        httpPost.setEntity(new StringEntity("test"));

        HttpResponse response1 = httpClient.execute(httpPost);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        long times = BaseManagerFactory.locate()
                .times("POST", "HTTP/1.1", "http://localhost:8080/auth", "test");
        Assertions.assertEquals(1, times);
    }

    @AnyStubId
    @RepeatedTest(2)
    void testPostSkipEmptyBody(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/auth")
                .willReturn(ok()
                        .withHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .withBody("ok")));

        int port = wmRuntimeInfo.getHttpPort();
        HttpPost httpPost = new HttpPost("http://localhost:"+port+"/auth");
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpPost.setHeader("Accept", "plain/text, */*");

        HttpResponse response1 = httpClient.execute(httpPost);
        Assertions.assertEquals(200, response1.getStatusLine().getStatusCode());

        Document post = BaseManagerFactory.locate()
                .match("POST", "HTTP/1.1", "http://localhost:8080/auth")
                .reduce((a, b) -> b)
                .get();
        Assertions.assertEquals("http://localhost:8080/auth", post.getKey(-1));

    }


}