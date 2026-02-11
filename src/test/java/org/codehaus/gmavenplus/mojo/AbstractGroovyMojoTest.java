package org.codehaus.gmavenplus.mojo;

import org.codehaus.gmavenplus.model.internal.Version;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;


/**
 * Unit tests for the AbstractGroovyMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGroovyMojoTest {
    private AbstractGroovyMojo testMojo;

    @Before
    public void setup() {
        testMojo = spy(new TestGroovyMojo());
    }

    @Test
    public void testGetJavaVersion() {
        assertTrue(testMojo.getJavaVersionString() != null && !testMojo.getJavaVersionString().isEmpty());
        assertNotNull(testMojo.getJavaVersion());
    }

    @Test
    public void testIsJavaSupportIndy() {
        doReturn(Version.parseFromString("1.7.0_45")).when(testMojo).getJavaVersion();
        assertTrue(testMojo.isJavaSupportIndy());
    }

    @Test
    public void testIsJavaSupportIndyNo() {
        doReturn(Version.parseFromString("1.6.0_45")).when(testMojo).getJavaVersion();
        assertFalse(testMojo.isJavaSupportIndy());
    }

    @Test
    public void testIsJavaSupportPreviewFeatures() {
        doReturn(Version.parseFromString("12.0.1")).when(testMojo).getJavaVersion();
        assertTrue(testMojo.isJavaSupportPreviewFeatures());
    }

    @Test
    public void testIsJavaSupportPreviewFeaturesNo() {
        doReturn(Version.parseFromString("11.0.3")).when(testMojo).getJavaVersion();
        assertFalse(testMojo.isJavaSupportPreviewFeatures());
    }

    public static class TestGroovyMojo extends AbstractGroovyMojo {
        @Override
        public void execute() {
        }
    }

}
