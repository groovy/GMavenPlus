package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class SomeClassTest {

    @Test
    public void testGroovyDocExists() {
        boolean foundGroovydocDir = false;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile("target/gmavenplus-plugin-it-basicGroovyDocJar-testing-groovydoc.jar");
            Enumeration<? extends ZipEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                String entryName = entry.getName();
                if ("org/codehaus/gmavenplus/SomeClass.html".equals(entryName)) {
                    foundGroovydocDir = true;
                    break;
                }
            }
        } catch (IOException ioe) {
            System.err.println("Error opening zip file" + ioe);
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing zip file" + ioe);
            }
        }
        Assert.assertTrue(foundGroovydocDir);
    }

}
