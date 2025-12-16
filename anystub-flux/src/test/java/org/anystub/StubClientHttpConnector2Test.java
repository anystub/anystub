package org.anystub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import org.anystub.mgmt.MTCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.anystub.Util.anystubOptions;
import static org.anystub.mgmt.BaseManagerFactory.locate;

@WireMockTest(httpPort = 8080)
@AnyStubId
class StubClientHttpConnector2Test {
    private WebClient webClient;

    @BeforeEach
    void setup() {
        webClient = WebClient.builder()
                .exchangeFunction(new StubExchangeFunction())
                .build();
    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void TestGetRequest2(WireMockRuntimeInfo wmRuntimeInfo) throws JsonProcessingException {

        // The static DSL will be automatically configured for you
        stubFor(WireMock.get("/").willReturn(ok()
                        .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.get()
                .uri("http://localhost:"+port)
                .retrieve()
                .toEntityFlux(String.class)
                .block().getBody().collectList().block()
                        .stream().collect(Collectors.joining());

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = locate()
                .times();
        Assertions.assertEquals(1, times);

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    @AnySettingsHttp(headers = {"Accept", "missing-header"})
    void testSavingHeaders(WireMockRuntimeInfo wmRuntimeInfo) {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.get("/").willReturn(ok()
                .withHeader("x-forward", "test")
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.get()
                        .uri("http://localhost:"+port)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .header("Accept", "application/x-ndjson", "plain/text", MediaType.ALL_VALUE)
                        .retrieve()
                        .toEntityFlux(String.class)
                        .block().getBody().collectList().block()
                        .stream().collect(Collectors.joining());

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = locate()
                .history()
                .findFirst().get();

        Assertions.assertTrue(document.matchEx_to(null, null, "Accept:.*", "http.*"));

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


    static class Request1 {
        int code;
        String msg;
        LocalDate date;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
    }
    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    @AnySettingsHttp(headers = "Accept", bodyTrigger = "")
    void testSavingRequestBody(WireMockRuntimeInfo wmRuntimeInfo) {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/")
                .willReturn(ok()
                .withHeader("x-forward", "test")
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"test\":\"ok\"}")));


        Request1 request1 = new Request1();
        request1.code =23;
        request1.msg = "test msg";
        request1.date = LocalDate.of(2022,6,30);


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.post()
                        .uri("http://localhost:"+port)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .header("Accept", "application/x-ndjson", "plain/text", MediaType.ALL_VALUE)
                        .bodyValue(request1)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = locate()
                .history()
                .findFirst().get();

        Assertions.assertTrue(document.matchEx_to(null, null,
                "Accept:.*", null,
                ".*test msg.*"));

    }

    @Test
    @AnyStubId(filename = "testUseSaved", requestMode = RequestMode.rmNone)
    @AnySettingsHttp(headers = "Accept", bodyTrigger = "")
    void testUseSaved(WireMockRuntimeInfo wmRuntimeInfo) {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/")
                .willReturn(ok()
                        .withHeader("x-forward", "test")
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"test\":\"ok\"}")));


        Request1 request1 = new Request1();
        request1.code =23;
        request1.msg = "test msg";
        request1.date = LocalDate.of(2022,6,30);


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.post()
                        .uri("http://localhost:"+port)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .header("Accept", "application/x-ndjson", "plain/text", MediaType.ALL_VALUE)
                        .bodyValue(request1)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = locate()
                .history()
                .findFirst().get();

        Assertions.assertEquals("http://localhost:"+port, document.getKey(-2));
        Assertions.assertTrue(document.getKey(-1).startsWith("{"));

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    @AnySettingsHttp(bodyTrigger = "")
    void testNoBodyInGet(WireMockRuntimeInfo wmRuntimeInfo) {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.get("/")
                .willReturn(ok()
                        .withHeader("x-forward", "test")
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.get()
                        .uri("http://localhost:"+port)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .header("Accept", "application/x-ndjson", "plain/text", MediaType.ALL_VALUE)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = locate()
                .history()
                .findFirst().get();

        Assertions.assertEquals("http://localhost:"+port, document.getKey(-1));

    }


    @Test
    @AnyStubId(requestMode = RequestMode.rmAll, requestMasks = {"secret", "password", ": ....-.* ", "\\d{4},\\d{1,2},\\d{1,2}"})
    @AnySettingsHttp(bodyTrigger = "")
    void testMaskRequest(WireMockRuntimeInfo wmRuntimeInfo) {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/")
                .willReturn(ok()
                        .withHeader("x-forward", "test")
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"test\":\"ok\"}")));


        Request1 request1 = new Request1();
        request1.code =23;
        request1.msg = String.format("hypothetical request containing a secret data like a password, "+
                        "or a variable timestamp: %s in the middle of request",
                LocalDateTime.now().toString());
        request1.date = LocalDate.now();


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.post()
                        .uri("http://localhost:"+port)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .header("Accept", "application/x-ndjson", "plain/text", MediaType.ALL_VALUE)
                        .bodyValue(request1)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        long times = locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = locate()
                .history()
                .findFirst().get();

        Assertions.assertTrue(document.key_to_string().contains("containing a ... data like a ..., or a variable timestamp...request"));
        Assertions.assertTrue(document.key_to_string().contains("date\":[...]"), document.key_to_string());
    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    @AnySettingsHttp(bodyTrigger = "")
    void testBase64RequestResponse(WireMockRuntimeInfo wmRuntimeInfo){
        // The static DSL will be automatically configured for you
        stubFor(WireMock.post("/")
                .willReturn(ok()
                        .withHeader("x-forward", "test")
                        .withHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .withBody("line1\nline2"+(char)0x01+"eom")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.post()
                        .uri("http://localhost:"+port)
                        .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .header("Accept", "application/x-ndjson", "plain/text", MediaType.ALL_VALUE)
                        .bodyValue("body hex"+(char)0x2)
                        .retrieve()
                        .toEntity(String.class)
                        .block().getBody();


        Assertions.assertEquals("line1\nline2"+(char)0x01+"eom", block);

        long times = locate()
                .times();
        Assertions.assertEquals(1, times);

        Document document = locate()
                .history()
                .findFirst().get();

        Assertions.assertTrue(document.getKey(-1).startsWith("BASE64"));
        Assertions.assertTrue(document.getVal(-1).startsWith("BASE64"));

    }


    @RepeatedTest(3)
    void testRMNewMode(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        locate().clear();
        String filePath = locate().getFilePath();
        Files.deleteIfExists(new File(filePath).toPath());

        // The static DSL will be automatically configured for you
        stubFor(WireMock.get("/").willReturn(ok()
                .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.get()
                        .uri("http://localhost:"+port)
                        .retrieve()
                        .toEntityFlux(String.class)
                        .block().getBody().collectList().block()
                        .stream().collect(Collectors.joining());

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        String block2 =
                webClient.get()
                        .uri("http://localhost:"+port)
                        .retrieve()
                        .toEntityFlux(String.class)
                        .block().getBody().collectList().block()
                        .stream().collect(Collectors.joining());
        Assertions.assertEquals(block, block2);

        long times = locate()
                .times();
        Assertions.assertEquals(2, times);

        long count = locate()
                .history()
                .count();

        Assertions.assertEquals(2,count);

        verify(1,getRequestedFor(urlPathEqualTo("/")));

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void testRMAllMode(WireMockRuntimeInfo wmRuntimeInfo) {
        stubFor(WireMock.get("/").willReturn(ok()
                .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.get()
                        .uri("http://localhost:"+port)
                        .retrieve()
                        .toEntityFlux(String.class)
                        .block().getBody().collectList().block()
                        .stream().collect(Collectors.joining());

        Assertions.assertEquals("{\"test\":\"ok\"}", block);

        String block2 =
                webClient.get()
                        .uri("http://localhost:"+port)
                        .retrieve()
                        .toEntityFlux(String.class)
                        .block().getBody().collectList().block()
                        .stream().collect(Collectors.joining());
        Assertions.assertEquals(block, block2);

        long times = locate()
                .times();
        Assertions.assertEquals(2, times);

        long count = locate()
                .history()
                .count();

        Assertions.assertEquals(2,count);

        verify(2,getRequestedFor(urlPathEqualTo("/")));
    }

   @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void testHeaderLikeResponseBody(WireMockRuntimeInfo wmRuntimeInfo) {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.get("/").willReturn(ok()
                .withHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .withBody("Content-Type: ok")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        String block =
                webClient.get()
                        .uri("http://localhost:"+port)
                        .retrieve()
                        .toEntityFlux(String.class)
                        .block().getBody().collectList().block()
                        .stream().collect(Collectors.joining());

        Assertions.assertEquals("Content-Type: ok", block);


        Document document = locate()
                .history()
                .findFirst()
                .get();

        Assertions.assertEquals("TEXT Content-Type: ok", document.getVal(-1));

    }


    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void passStubInContext(WireMockRuntimeInfo wmRuntimeInfo) {
        // The static DSL will be automatically configured for you
        stubFor(WireMock.get("/").willReturn(ok()
                .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        Mono<String> blockMono =
                webClient.get()
                        .uri("http://localhost:" + port)
                        .retrieve()
                        .toEntityFlux(String.class)
                        .flatMap(response -> response.getBody().collectList())
                        .map(strings -> String.join("", strings));

        StepVerifier.create(blockMono.single(),
                        anystubOptions())
                .expectNextMatches(block -> {
                            Assertions.assertEquals("{\"test\":\"ok\"}", block);
                            return true;
                        }

                )
                .verifyComplete();


        long times = locate()
                .times();
        Assertions.assertEquals(1, times);

        long count = locate()
                .history()
                .count();

        Assertions.assertEquals(1,count);

        verify(1,getRequestedFor(urlPathEqualTo("/")));
    }

    @RepeatedTest(value = 10)
    @AnySettingsHttp(allHeaders = true)
    void testAsync(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        File stubFile = new File(locate().getFilePath());
        Files.deleteIfExists(stubFile.toPath());
        locate().clear();

        try (AutoCloseable x = MTCache.setMtFallback()) {
            stubFor(WireMock.get("/").willReturn(ok()
                    .withBody("{\"test\":\"ok\"}")));


            // Info such as port numbers is also available
            int port = wmRuntimeInfo.getHttpPort();
            Mono<String> blockMono1 =
                    webClient.get()
                            .uri("http://localhost:" + port)
                            .header("Accept", "application/json")
                            .retrieve()
                            .toEntityFlux(String.class)
                            .flatMap(response -> response.getBody().collectList())
                            .map(strings -> String.join("", strings));

            Mono<String> blockMono2 =
                    webClient.get()
                            .uri("http://localhost:" + port)
                            .retrieve()
                            .toEntityFlux(String.class)
                            .flatMap(response -> response.getBody().collectList())
                            .map(strings -> String.join("", strings));

            Mono<String> blockMono3 =
                    webClient.get()
                            .uri("http://localhost:" + port)
                            .retrieve()
                            .toEntityFlux(String.class)
                            .flatMap(response -> response.getBody().collectList())
                            .map(strings -> String.join("", strings));


            CompletableFuture.allOf(blockMono1.toFuture(),
                    blockMono2.toFuture(),
                    blockMono3.toFuture()).join();


            // three independent Monos
            // two have identical requests - so only one of them pass to real syste,
            verify(2, getRequestedFor(urlPathEqualTo("/")));
            verify(1, getRequestedFor(urlPathEqualTo("/"))
                    .withHeader("Accept", new EqualToPattern("application/json")));

            long get = locate()

                    .timesEx("GET", "HTTP/1.1", "Accept.*");

            Assertions.assertEquals(1, get);

            // @todo: sometimes history has 3 requests. maybe this is desired result
//            Assertions.assertEquals(2, BaseManagerFactory.locate().times());
        }
    }

    @Test
    @AnySettingsHttp(headers = "Accept")
    void testRepeatedCall(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {


        File stubFile = new File(locate().getFilePath());
        Files.deleteIfExists(stubFile.toPath());

        stubFor(WireMock.get("/").willReturn(ok()
                .withFixedDelay(300)
                .withBody("{\"test\":\"ok\"}")));


        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();
        Mono<String> blockMono = Mono
                .just(1)
                .repeat(10)
                .flatMap((v) ->
                        webClient.get()
                                .uri("http://localhost:" + port)
                                .header("Accept", "application/json")
                                .header("Content-Type", "application/json")
                                .retrieve()
                                .toEntityFlux(String.class)
                                .flatMap(response -> response.getBody().collectList())
                                .map(strings -> String.join("", strings)))
                .last();

        StepVerifier.create(blockMono.single(),
                        anystubOptions())
                .expectNextMatches(block -> {
                    Assertions.assertEquals("{\"test\":\"ok\"}", block);
                            return true;
                        }

                )
                .verifyComplete();



        long times = locate()
                .times();
        Assertions.assertEquals(1, times);

        times = locate()
                .timesEx("GET", "HTTP/1.1", "Accept.*");
        Assertions.assertEquals(1, times);

        verify(1,getRequestedFor(urlPathEqualTo("/")));
        verify(1,getRequestedFor(urlPathEqualTo("/"))
                .withHeader("Accept", new EqualToPattern("application/json")));
    }


    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void testFailedConnection(WireMockRuntimeInfo wmRuntimeInfo) {
        int port = wmRuntimeInfo.getHttpPort();
        Mono<ResponseEntity<Flux<String>>> accept = webClient.get()
                .uri("http://localhost:" + (port+1))
                .retrieve()
                .toEntityFlux(String.class);

        StepVerifier.create(accept, anystubOptions())
                .verifyError();

        long times = locate()
                .times();

        Assertions.assertEquals(1, times);
    }

}