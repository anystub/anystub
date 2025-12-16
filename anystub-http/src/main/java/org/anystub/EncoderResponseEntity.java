package org.anystub;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;

public class EncoderResponseEntity<T> implements Encoder<ResponseEntity<T>> {
    @Override
    public Iterable<String> encode(ResponseEntity<T> resp) {
        HttpStatusCode statusCode = resp.getStatusCode();
        HttpHeaders headers = resp.getHeaders();
        if (!resp.hasBody()) {
            return asList(new EncoderJson<Integer>().encode(statusCode.value()),
                    new EncoderJson<HttpHeaders>().encode(headers));
        }
        T body = resp.getBody();

        return asList(new EncoderJson<Integer>().encode(statusCode.value()),
                new EncoderJson<HttpHeaders>().encode(headers),
                new EncoderJson<T>().encode(body));
    }
}
