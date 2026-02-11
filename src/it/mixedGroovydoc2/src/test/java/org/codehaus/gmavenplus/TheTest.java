package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class TheTest {

    @Test
    public void testClassesExist() {
        File generatedGroovyDoc1 = new File("target/gapidocs/org/codehaus/gmavenplus/groovy/Child.html");
        Assert.assertTrue(generatedGroovyDoc1 + " does not exist.", generatedGroovyDoc1.exists());
        Assert.assertTrue(generatedGroovyDoc1 + " is empty.", generatedGroovyDoc1.length() > 0);
        File generatedGroovyDoc2 = new File("target/gapidocs/org/codehaus/gmavenplus/java/Parent.html");
        Assert.assertTrue(generatedGroovyDoc2 + " does not exist.", generatedGroovyDoc2.exists());
        Assert.assertTrue(generatedGroovyDoc2 + " is empty.", generatedGroovyDoc2.length() > 0);
        File generatedGroovyDoc3 = new File("target/gapidocs/org/codehaus/gmavenplus/java/Grandchild.html");
        Assert.assertTrue(generatedGroovyDoc3 + " does not exist.", generatedGroovyDoc3.exists());
        Assert.assertTrue(generatedGroovyDoc3 + " is empty.", generatedGroovyDoc3.length() > 0);
    }

    @Test
    public void testOverviewSummaryExists() {
        File generatedGroovyDoc = new File("target/gapidocs/overview-summary.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
    }

}
