package org.codehaus.gmavenplus;

import org.codehaus.gmavenplus.groovy.*;
import org.codehaus.gmavenplus.java.*;
import org.junit.Assert;
import org.junit.Test;


public class TheTest {

    @Test
    public void testAnotherMethod() {
        Child child = new Child();
        Assert.assertEquals("Hello, world.", child.someOtherMethod());
    }

}
