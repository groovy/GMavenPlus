package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class SomeClassTest {

    @Test
    public void testSomeClassExists() {
        File generatedGroovyDoc = new File("target/gapidocs/org/codehaus/gmavenplus/SomeClass.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
    }

    @Test
    public void testOverviewSummaryExists() {
        File generatedGroovyDoc = new File("target/gapidocs/overview-summary.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
    }

}
