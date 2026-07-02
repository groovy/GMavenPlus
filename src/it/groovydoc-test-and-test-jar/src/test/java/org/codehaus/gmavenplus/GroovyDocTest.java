package org.codehaus.gmavenplus;

import java.io.File;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GroovyDocTest {
    @Test
    public void generatesTestDocumentation() {
        assertTrue(new File("target/testgapidocs/index.html").isFile());
    }
}
