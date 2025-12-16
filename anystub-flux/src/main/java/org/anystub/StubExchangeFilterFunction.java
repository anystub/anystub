package org.anystub;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.reactive.MockClientHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.anystub.Util.HEADER_MASK;
import static org.anystub.Util.code2Text;
import static org.anystub.Util.extractBase;
import static org.anystub.Util.extractHttpOptions;
import static org.anystub.Util.extractOptions;
import static org.anystub.Util.headerToString;

public class StubExchangeFilterFunction implements ExchangeFilterFunction {

    private final RequestCache<ClientResponse> cache = new RequestCache<>();

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        HttpMethod method = request.method();
        URI uri = request.url();

        MockClientHttpRequest mockClientHttpRequest = new MockClientHttpRequest(method, uri);
        Mono<MockClientHttpRequest> requestMono = request
                .writeTo(mockClientHttpRequest, ExchangeStrategies.withDefaults())
                .then(Mono.defer(() -> Mono.just(mockClientHttpRequest)))
                .cache();

        return requestMono
                .flatMap(mockClientHttpRequest1 ->
                        Mono.deferContextual(ctx -> {
                            AnySettingsHttp settingsHttp = extractHttpOptions(ctx);
                            AnyStubId settings = extractOptions(ctx);
                            return Util.getRequestKey(method, uri, mockClientHttpRequest1, settingsHttp, settings);
                        }))
                .flatMap((Function<List<String>, Mono<ClientResponse>>) key ->
                        Mono.deferContextual(ctx -> {
                            Base base = extractBase(ctx);

                            Mono<ClientResponse> candidate = base.request2(
                                    () -> next.exchange(request),
                                    values -> Mono.just(decode(values)),
                                    new Inverter<Mono<ClientResponse>>() {
                                        @Override
                                        public Mono<ClientResponse> invert(Mono<ClientResponse> clientResponseMono, BiFunction<Iterable<String>, Throwable, Mono<ClientResponse>> decoderFunction) {
                                            return clientResponseMono
                                                    .flatMap((ClientResponse clientResponse) -> encode(clientResponse))
                                                    .flatMap((Iterable<String> strings) -> decoderFunction.apply(strings, null))
                                                    .doOnError(throwable -> decoderFunction.apply(null, throwable));

                                        }
                                    },
                                    new KeysSupplier() {
                                        @Override
                                        public String[] get() {
                                            return key.toArray(new String[0]);
                                        }
                                    }

                            );
                            return cache.track(base, key, candidate);
                        }));

    }

    private static ClientResponse decode(Iterable<String> iterable) {
        Iterator<String> iterator = iterable.iterator();
        String[] protocol = iterator.next().split("[/.]");
        String code = iterator.next();
        String reason = iterator.next();


        ClientResponse.Builder builder = ClientResponse.create(HttpStatus.valueOf(Integer.parseInt(code)));

        MediaType contentType = null;
        String postHeader = null;
        while (iterator.hasNext()) {
            String header;
            header = iterator.next();
            if (!header.matches(HEADER_MASK)) {
                postHeader = header;
                break;
            }

            int i = header.indexOf(": ");
            builder = builder.header(header.substring(0, i), header.substring(i + 2));
            if (contentType == null
                    && StringUtils.hasLength(header.substring(0, i))
                    && header.substring(0, i).equals("Content-Type")) {
                contentType = MediaType.parseMediaType(header.substring(i + 2));
            }
        }

        if (postHeader != null) {
            byte[] bytes = StringUtil.recoverBinaryData(postHeader);
            Charset charset = null;
            if (contentType != null) {
                charset = contentType.getCharset();
            }
            if (charset == null) {
                charset = StandardCharsets.UTF_8;
            }
            builder.body(new String(bytes, charset));
        }
        return builder.build();
    }

    private static Mono<Iterable<String>> encode(ClientResponse response) {
        List<String> res = new ArrayList<>();
        if (response == null) {
            return Mono.just(res);
        }

        res.add("HTTP/1.1");
        res.add(Integer.toString(response.statusCode().value()));
        res.add(code2Text(response.statusCode()));

        List<String> headers = response.headers()
                .asHttpHeaders()
                .keySet()
                .stream()
                .sorted(String::compareTo)
                .map(h -> headerToString(response
                        .headers()
                        .asHttpHeaders(), h))
                .collect(Collectors.toList());

        res.addAll(headers);

        Flux<DataBuffer> body = response.bodyToFlux(DataBuffer.class);

        return Util.extractStringMono(body)
                .map(bodyString -> {
                    res.add(bodyString);
                    return res;
                });
    }

    @Override
    public ExchangeFilterFunction andThen(ExchangeFilterFunction afterFilter) {
        return ExchangeFilterFunction.super.andThen(afterFilter);
    }

    @Override
    public ExchangeFunction apply(ExchangeFunction exchange) {
        return ExchangeFilterFunction.super.apply(exchange);
    }


}
