package org.anystub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncoderJsonTest {
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
    void encode() {
        EncoderJson<Car> carEncoderJson = new EncoderJson<>();
        Car car = new Car();
        car.setLength(13);
        car.setWeight(11);
        String encode = carEncoderJson.encode(car);
        assertEquals("{\"weight\":11,\"length\":13}", encode);
    }


}