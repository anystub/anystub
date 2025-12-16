package org.anystub.fasterxml;


import org.anystub.RandomGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class RandomSystem {

    public static class Car {
        int weight;
        int capacity;
        LocalDate prod;

        public Car() {

        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public LocalDate getProd() {
            return prod;
        }

        public void setProd(LocalDate prod) {
            this.prod = prod;
        }
    }


    public String get(String test, LocalDate d) {
        String v = test+d.toString();
        int i = (int)(Math.random()*v.length());
        return v.substring(i);
    }

    public Car get(Car c) {
        Car cc = new Car();
        int i = (int)(Math.random()*100);
        cc.setCapacity(c.getCapacity()+i+20);
        cc.setWeight(c.getWeight()+i+20);
        cc.setProd(c.getProd().minusDays(20).minusDays(i));

        return cc;
    }
    public List<String> getL(String param) {
        List<String> res = new ArrayList<>();
        for(int i=0; i<10; i++){
            res.add(RandomGenerator.gString());
        }
        return res;
    }

    public void post(Car c) {
    }
}
