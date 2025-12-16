package org.anystub.fasterxml;

import com.fasterxml.jackson.core.type.TypeReference;
import org.anystub.mgmt.BaseManagerFactory;

import java.time.LocalDate;
import java.util.List;

public class RandomSystemT extends RandomSystem {
    private final RandomSystem v;

    public RandomSystemT(RandomSystem v) {
        this.v = v;
    }

    @Override
    public String get(String test, LocalDate d) {
        return BaseManagerFactory.locate()
                .request(() -> v.get(test, d), String.class, test, d);
    }

    @Override
    public Car get(Car c) {
        return BaseManagerFactory.locate()
                .request(() -> v.get(c), Car.class, c);
    }

    @Override
    public List<String> getL(String param) {
        TypeReference<List<String>> returnType = new TypeReference<>() {};
        return BaseManagerFactory.locate()
                .request(()->v.getL(param),
                        returnType, "getL", param);
    }

    @Override
    public void post(Car c) {
        BaseManagerFactory.locate()
                .post(() -> {
                    v.post(c);
                }, "post", c);
    }
}
