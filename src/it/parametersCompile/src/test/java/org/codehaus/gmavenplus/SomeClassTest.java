package org.codehaus.gmavenplus;

import groovy.lang.GroovySystem;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;


public class SomeClassTest {

    @Test
    public void testSomeMethod() throws NoSuchMethodException {
        Method method = SomeClass.class.getDeclaredMethod("someMethod", String.class, String.class);
        List<Parameter> parameterNames = Arrays.asList(method.getParameters());

        Assert.assertEquals(2, parameterNames.size());

        if (GroovySystem.getVersion().startsWith("1.5")
                || GroovySystem.getVersion().startsWith("1.6")
                || GroovySystem.getVersion().startsWith("1.7")
                || GroovySystem.getVersion().startsWith("1.8")
                || GroovySystem.getVersion().startsWith("1.9")
                || GroovySystem.getVersion().startsWith("2.0")
                || GroovySystem.getVersion().startsWith("2.1")
                || GroovySystem.getVersion().startsWith("2.2")
                || GroovySystem.getVersion().startsWith("2.3")
                || GroovySystem.getVersion().startsWith("2.4")) {
            Assert.assertEquals("arg0", parameterNames.get(0).getName());
            Assert.assertEquals("arg1", parameterNames.get(1).getName());
        } else {
            Assert.assertEquals("param1", parameterNames.get(0).getName());
            Assert.assertEquals("param2", parameterNames.get(1).getName());
        }
    }

}
