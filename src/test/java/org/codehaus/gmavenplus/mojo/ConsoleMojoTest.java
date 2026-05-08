package org.codehaus.gmavenplus.mojo;

import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ConsoleMojo class.
 */
public class ConsoleMojoTest {

    private ConsoleMojo consoleMojo;

    @Mock
    private ClassWrangler classWrangler;

    @Mock
    private MavenProject project;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        consoleMojo = new ConsoleMojo();
        consoleMojo.classWrangler = classWrangler;
        consoleMojo.project = project;
        when(project.getProperties()).thenReturn(new Properties());

        Class<?> antBuilderClass;
        try {
            antBuilderClass = Class.forName("groovy.ant.AntBuilder");
        } catch (ClassNotFoundException e) {
            antBuilderClass = Class.forName("groovy.util.AntBuilder");
        }
        when(classWrangler.getClass("groovy.ant.AntBuilder")).thenReturn((Class) antBuilderClass);
        when(classWrangler.getClass("groovy.util.AntBuilder")).thenReturn((Class) antBuilderClass);
        when(classWrangler.getClass("groovy.lang.GroovyShell")).thenReturn((Class) TestShell.class);
    }

    @Test
    public void testInitializeAnt() {
        consoleMojo.initializeAnt();
        assertEquals(Boolean.TRUE, consoleMojo.properties.get("ant"));
    }

    @Test
    public void testBindAntBuilder() throws Exception {
        consoleMojo.initializeAnt();
        assertEquals(Boolean.TRUE, consoleMojo.properties.get("ant"));

        Class<?> consoleClass = TestConsole.class;
        Class<?> bindingClass = TestBinding.class;
        TestConsole console = new TestConsole();

        consoleMojo.bindAntBuilder(consoleClass, bindingClass, console);

        Object ant = consoleMojo.properties.get("ant");
        assertNotNull(ant);
        assertNotEquals(Boolean.TRUE, ant);
        assertTrue(ant.getClass().getName().contains("AntBuilder"));
    }

    public static class TestConsole {
        public TestShell shell = new TestShell();
    }

    public static class TestShell {
        public TestBinding getContext() {
            return new TestBinding();
        }
    }

    public static class TestBinding {
        public void setVariable(String name, Object value) {
        }
    }
}
