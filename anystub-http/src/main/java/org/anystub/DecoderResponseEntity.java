package org.anystub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.Iterator;
import java.util.Map;

public class DecoderResponseEntity<T> implements Decoder<ResponseEntity<T>>{
    private final TypeReference<T> responseClass;
    private final ObjectMapper objectMapper = ObjectMapperFactory.get();

    public DecoderResponseEntity(TypeReference<T> responseClass) {
        this.responseClass = responseClass;
    }

    @Override
    public ResponseEntity<T> decode(Iterable<String> values) {
        Iterator<String> iterator = values.iterator();
        String code = iterator.next();
        String headers = iterator.next();
        String body = null;
        if (iterator.hasNext()) {
            body = iterator.next();
        }
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.parseInt(code));

        Map headersMap = new DecoderJson<Map>(Map.class).decode(headers);
        MultiValueMap<String,String> multiValueMap = new MultiValueMapAdapter<>(headersMap);

        if (body == null) {
            return new ResponseEntity<>(multiValueMap, httpStatus);
        }

        T t;
        try {
            t = objectMapper.readValue(body, responseClass);
        } catch (JsonProcessingException e) {
            t = null;
        }

        return new ResponseEntity<>(t,
                multiValueMap,
                httpStatus);
    }
}
