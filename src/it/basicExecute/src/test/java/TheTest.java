package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class TheTest {

    @Test
    public void test() {
        File fileFromScript = new File("target/helloWorld.txt");
        Assert.assertTrue(fileFromScript.exists());
        Assert.assertTrue(fileFromScript.length() > 0);
    }

}
