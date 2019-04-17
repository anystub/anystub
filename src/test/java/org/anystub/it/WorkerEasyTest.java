package org.anystub.it;

import org.anystub.Base;
import org.anystub.mgmt.BaseManagerImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Kirill on 9/4/2016.
 */
public class WorkerEasyTest {

    SourceSystem sourceSystem;

    @Before
    public void createStub() {
        Base base = BaseManagerImpl.instance().getBase();
        sourceSystem = new SourceSystem("http://localhost:8080") {
            @Override
            public String get() throws IOException {
                SourceSystem th = this;
                return base.request(() -> th.get(),
                        getPath());
            }

            @Override
            public String getPath() {
                try {
                    return super.getPath();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        };
    }

    @Test
    public void xTest() throws IOException {

        Worker worker = new Worker(sourceSystem);
        assertEquals("fixed", worker.get());
    }

}
