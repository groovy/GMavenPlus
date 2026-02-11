package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;


public class TheTest {

    @Test
    public void testSomeMethod() {
        SomeClass someClass = new SomeClass();
        Assert.assertEquals("Hello, world.", someClass.someMethod());
    }

    @Test
    public void testSomeMethod2() {
        SomeOtherClass someOtherClass = new SomeOtherClass();
        Assert.assertEquals("Hello, world.", someOtherClass.someMethod());
    }

}
