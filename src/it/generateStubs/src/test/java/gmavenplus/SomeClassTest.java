package gmavenplus;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class SomeClassTest {

    @Test
    public void testSomeMethod() {
        SomeClass someClass = new SomeClass();
        File generatedStub = new File("target/generated-sources/groovy-stubs/main/gmavenplus/SomeClass.java");
        Assert.assertTrue(generatedStub.exists());
    }

}
