package org.anystub.http;

import org.anystub.EncoderResponseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EncoderResponseEntityTest {

    @Test
    void testEncoderFull() {
        EncoderResponseEntity<Integer> entity = new EncoderResponseEntity<>();
        ResponseEntity<Integer> responseEntity = new ResponseEntity<>(123, HttpStatus.ACCEPTED);

        Iterator<String> encode = entity.encode(responseEntity).iterator();
        assertEquals("202", encode.next());
        assertEquals("{}", encode.next());
        assertEquals("123", encode.next());


    }

    @Test
    void testEncoderVoid() {
        EncoderResponseEntity<Void> entity = new EncoderResponseEntity<>();
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);

        Iterator<String> encode = entity.encode(responseEntity).iterator();
        assertEquals("202", encode.next());
        assertEquals("{}", encode.next());
        assertFalse(encode.hasNext());

    }

}