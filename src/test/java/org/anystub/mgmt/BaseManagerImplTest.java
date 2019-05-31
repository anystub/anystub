package org.anystub.mgmt;

import org.anystub.AnyStubId;
import org.anystub.Base;
import org.anystub.Document;
import org.junit.Test;

import java.io.File;
import java.util.stream.Stream;

import static org.anystub.http.HttpUtil.HTTP_PROPERTY;
import static org.anystub.http.HttpUtil.HTTP_PROPERTY_BODY;
import static org.anystub.mgmt.BaseManagerImpl.getStub;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseManagerImplTest {

    @Test
    public void scenario() {

        BaseManagerImpl.instance().getBase("test.yml");

        boolean expectedError;
        try {
            new Base("test.yml");
            expectedError = false;
        } catch (StubFileAlreadyCreatedException e) {
            expectedError = true;
        }

        assertTrue(expectedError);
        BaseManagerImpl.instance().getBase("test.yml");
        BaseManagerImpl.instance().getBase("test.yml");
        BaseManagerImpl.instance().getBase("test1.yml");

        new Base("test2.yml");
        BaseManagerImpl.instance().getBase("test2.yml");

    }

    @Test
    public void getNamesTest() {
        assertTrue(BaseManagerImpl.getFilePath("test3.yml").endsWith(File.separator + "test3.yml"));
        assertTrue(BaseManagerImpl.getFilePath("src/test3.yml").endsWith(File.separator + "test3.yml"));
        assertTrue(BaseManagerImpl.getFilePath("./test3.yml").endsWith(File.separator + "test3.yml"));
    }

    @Test
    @AnyStubId
    public void testInitializer()  {
       BaseManagerImpl.setDefaultInitializer(base->
               base.addProperty("aaa", "bbbb"));


        Stream<Document> property;
        assertEquals(1, getStub().getProperty("aaa").count());
        assertEquals("bbbb", getStub().getProperty("aaa").findFirst().get().get());


        assertEquals(1, getStub("xxxx.yml").getProperty("aaa").count());
        assertEquals("bbbb", getStub("xxxx.yml").getProperty("aaa").findFirst().get().get());

        BaseManagerImpl.setDefaultInitializer(null);
    }
}