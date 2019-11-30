package org.anystub;

import org.anystub.http.AnySettingsHttp;
import org.anystub.http.StubHttpClient;
import org.anystub.mgmt.BaseManagerFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class StubHttpClientTest {

    @Test
    @AnyStubId(filename = "httpStub-static.yml")
    public void executeGetTest() throws IOException {

        HttpClient real = HttpClients.createDefault();
        StubHttpClient result = new StubHttpClient(real);

        HttpGet httpUriRequest = new HttpGet("https://gturnquist-quoters.cfapps.io:443/api/random");
        HttpResponse response = result.execute(httpUriRequest);


        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    @AnyStubId(filename = "httpStub-static")
    public void executeHostUriTest() throws IOException {

        HttpClient real = HttpClients.createDefault();
        StubHttpClient result = new StubHttpClient(real);

        HttpHost httpHost = new HttpHost("gturnquist-quoters.cfapps.io", 443, "https");
        HttpGet httpUriRequest = new HttpGet("/api/random");
        HttpResponse response = result.execute(httpHost, httpUriRequest);


        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void executeResponseHandlerTest() throws IOException {

        Base base = BaseManagerFactory.getBaseManager().getBase("httpStub-static.yml")
                .constrain(RequestMode.rmNone);
        base.clear();

        HttpClient real = HttpClients.createDefault();
        StubHttpClient result = new StubHttpClient(real).setFallbackBase(base);

        HttpGet httpUriRequest = new HttpGet("https://gturnquist-quoters.cfapps.io:443/api/random");
        int response = result.execute(httpUriRequest,
                httpResponse -> httpResponse.getStatusLine().getStatusCode());


        assertEquals(200, response);
    }

    @Test
    @AnyStubId
    public void executePostTest() throws IOException {
        StubHttpClient.addBodyRule("randomX");

        HttpClient real = HttpClients.createDefault();
        StubHttpClient result = new StubHttpClient(real);

        HttpPost httpUriRequest = new HttpPost("https://gturnquist-quoters.cfapps.io:443/api/randomX");
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity("{\"a\":1}".getBytes());
        httpUriRequest.setEntity(byteArrayEntity);
        int response = result.execute(httpUriRequest,
                httpResponse -> httpResponse.getStatusLine().getStatusCode());


        assertEquals(405, response);
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times());
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times(null, null, null, "{\"a\":1}"));

    }

    @Test
    @AnyStubId
    public void executePostTextTest() throws IOException {
        StubHttpClient.addBodyRule("randomX");

        HttpClient real = HttpClients.createDefault();
        StubHttpClient result = new StubHttpClient(real);

        HttpPost httpUriRequest = new HttpPost("https://gturnquist-quoters.cfapps.io:443/api/randomX-text");
        httpUriRequest.setHeader("Content-Type", "application/json; charset=UTF-8");
        StringEntity stringEntity = new StringEntity("some text", StandardCharsets.UTF_8);
        httpUriRequest.setEntity(stringEntity);
        int response = result.execute(httpUriRequest,
                httpResponse -> httpResponse.getStatusLine().getStatusCode());


        assertEquals(405, response);
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times());
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times(null, null, null, "some text"));

    }

    @Test
    @AnyStubId
    public void executePostStreamingTest() throws IOException {
        StubHttpClient.addBodyRule("443");

        HttpClient real = HttpClients.createDefault();
        StubHttpClient result = new StubHttpClient(real);

        HttpPost httpUriRequest = new HttpPost("https://gturnquist-quoters.cfapps.io:443/api/randomX-stream");
        httpUriRequest.setHeader("Content-Type", "plain/text; charset=UTF-8");
        BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("very-string".getBytes(StandardCharsets.UTF_8));
        basicHttpEntity.setContent(inputStream);
        httpUriRequest.setEntity(basicHttpEntity);
        int response = result.execute(httpUriRequest,
                httpResponse -> httpResponse.getStatusLine().getStatusCode());


        assertEquals(405, response);
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times());
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times(null, null, null, "very-string"));

    }


    @Test
    @AnyStubId
    @AnySettingsHttp(headers = {"HEADER", "HEADER3"})
    public void executePostHttpSettingsTest() throws IOException {

        HttpClient real = HttpClients.createDefault();
        StubHttpClient result = new StubHttpClient(real);

        HttpPost httpUriRequest = new HttpPost("https://gturnquist-quoters.cfapps.io:443/api/randomX-stream");
        httpUriRequest.setHeader("Content-Type", "plain/text; charset=UTF-8");
        httpUriRequest.setHeader("HEADER", "1");
        httpUriRequest.setHeader("HEADER2", "2");
        httpUriRequest.setHeader("HEADER3", "3");
        BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("very-string".getBytes(StandardCharsets.UTF_8));
        basicHttpEntity.setContent(inputStream);
        httpUriRequest.setEntity(basicHttpEntity);
        int response = result.execute(httpUriRequest,
                httpResponse -> httpResponse.getStatusLine().getStatusCode());


        assertEquals(405, response);
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times());
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times(null, null, "HEADER: 1", "HEADER3: 3"));

    }

    @Test
    @AnyStubId
    @AnySettingsHttp(allHeaders = true, bodyTrigger = "randomX-stream")
    public void executePostHttpSettingsBodyTest() throws IOException {

        HttpClient real = HttpClients.createDefault();
        StubHttpClient result = new StubHttpClient(real);

        HttpPost httpUriRequest = new HttpPost("https://gturnquist-quoters.cfapps.io:443/api/randomX-stream");
        httpUriRequest.setHeader("Content-Type", "plain/text; charset=UTF-8");
        httpUriRequest.setHeader("HEADER", "1");
        httpUriRequest.setHeader("HEADER2", "2");
        httpUriRequest.setHeader("HEADER3", "3");
        BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("very-string".getBytes(StandardCharsets.UTF_8));
        basicHttpEntity.setContent(inputStream);
        httpUriRequest.setEntity(basicHttpEntity);
        int response = result.execute(httpUriRequest,
                httpResponse -> httpResponse.getStatusLine().getStatusCode());


        assertEquals(405, response);
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times());
        assertEquals(1, BaseManagerFactory.getBaseManager().getStub().times("POST", "HTTP/1.1",
                "Content-Type: plain/text; charset=UTF-8", "HEADER: 1", "HEADER2: 2",
                "HEADER3: 3", "https://gturnquist-quoters.cfapps.io:443/api/randomX-stream",
                "very-string"));

    }

}