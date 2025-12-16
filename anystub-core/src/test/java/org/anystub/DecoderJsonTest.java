package org.anystub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecoderJsonTest {
    static class Car {
        int weight;
        int length;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }
    }

    @Test
    void decode() {
        DecoderJson<Car> carDecoderJson = new DecoderJson<>(Car.class);
        Car car = carDecoderJson.decode("{\"weight\":12}");
        assertEquals(0, car.length );
        assertEquals(12, car.weight );
    }


    @Test
    void decodeFail() {
        DecoderJson<Car> carDecoderJson = new DecoderJson<>(Car.class);

        assertThrows(TypeNotPresentException.class, ()-> {
             carDecoderJson.decode("{\"weighT\":12}");
        });

    }

    @Test
    void decodePrimitives() {
        assertEquals(1, new DecoderJson<>(int.class).decode("1"));
        assertEquals(1.123, new DecoderJson<>(double.class).decode("1.123"));
        assertEquals("1.123", new DecoderJson<>(String.class).decode("1.123"));
        assertEquals(true, new DecoderJson<>(boolean.class).decode("true"));
    }
}