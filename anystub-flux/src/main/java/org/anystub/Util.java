package org.anystub;

import org.anystub.mgmt.BaseManagerFactory;
import org.anystub.mgmt.MTCache;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.http.client.reactive.MockClientHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifierOptions;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static org.anystub.StringUtil.addTextPrefix;
import static org.anystub.StringUtil.escapeCharacterString;
import static org.anystub.StringUtil.isText;
import static org.anystub.StringUtil.toCharacterString;

public class Util {
    public static final String HEADER_MASK = "^[A-Za-z0-9\\-]+: .+";

    private Util() {
    }

    /**
     * @param headers
     * @return
     */
    public static List<String> filterHeaders(HttpHeaders headers) {

        AnySettingsHttp settings = AnySettingsHttpExtractor.httpSettings();
        return filterHeaders(headers, settings);
    }

    public static List<String> filterHeaders(HttpHeaders headers, AnySettingsHttp settings) {

        if (settings.allHeaders()) {
            return headers.keySet()
                    .stream()
                    .map(h -> headerToString(headers, h))
                    .sorted()
                    .collect(Collectors.toList());
        }

        return stream(settings.headers())
                .filter(headers::containsKey)
                .map(h -> headerToString(headers, h))
                .sorted()
                .collect(Collectors.toList());
    }

    public static String headerToString(HttpHeaders headers, String h) {
        return String.format("%s: %s", h, String.join(", ", headers.getOrEmpty(h)));
    }


    public static Mono<String> extractStringMono(Flux<DataBuffer> body) {
        return body.map(dataBuffer ->
                        StringUtil.readStream(dataBuffer.asInputStream(true)))
                .collectList()
                .map(block -> {
                    if (block == null || block.isEmpty()) {
                        return new byte[0];
                    } else {
                        return
                                block.stream()
                                        .reduce(new byte[0], (result, bytes2) -> {
                                            int v = result.length;
                                            result = Arrays.copyOf(result, v + bytes2.length);
                                            System.arraycopy(bytes2, 0, result, v, bytes2.length);
                                            return result;
                                        });
                    }
                })
                .map(bodyContext -> {
                    String bodyString = toCharacterString(bodyContext);
                    if (bodyString.matches(HEADER_MASK)) {
                        bodyString = addTextPrefix(bodyString);
                    }
                    return bodyString;
                });
    }

    /**
     * builds key for the request according to http setting
     *
     * @param method
     * @param uri
     * @param request
     * @return
     */
    public static Mono<List<String>> getRequestKey(HttpMethod method, URI uri,
                                                   MockClientHttpRequest request,
                                                   AnySettingsHttp settingsHttp,
                                                   AnyStubId settings) {
        ArrayList<String> key = new ArrayList<>();
        key.add(method.name());
        key.add("HTTP/1.1");
        key.addAll(filterHeaders(request.getHeaders(), settingsHttp));
        key.add(uri.toString());

        return Mono.just(key.toArray(new String[0]))
                .flatMap(keys -> {
                    if (!SettingsUtil.matchBodyRule(method.name(), uri.toString(), settingsHttp)) {
                        return Mono.just(List.of(keys));
                    }
                    return request.getBodyAsString()
                            .switchIfEmpty(Mono.just(""))
                            .map((String body) -> {
                                String maskedBody = SettingsUtil.maskBody(body, settings);
                                String safeBody = isText(maskedBody) ?
                                        escapeCharacterString(maskedBody) :
                                        toCharacterString(maskedBody.getBytes(StandardCharsets.UTF_8));

                                return Stream.concat(Stream.of(keys), Stream.of(safeBody))
                                        .collect(Collectors.toList());
                            });

                });
    }

    public static ContextView anystubContext() {
        Context context = Context.empty();
        AnyStubId anyStubId = AnyStubFileLocator.discoverFile();
        if (anyStubId!=null) {
            context = context.put(AnyStubId.class, anyStubId);
        }
        AnySettingsHttp anySettingsHttp = AnySettingsHttpExtractor.httpSettings();
        if (anySettingsHttp!=null) {
            context = context.put(AnySettingsHttp.class, anySettingsHttp);
        }
        return context;
    }

    public static StepVerifierOptions anystubOptions() {
        return StepVerifierOptions
                .create()
                .withInitialContext(Context.empty().putAll(anystubContext()));
    }
    public static StepVerifierOptions anystubOptionsMT() {
        MTCache.setMtFallback();
        return anystubOptions();
    }

    /**
     * extract stub from context
     * @param ctx
     * @return
     */
    public static Base extractBase(ContextView ctx) {
        AnyStubId anyStubId = ctx.getOrDefault(AnyStubId.class, null);
        if (anyStubId != null) {
            return BaseManagerFactory.baseFromSettings(anyStubId);
        }

        return BaseManagerFactory.locate();
    }

    public static AnySettingsHttp extractHttpOptions(ContextView ctx) {
        return ctx.getOrDefault(AnySettingsHttp.class, AnySettingsHttpExtractor.httpSettings());
    }

    public static AnyStubId extractOptions(ContextView ctx) {
        return ctx.getOrDefault(AnyStubId.class, AnyStubFileLocator.discoverFile());
    }

    public static String code2Text(HttpStatusCode code) {
        HttpStatus resolve = HttpStatus.resolve(code.value());
        if (resolve == null) {
            return "";
        }
        return resolve.getReasonPhrase();
    }


}
