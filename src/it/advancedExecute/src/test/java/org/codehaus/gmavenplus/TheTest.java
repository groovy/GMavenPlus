package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class TheTest {

    @Test
    public void test1() {
        File fileFromScript = new File("target/helloWorld.txt");
        Assert.assertTrue(fileFromScript.exists());
        Assert.assertTrue(fileFromScript.length() > 0);
    }

    @Test
    public void test2() {
        File fileFromScript = new File("target/helloWorld2.txt");
        Assert.assertTrue(fileFromScript.exists());
        Assert.assertTrue(fileFromScript.length() > 0);
    }

    @Test
    public void test3() {
        File fileFromScript = new File("target/javaVersion.txt");
        Assert.assertTrue(fileFromScript.exists());
        Assert.assertTrue(fileFromScript.length() > 0);
    }

}
