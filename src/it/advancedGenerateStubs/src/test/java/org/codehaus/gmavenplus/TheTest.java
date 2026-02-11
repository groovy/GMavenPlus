package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class TheTest {

    @Test
    public void testSomeClassExists() {
        File generatedStub = new File("target/generated-sources/groovy-stubs/main/org/codehaus/gmavenplus/SomeClass.java");
        Assert.assertTrue(generatedStub + " does not exist.", generatedStub.exists());
        Assert.assertTrue(generatedStub + " is empty.", generatedStub.length() > 0);
    }

    @Test
    public void testSomeOtherClassExists() {
        File generatedStub = new File("target/generated-sources/groovy-stubs/main/org/codehaus/gmavenplus/SomeOtherClass.java");
        Assert.assertTrue(generatedStub + " does not exist.", generatedStub.exists());
        Assert.assertTrue(generatedStub + " is empty.", generatedStub.length() > 0);
    }

}
