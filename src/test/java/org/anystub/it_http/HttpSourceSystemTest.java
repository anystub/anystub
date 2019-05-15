package org.anystub.it_http;

import org.anystub.AnyStubId;
import org.anystub.Base;
import org.anystub.RequestMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.anystub.http.StubHttpClient.addHeaders;
import static org.anystub.mgmt.BaseManagerImpl.getStub;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AnyStubId(filename = "httpStub.yml")
public class HttpSourceSystemTest {

    @Autowired
    private HttpSourceSystem httpSourceSystem;

    @Autowired
    private Base httpBase;

    @Autowired(required = false)
    private RestTemplate restTemplate;

    @Test
    public void getStringsTest() {

        String r = httpSourceSystem.getStrings();

        assertEquals("{\"type\":\"success\",\"value\":{\"id\":2,\"quote\":\"With Boot you deploy everywhere you can find a JVM basically.\"}}", r);

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    public void getWithHeaders() {

        addHeaders("random");
        ResponseEntity<String> forEntity = restTemplate.getForEntity("https://gturnquist-quoters.cfapps.io/api/random", String.class);
        assertEquals(200, forEntity.getStatusCodeValue());

        assertEquals(1, getStub().times());
        assertTrue(getStub().match().findFirst().get().getKey(2).startsWith("Accept:"));


        forEntity = restTemplate.getForEntity("https://gturnquist-quoters.cfapps.io/api", String.class);
        assertEquals(200, forEntity.getStatusCodeValue());
        assertEquals(1, getStub().timesEx(null, null, "https.*"));
    }

    @Test(expected = HttpClientErrorException.class)
    public void postTest() {
        restTemplate.postForEntity("https://gturnquist-quoters.cfapps.io/api/random", null, String.class);
    }

    @Test(expected = HttpClientErrorException.class)
    public void postBodyTest() {
        restTemplate.postForEntity("https://gturnquist-quoters.cfapps.io/api/random/xxx", "{test}", String.class);
    }

    @Test
    public void getBase64Test() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity("https://test", String.class);
        assertEquals("test", forEntity.getBody());
    }

}
