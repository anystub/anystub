package org.anystub;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.mock.http.client.reactive.MockClientHttpRequest;
import org.springframework.mock.http.client.reactive.MockClientHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.anystub.Util.HEADER_MASK;
import static org.anystub.Util.code2Text;
import static org.anystub.Util.extractBase;
import static org.anystub.Util.extractHttpOptions;
import static org.anystub.Util.extractOptions;

public class StubClientHttpConnector implements ClientHttpConnector {


    final ClientHttpConnector real;

    private final RequestCache<ClientHttpResponse> cache = new RequestCache<>();

    public StubClientHttpConnector(ClientHttpConnector real) {
        this.real = real;
    }

    @Override
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {


        MockClientHttpRequest request = new MockClientHttpRequest(method, uri);
        Mono<MockClientHttpRequest> requestMono = requestCallback
                .apply(request)
                .then(Mono.defer(() ->
                        Mono.just(request)
                                .cache()));


        return requestMono
                .flatMap(clientHttpRequest ->
                        Mono.deferContextual(ctx -> {
                            AnySettingsHttp settingsHttp = extractHttpOptions(ctx);
                            AnyStubId settings = extractOptions(ctx);
                            return Util.getRequestKey(method, uri, clientHttpRequest, settingsHttp, settings);
                        }))
                .flatMap((Function<List<String>, Mono<ClientHttpResponse>>) key ->
                        Mono.deferContextual(ctx -> {

                            Base base = extractBase(ctx);

                            Mono<ClientHttpResponse> candidate = base.request2(() -> real.connect(method, uri, requestCallback),
                                    (Iterable<String> iterable) -> Mono.just(decode(iterable)),

                                    (clientHttpResponseMono, decoderFunction) ->
                                            clientHttpResponseMono
                                                    .flatMap((ClientHttpResponse response) ->
                                                            encode(response))
                                                    .flatMap((Iterable<String> strings) ->
                                                            decoderFunction.apply(strings, null))
                                                    .doOnError(throwable -> decoderFunction.apply(null, throwable)),
                                    new KeysSupplier() {
                                        @Override
                                        public String[] get() {
                                            return key.toArray(new String[0]);
                                        }
                                    }).cache();

                            return cache.track(base, key, candidate);

                        }));

    }


    private static ClientHttpResponse decode(Iterable<String> iterable) {
        if (iterable==null) {
            return null;
        }
        Iterator<String> iterator = iterable.iterator();
        String[] protocol = iterator.next().split("[/.]");
        String code = iterator.next();
        String reason = iterator.next();

        MockClientHttpResponse clientResponse = new MockClientHttpResponse(
                Integer.parseInt(code));

        String postHeader = null;
        while (iterator.hasNext()) {
            String header;
            header = iterator.next();
            if (!header.matches(HEADER_MASK)) {
                postHeader = header;
                break;
            }

            int i = header.indexOf(": ");
            clientResponse
                    .getHeaders()
                    .set(header.substring(0, i), header.substring(i + 2));
        }

        if (postHeader != null) {
            byte[] bytes = StringUtil.recoverBinaryData(postHeader);
            Charset charset = null;
            MediaType contentType = clientResponse.getHeaders().getContentType();
            if (contentType != null) {
                charset = contentType.getCharset();
            }
            if (charset == null) {
                charset = StandardCharsets.UTF_8;
            }
            clientResponse.setBody(new String(bytes, charset));
        }
        return clientResponse;
    }

    private static Mono<Iterable<String>> encode(ClientHttpResponse response) {
        List<String> res = new ArrayList<>();
        if (response == null) {
            return Mono.just(res);
        }

        res.add("HTTP/1.1");
        res.add(Integer.toString(response.getStatusCode().value()));
        res.add(code2Text(response.getStatusCode()));

        List<String> headers = response.getHeaders().keySet().stream().sorted(String::compareTo).map(h -> Util.headerToString(response.getHeaders(), h)).collect(Collectors.toList());

        res.addAll(headers);

        Flux<DataBuffer> body = response.getBody();

        return Util.extractStringMono(body).map(bodyString -> {
            res.add(bodyString);
            return res;
        });
    }

}
