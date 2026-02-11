package org.codehaus.gmavenplus;

import org.codehaus.gmavenplus.groovy.*;
import org.codehaus.gmavenplus.java.*;
import org.junit.Assert;
import org.junit.Test;


public class TheTest {

    @Test
    public void testYetAnotherMethod() {
        JClass jClass = new JClass();
        GClass gClass = new GClass();
        Assert.assertNotNull(jClass.getgObject());
        Assert.assertNotNull(jClass.getjObject());
        Assert.assertNotNull(gClass.getgObject());
        Assert.assertNotNull(gClass.getjObject());
    }

}
