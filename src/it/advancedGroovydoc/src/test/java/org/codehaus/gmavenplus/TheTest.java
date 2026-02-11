package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class TheTest {

    @Test
    public void testSomeClassExistsAndContainsJavaLinks() throws Exception {
        File generatedGroovyDoc = new File("target/gapidocs/org/codehaus/gmavenplus/SomeClass.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
        Assert.assertTrue(generatedGroovyDoc + " does not contain Java 5 links.", readFileToString(generatedGroovyDoc)
                .contains("https://docs.oracle.com/en/java/javase/11/docs/api/"));
    }

    @Test
    public void testSomeOtherClassExistsAndContainsJavaLinks() throws Exception {
        File generatedGroovyDoc = new File("target/gapidocs/org/codehaus/gmavenplus/SomeOtherClass.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
        Assert.assertTrue(generatedGroovyDoc + " does not contain Java 5 links.", readFileToString(generatedGroovyDoc)
                .contains("https://docs.oracle.com/en/java/javase/11/docs/api/"));
    }

    @Test
    public void testOverviewSummaryExistsAndContainsStylesheetLink() throws Exception {
        File generatedGroovyDoc = new File("target/gapidocs/overview-summary.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
        Assert.assertTrue(generatedGroovyDoc + " does not contain stylesheet link.", readFileToString(generatedGroovyDoc)
                .contains("<link rel =\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\" title=\"Style\">"));
    }

    private String readFileToString(File file) throws IOException {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            return scanner.useDelimiter("\\A").next();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

}
