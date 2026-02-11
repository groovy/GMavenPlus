package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class SomeClassTest {

    @Test
    public void testSomeClassStubExists() {
        File generatedStub = new File("target/generated-sources/groovy-stubs/main/org/codehaus/gmavenplus/SomeClass.java");
        Assert.assertTrue(generatedStub + " does not exist.", generatedStub.exists());
        Assert.assertTrue(generatedStub + " is empty.", generatedStub.length() > 0);
    }

}
