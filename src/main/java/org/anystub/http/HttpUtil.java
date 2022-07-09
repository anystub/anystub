package org.anystub.http;

import org.anystub.AnySettingsHttp;
import org.anystub.AnySettingsHttpExtractor;
import org.anystub.HttpGlobalSettings;
import org.anystub.SettingsUtil;
import org.anystub.StringUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.anystub.HttpGlobalSettings.globalHeaders;
import static org.anystub.StringUtil.escapeCharacterString;

/**
 * contains common functions to process HttpResponse, HttpRequest -
 * convert to string, mask body
 */
public class HttpUtil {

    private static final Logger LOGGER = Logger.getLogger(HttpUtil.class.getName());

    private static final String HEADER_MASK = "^[A-Za-z0-9\\-_]+: .+";

    private HttpUtil() {
    }

    /**
     * builds HttpResponse from strings
     * @param iterable strings representation of HttpResponse
     * @return recovered HttpResponse
     */
    public static HttpResponse decode(Iterable<String> iterable) {
        BasicHttpResponse basicHttpResponse;

        Iterator<String> iterator = iterable.iterator();
        String[] protocol = iterator.next().split("[/.]");
        String code = iterator.next();
        String reason = iterator.next();

        basicHttpResponse = new BasicHttpResponse(new ProtocolVersion(protocol[0], parseInt(protocol[1]), parseInt(protocol[2])),
                parseInt(code),
                reason);

        String postHeader = null;
        while (iterator.hasNext()) {
            String header;
            header = iterator.next();
            if (!header.matches(HEADER_MASK)) {
                postHeader = header;
                break;
            }

            int i = header.indexOf(": ");
            basicHttpResponse.setHeader(header.substring(0, i), header.substring(i + 2));
        }

        if (postHeader != null) {
            BasicHttpEntity httpEntity = new BasicHttpEntity();

            byte[] bytes = StringUtil.recoverBinaryData(postHeader);
            httpEntity.setContentLength(bytes.length);
            httpEntity.setContent(new ByteArrayInputStream(bytes));
            basicHttpResponse.setEntity(httpEntity);
        }

        return basicHttpResponse;
    }

    /**
     * converts HttpResponse to strings
     * @param httpResponse response to convert
     * @return string's representation to save in stub
     */
    public static List<String> encode(HttpResponse httpResponse) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(httpResponse.getStatusLine().getProtocolVersion().toString());
        strings.add(String.valueOf(httpResponse.getStatusLine().getStatusCode()));
        strings.add(httpResponse.getStatusLine().getReasonPhrase());

        for (Header h : httpResponse.getAllHeaders()) {
            strings.add(headerToString(h));
        }

