package org.anystub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Random;

import static org.anystub.RandomGenerator.gString;
import static org.anystub.mgmt.BaseManagerFactory.locate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceTest {


    interface System {
        String exec(String... in);

    }

    static class SystemImpl implements System {
        public String exec(String... in) {
            return "response" + in.length;
        }
    }

    static class StubSystem implements System {

        final System system;

        StubSystem(System system) {
            this.system = system;
        }

        @Override
        public String exec(String... in) {
            return locate()
                    .request(() -> system.exec(in), String.class, in);
        }
    }

    System system;

    @BeforeEach
    void setup() {
        system = new StubSystem(new SystemImpl());
    }


    @Test
    @AnyStubId
    void test400x7Test() {
        String res;

        locate().clear();
        new File(locate().getFilePath()).delete();

        int size = 1600;

        Random random;
        random = RandomGenerator.initRandomizer(123);
        for (int i = 0; i < size; i++) {
            int i1 = random.nextInt(5) + 3;
            String[] in = new String[i1];
            for (int j = 0; j < i1; j++) {
                in[j] = gString();
            }
            in[0] = Integer.toString(i);
            res = system.exec(in);
            assertTrue(res.startsWith("response"));
        }

        assertEquals(size, locate().times());

        random = RandomGenerator.initRandomizer(123);
        for (int i = 0; i < size; i++) {
            int i1 = random.nextInt(5) + 3;
            String[] in = new String[i1];
            for (int j = 0; j < i1; j++) {
                in[j] = gString();
            }
            in[0] = Integer.toString(i);
            res = system.exec(in);
            assertTrue(res.startsWith("response"));
        }
        assertEquals(size*2, locate().times());

    }


}
