package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;


public class SomeClassTest {

    @Test
    public void testSomeMethod() {
        SomeClass someClass = new SomeClass();
        Assert.assertEquals("[dog, cat]", someClass.someMethod().toString());
    }

}
