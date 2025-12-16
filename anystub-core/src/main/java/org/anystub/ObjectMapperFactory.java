package org.anystub;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperFactory {
    private static  ObjectMapper objectMapper = null;

    private ObjectMapperFactory() {
    }

     public static ObjectMapper get() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
        }
        return objectMapper;
    }

}
