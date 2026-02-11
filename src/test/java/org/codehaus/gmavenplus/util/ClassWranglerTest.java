package org.codehaus.gmavenplus.util;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the ClassWrangler class.
 *
 * @author Keegan Witt
 */
public class ClassWranglerTest {

    @Test
    public void testGetGroovyJar() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(emptyList(), null, mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to force GMavenPlus to get version from jar.")).when(classWrangler).getClass(anyString());
        doReturn("some/path/groovy-all-1.5.0.jar").when(classWrangler).getJarPath();
        assertEquals("groovy-all-1.5.0.jar", classWrangler.getGroovyJar());
    }

    @Test
    public void testGetGroovyVersionStringFromGroovySystemThenFromInvokerHelper() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(emptyList(), null, mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to force GMavenPlus to try other methods."))
                .when(classWrangler).getClass(anyString());
        doReturn("some/path/groovy-all-1.5.0.jar").when(classWrangler).getJarPath();
        ArgumentCaptor<String> classArg = ArgumentCaptor.forClass(String.class);
        classWrangler.getGroovyVersionString();
        verify(classWrangler, times(2)).getClass(classArg.capture());
        assertEquals("groovy.lang.GroovySystem", classArg.getAllValues().get(0));
        assertEquals("org.codehaus.groovy.runtime.InvokerHelper", classArg.getAllValues().get(1));
    }

    @Test
    public void testGetGroovyVersionStringFromJar() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(emptyList(), null, mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to force GMavenPlus to get version from jar.")).when(classWrangler).getClass(anyString());
        doReturn("some/path/groovy-all-1.5.0.jar").when(classWrangler).getJarPath();
        assertEquals("1.5.0", classWrangler.getGroovyVersionString());
    }

    @Test
    public void testGetGroovyVersionWithIndyFromJar() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(emptyList(), null, mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to force GMavenPlus to get version from jar.")).when(classWrangler).getClass(anyString());
        doReturn("some/path/groovy-all-2.4.0-indy.jar").when(classWrangler).getJarPath();
        assertEquals("2.4.0", classWrangler.getGroovyVersion().toString());
    }

    @Test
    public void testGetGroovyVersionWithGrooidFromJar() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(emptyList(), null, mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to force GMavenPlus to get version from jar.")).when(classWrangler).getClass(anyString());
        doReturn("some/path/groovy-all-2.4.0-grooid.jar").when(classWrangler).getJarPath();
        assertEquals("2.4.0", classWrangler.getGroovyVersion().toString());
    }

    @Test
    public void testIsGroovyIndyTrue() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(emptyList(), null, mock(Log.class)));
        doReturn(null).when(classWrangler).getClass(anyString());  // make it appear Groovy is indy
        assertTrue(classWrangler.isGroovyIndy());
    }

    @Test
    public void testIsGroovyIndyFalse() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(emptyList(), null, mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to make it appear Groovy is not indy.")).when(classWrangler).getClass(anyString());
        assertFalse(classWrangler.isGroovyIndy());
    }

}
