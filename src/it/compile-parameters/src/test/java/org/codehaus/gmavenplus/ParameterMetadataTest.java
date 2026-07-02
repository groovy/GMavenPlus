package org.codehaus.gmavenplus;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;


public class ParameterMetadataTest {

    @Test
    public void testParameterNamesAreAvailable() throws NoSuchMethodException {
        Method method = ParameterFixture.class.getDeclaredMethod("join", String.class, String.class);

        Assert.assertTrue(method.getParameters()[0].isNamePresent());
        Assert.assertTrue(method.getParameters()[1].isNamePresent());
        Assert.assertEquals("firstName", method.getParameters()[0].getName());
        Assert.assertEquals("lastName", method.getParameters()[1].getName());
        Assert.assertEquals("Ada Lovelace", new ParameterFixture().join("Ada", "Lovelace"));
    }

}
