package org.anystub;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DecoderJson<R extends Object> implements DecoderSimple<R>{
    private final Class<R> responseClass;

    public DecoderJson(Class<R> responseClass) {
        this.responseClass = responseClass;
    }

    @Override
    public R decode(String values) {
        R r = null;
        if (responseClass.equals(String.class)) {
            try {
                Constructor<R> constructor = responseClass.getConstructor(String.class);
                r = constructor.newInstance(values);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new TypeNotPresentException(responseClass.getName(), e);
            }
            return r;
        }

        try {
            r = ObjectMapperFactory.get().readValue(values, responseClass);
        } catch (JsonProcessingException e) {
            throw new TypeNotPresentException(responseClass.getName(), e);
        }
        return r;
    }
}
