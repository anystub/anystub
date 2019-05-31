package org.anystub;

import org.anystub.http.StubHttpClient;
import org.anystub.mgmt.BaseManagerImpl;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.anystub.http.HttpUtil.HTTP_PROPERTY;
import static org.anystub.http.HttpUtil.HTTP_PROPERTY_BODY;
import static org.anystub.http.StubHttpClient.addBodyRule;
import static org.anystub.mgmt.BaseManagerImpl.getStub;
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

        Base base = BaseManagerImpl.instance().getBase("httpStub-static.yml")
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
        addBodyRule("randomX");

        HttpClient real = HttpClients.createDefault();
        StubHttpClient result = new StubHttpClient(real);

        HttpPost httpUriRequest = new HttpPost("https://gturnquist-quoters.cfapps.io:443/api/randomX");
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity("{\"a\":1}".getBytes());
        httpUriRequest.setEntity(byteArrayEntity);
        int response = result.execute(httpUriRequest,
                httpResponse -> httpResponse.getStatusLine().getStatusCode());


        assertEquals(405, response);
        assertEquals(1, getStub().times());
        assertEquals(1, getStub().times(null,null,null,"{\"a\":1}"));

    }



}