        if (httpResponse.getEntity() != null) {
            try {
                BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpResponse.getEntity());
                httpResponse.setEntity(bufferedHttpEntity);
                extractEntity(bufferedHttpEntity)
                        .ifPresent(strings::add);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "buffering the entity failed", e);
            }
        }

        return strings;
    }


    /**
     * extracts request body, it guaranties that the request could be used in following procedures.
     * if request body is a stream the data could be unavailable after reading for saving. so the function
     * create a duplicate
     * @param httpRequest request
     * @return binary representation
     */
    public static byte[] extractEntity(HttpRequest httpRequest) {

        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) httpRequest;
            try {
                BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(request.getEntity());
                request.setEntity(bufferedHttpEntity);
                return extractEntityData(bufferedHttpEntity);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "buffering the entity failed", e);
            }
        }
        return null;
    }

    /**
     * converts data into string representation - could add text prefix to distinguish from headers
     * @param entity entity to encode
     * @return converted string
     */
    public static Optional<String> extractEntity(HttpEntity entity) {
        byte[] bytes = extractEntityData(entity);
        if (bytes == null) {
            return Optional.empty();
        }
        String entityText = StringUtil.toCharacterString(bytes);
        if (entityText.matches(HEADER_MASK)) {
            entityText = StringUtil.addTextPrefix(entityText);
        }

        return Optional.of(entityText);
    }

    /**
     * extract data from stream-based entity
     * @param entity
     * @return binary representation
     */
    public static byte[] extractEntityData(HttpEntity entity) {
        if (entity == null) {
            return null;
        }

        byte[] bytes = null;

        try {

            if (entity instanceof BasicHttpEntity) {
                BasicHttpEntity basicHttpEntity = (BasicHttpEntity) entity;

                try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
                    basicHttpEntity.writeTo(byteArray);
                    bytes = byteArray.toByteArray();
                }
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

                basicHttpEntity.setContent(inputStream);

            } else if (!entity.isStreaming()) {
                // put to cover:1. entity instanceof StringEntity
                // comes from https://github.com/OpenFeign/feign
                // when Content-Type: application/json; charset=UTF-8
                // 2. entity instanceof ByteArrayEntity
                // comes from org.springframework.web.client.RestTemplate
                // 3. HttpEntityWrapper
                try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
                    entity.writeTo(byteArray);
                    bytes = byteArray.toByteArray();
                }
            } else {
                LOGGER.warning(() -> String.format("content: unavailable %s %s", entity.getClass().getName(), entity));
            }


        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Stringify entity failed", e);
        }
        return bytes;
    }

    /**
     * encodes a request into stub-key
     * @param httpRequest request to build key
     * @param httpHost host to build key
     * @return string representation
     */
    public static List<String> encode(HttpRequest httpRequest, HttpHost httpHost) {
        ArrayList<String> strings = new ArrayList<>();

        strings.add(httpRequest.getRequestLine().getMethod());
        strings.add(httpRequest.getRequestLine().getProtocolVersion().toString());

        String fullUrl =
                ((Function<String, String>) (String o) -> {
                    if (httpHost != null && !o.contains(httpHost.toString())) {
                        return httpHost.toString() + o;
                    }
                    return o;
                }).apply(httpRequest.getRequestLine().getUri());

        strings.addAll(encodeHeaders(httpRequest));
        strings.add(fullUrl);

        if (matchBodyRule(fullUrl)) {
            byte[] bytes = extractEntity(httpRequest);
            if (bytes != null) {
                if (StringUtil.isText(bytes)) {
                    String bodyText = SettingsUtil.maskBody(new String(bytes, StandardCharsets.UTF_8));
                    strings.add(escapeCharacterString(bodyText));
                } else {
                    // omit changes for binary data
                    // TODO: implement search substring for binary data
                    strings.add(StringUtil.toCharacterString(bytes));
                }
            }
        }

        return strings;
    }

    /**
     * encodes a request into stub-key
     * @param httpRequest request to build key
     * @return string representation
     */
    public static List<String> encode(HttpRequest httpRequest) {
        return encode(httpRequest, null);
    }

    /**
     * saves headers
     * uses settings to collect required headers
     * @param httpRequest request to extract headers
     * @return string representation
     */
    public static List<String> encodeHeaders(HttpRequest httpRequest) {

        boolean currentAllHeaders = HttpGlobalSettings.globalAllHeaders;

        AnySettingsHttp settings = AnySettingsHttpExtractor.discoverSettings();

        if (settings != null) {
            currentAllHeaders = settings.allHeaders();
        }

        Header[] currentHeaders = httpRequest.getAllHeaders();

        // not comparator in lambda because compiler optimizes to use super class,
        // the optimization does not work with old apache client
        Arrays.sort(currentHeaders, HttpUtil::compareHeaders);


        if (currentAllHeaders) {
            return stream(currentHeaders)
                    .map(HttpUtil::headerToString)
                    .collect(Collectors.toList());
        }


        Set<String> headersToAdd = new HashSet<>();
        if (settings != null) {
            headersToAdd.addAll(asList(settings.headers()));
        }
        if ((settings == null || !settings.overrideGlobal()) && globalHeaders != null) {
            headersToAdd.addAll(asList(globalHeaders));
        }

        return stream(currentHeaders)
                .filter(header -> headersToAdd.contains(header.getName()))
                .map(HttpUtil::headerToString)
                .collect(Collectors.toList());

    }


    /**
     * checks if for given URL settings require to save request body
     * @param url request to check against settings
     * @return true if request bosy should be saved
     */
    private static boolean matchBodyRule(String url) {
        return SettingsUtil.matchBodyRule(url);
    }


    /**
     * converts heaers into string representation
     * @param h header to convert
     * @return string representation
     */
    public static String headerToString(Header h) {
        return String.format("%s: %s", h.getName(), h.getValue());
    }


    private static int compareHeaders(Header h1, Header h2) {
        return h1.getName().compareTo(h2.getName());
    }

}
