package org.anystub;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static java.util.Arrays.asList;
import static org.anystub.Util.anystubContext;
import static org.anystub.Util.extractBase;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.ALLOW;
import static org.springframework.http.HttpHeaders.REFERER;

class UtilTest {

    @Test
    @AnyStubId
    @AnySettingsHttp(allHeaders = true, headers = "Accept")
    void testHeaderFilterAll() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, "testVal");
        headers.set(ALLOW, "testVal");
        headers.set(REFERER, "testVal");

        List<String> strings = Util.filterHeaders(headers);
        Assertions.assertEquals(asList("Accept: testVal", "Allow: testVal", "Referer: testVal"), strings);

    }

   @Test
    @AnyStubId
    @AnySettingsHttp(headers = "Accept")
    void testHeaderFilterOne() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, "testVal");
        headers.set(ALLOW, "testVal");
        headers.set(REFERER, "testVal");

        List<String> strings = Util.filterHeaders(headers);
        Assertions.assertEquals(asList("Accept: testVal"), strings);

    }

    @Test
    @AnyStubId
    void reactiveContext() {
        Base stub = extractBase(anystubContext());
        Assertions.assertNotNull(stub);
        Assertions.assertEquals("UtilTest-reactiveContext.yml",
                new File(stub.getFilePath()).getName());

    }

    @Test
    @AnyStubId(filename = "customName")
    void reactiveOptions() {
        StepVerifier.create(Mono.just(1),
                Util.anystubOptions())
                .expectAccessibleContext()
                .matches(context -> context.hasKey(AnyStubId.class))
                .matches(context -> Util.extractBase(context)
                        .getFilePath()
                        .endsWith("customName.yml"))
                .then()
                .expectNext(1)
                .verifyComplete();
    }


    @Test
    void testNoExceptions() {
        Assertions.assertDoesNotThrow(()-> {
            Util.anystubOptionsMT();
        });
    }


    @Test
    void test() {

        Flux<String> stringFlux = Flux.range(1, 10)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(f -> Mono.fromCallable(() -> {
                    return "";
                }));

        StepVerifier.create(stringFlux)
                .expectNextCount(10)
                .verifyComplete();
    }

}