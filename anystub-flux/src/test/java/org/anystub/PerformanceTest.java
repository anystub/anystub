package org.anystub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.anystub.RandomGenerator.gString;
import static org.anystub.mgmt.BaseManagerFactory.locate;

@WireMockTest(httpPort = 8080)
@Disabled
public class PerformanceTest {

    @RepeatedTest(5)
//    @Test
//    @AnyStubId(requestMode = RequestMode.rmPassThrough) // reference time with no delays 1.5-2.4
//    @AnyStubId(requestMode = RequestMode.rmAll) // tests recording: 1.6 - 2.6
    @AnyStubId// test reading: 2 - 2.2
    void connectorTest(WireMockRuntimeInfo wmRuntimeInfo) {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(new StubExchangeFunction())
//                .filter(new StubExchangeFilterFunction())
                .build();
        locate().clear();

        String r = "{\"test\":\"ok\"}";
        stubFor(WireMock.post("/").willReturn(ok()
                .withBody(r)));

        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();

        int size = 1600;
        Random random = RandomGenerator.initRandomizer(123);

        Flux<String> stringFlux = Flux.range(0, size)
                .map(unused -> {
                    int i1 = random.nextInt(5) + 3;
                    String[] in = new String[i1];
                    for (int j = 0; j < i1; j++) {
                        in[j] = gString();
                    }
                    in[0] = unused.toString();
                    return in;
                })
                .parallel(2)
                .flatMap(in -> webClient.post()
                        .uri("http://localhost:" + port)
                        .bodyValue(in)
                        .retrieve()
                        .bodyToMono(String.class))
                .sequential();


        StepVerifier.create(stringFlux, Util.anystubOptions())
                .expectNextCount(size)
                .verifyComplete();

        long count = locate().history().count();
        Assertions.assertEquals(size, count);
        long times = locate().times();
        Assertions.assertEquals(size, times);

    }

}
