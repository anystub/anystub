package org.anystub;

import org.anystub.mgmt.BaseManagerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AnyStubId(filename = "AnyStubFileLocatorTest3-x")
class AnyStubFileLocator3Test {


    {
        Base stub = BaseManagerFactory.getBaseManager().getStub();
        stub.put(Document.fromArray("aaa", "bbb"));
    }

    static {
        Base stub = BaseManagerFactory.getBaseManager().getStub();
        stub.put(Document.fromArray("111", "222"));
    }

    @Test
    void testDiscoverFile() {
        String aaa = BaseManagerFactory
                .getBaseManager()
                .getBase("AnyStubFileLocatorTest3-x.yml")
                .getVals("aaa")
                .iterator()
                .next();
        assertEquals("bbb", aaa);

        String ones = BaseManagerFactory.getBaseManager().getBase("AnyStubFileLocatorTest3-x.yml")
                .getVals("111")
                .iterator()
                .next();
        assertEquals("222", ones);
    }

    @Test
    void testNotAnnotatedMethod() {
        AnyStubId anyStubId = AnyStubFileLocator.discoverFile();
        assertNotNull(anyStubId);
        assertEquals("AnyStubFileLocatorTest3-x-testNotAnnotatedMethod.yml", anyStubId.filename());
    }
    @Test
    @AnyStubId
    void testAnnotatedMethod() {
        AnyStubId anyStubId = AnyStubFileLocator.discoverFile();
        assertNotNull(anyStubId);
        assertEquals("AnyStubFileLocatorTest3-x-testAnnotatedMethod.yml", anyStubId.filename());
    }
    @Test
    @AnyStubId(filename = "filename2")
    void testAnnotatedMethodWFilename() {
        AnyStubId anyStubId = AnyStubFileLocator.discoverFile();
        assertNotNull(anyStubId);
        assertEquals("filename2.yml", anyStubId.filename());
    }
}