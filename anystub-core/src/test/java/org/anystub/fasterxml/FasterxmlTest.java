package org.anystub.fasterxml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.anystub.AnyStubId;
import org.anystub.ObjectMapperFactory;
import org.anystub.mgmt.BaseManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AnyStubId
class FasterxmlTest {

    @Autowired
    RandomSystem systemr;


    @Test
    void remoteCall() {
        String res = systemr.get("wieurowiuesflksdl ", LocalDate.of(2021,7,13));

        assertEquals("iuesflksdl 2021-07-13", res);
    }


    @Test
    void remoteCallCar() {
        RandomSystem.Car c = new RandomSystem.Car();
        c.setProd(LocalDate.of(2021,11,13));
        c.setWeight(303);
        c.setCapacity(5);
        RandomSystem.Car cc = systemr.get(c);

        assertEquals(359, cc.getWeight());
    }

    @Test
    void getList() {
        List<String> l = systemr.getL("unique-param");

        assertEquals(asList("111", "222"), l);
    }


    @Test
    void postCallTest() {
        RandomSystem.Car c = new RandomSystem.Car();
        c.setProd(LocalDate.of(2021,01,12));
        c.setWeight(100);
        c.setCapacity(50);
        systemr.post(c);

        assertEquals(1, BaseManagerFactory.locate().times("post"));
    }

    @Test
    void recoverWithRefType() throws JsonProcessingException {
        TypeReference<List<String>> returnType = new TypeReference<>() {};
        ObjectMapper objectMapper = ObjectMapperFactory.get();

        String in = "[\"111\", \"222\"]";
        List<String> strings = objectMapper.readValue(in, returnType);
        assertEquals(asList("111","222"), strings);
    }


    @TestConfiguration
    static class Conf {

        @Bean
        @Primary
        public RandomSystem systemrTest(@Qualifier("randomSystem") RandomSystem v) {
            return new RandomSystemT(v);
        }
    }


}
