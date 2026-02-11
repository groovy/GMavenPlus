package org.codehaus.gmavenplus.groovyworkarounds;

import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;


/**
 * Unit tests for the DotGroovyFile class.
 *
 * @author Keegan Witt
 */
public class DotGroovyFileTest {

    @Test
    public void testGroovyWithCustomExtension() {
        DotGroovyFile[] dotGroovyFiles = new DotGroovyFile[]{
                new DotGroovyFile("pathname.ext").setScriptExtensions(new HashSet<>(singletonList("ext"))),
                new DotGroovyFile("parent", "child.ext").setScriptExtensions(new HashSet<>(singletonList("ext"))),
                new DotGroovyFile(new File("parent"), "child.ext").setScriptExtensions(new HashSet<>(singletonList("ext"))),
                new DotGroovyFile(new File("filename.ext")).setScriptExtensions(new HashSet<>(singletonList("ext"))),
                new DotGroovyFile(new File("filename.ext").toURI()).setScriptExtensions(new HashSet<>(singletonList("ext")))
        };
        for (DotGroovyFile dotGroovyFile : dotGroovyFiles) {
            assertTrue(dotGroovyFile.getName() + " doesn't end with .groovy", dotGroovyFile.getName().endsWith(".groovy"));
        }
    }

    @Test
    public void testNonGroovyFile() {
        DotGroovyFile[] dotGroovyFiles = new DotGroovyFile[]{
                new DotGroovyFile("pathname.ext"),
                new DotGroovyFile("parent", "child.ext"),
                new DotGroovyFile(new File("parent"), "child.ext"),
                new DotGroovyFile(new File("filename.ext")),
                new DotGroovyFile(new File("filename.ext").toURI())
        };
        for (DotGroovyFile dotGroovyFile : dotGroovyFiles) {
            assertFalse(dotGroovyFile.getName() + " ends with .groovy", dotGroovyFile.getName().endsWith(".groovy"));
        }
    }

    @Test
    public void testGettersAndSetters() {
        Set<String> extensions = new HashSet<>();
        extensions.add("ext");
        DotGroovyFile dotGroovyFile = new DotGroovyFile("")
                .setScriptExtensions(extensions);
        assertEquals(extensions, dotGroovyFile.getScriptExtensions());
    }

}
