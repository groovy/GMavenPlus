package org.codehaus.gmavenplus.mojo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the AbstractToolsMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractToolsMojoTest {
    private TestMojo testMojo;
    @Spy
    private Properties properties;
    @Mock
    private MavenProject project;
    @Mock
    private MavenSession session;
    @Mock
    private List<Artifact> pluginArtifacts;
    @Mock
    private MojoExecution mojoExecution;
    @Mock
    private ClassWrangler classWrangler;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        testMojo = new TestMojo();
        testMojo.project = project;
        testMojo.session = session;
        testMojo.pluginArtifacts = pluginArtifacts;
        testMojo.mojoExecution = mojoExecution;
        testMojo.classWrangler = classWrangler;
        Class<?> antBuilderClass;
        try {
            antBuilderClass = Class.forName("groovy.ant.AntBuilder");
        } catch (ClassNotFoundException e) {
            antBuilderClass = Class.forName("groovy.util.AntBuilder");
        }

        doReturn(antBuilderClass).when(classWrangler).getClass(anyString());
    }

    @Test
    public void testInitializeProperties() {
        testMojo.initializeProperties();

        assertNotNull(testMojo.properties.get("project"));
        assertNotNull(testMojo.properties.get("session"));
        assertNotNull(testMojo.properties.get("pluginArtifacts"));
        assertNotNull(testMojo.properties.get("mojoExecution"));
    }

    @Test
    public void testInitializePropertiesNull() {
        testMojo.project = null;
        testMojo.session = null;
        testMojo.pluginArtifacts = null;
        testMojo.mojoExecution = mojoExecution;

        testMojo.initializeProperties();

        verify(properties, never()).put(eq("project"), any(MavenProject.class));
        verify(properties, never()).put(eq("session"), any(MavenSession.class));
        verify(properties, never()).put(eq("pluginArtifacts"), anyList());
        verify(properties, never()).put(eq("mojoExecution"), any(MojoExecution.class));
    }

    @Test
    public void testInitializePropertiesAlreadyInProps() {
        testMojo.properties = properties;

        testMojo.initializeProperties();
        testMojo.initializeProperties();

        verify(properties, times(1)).put(eq("project"), any(MavenProject.class));
        verify(properties, times(1)).put(eq("session"), any(MavenSession.class));
        verify(properties, times(1)).put(eq("pluginArtifacts"), anyList());
        verify(properties, times(1)).put(eq("mojoExecution"), any(MojoExecution.class));
    }

    @Test
    public void testAddAllProjectProperties() {
        Properties projectProperties = new Properties();
        projectProperties.put("foo", "bar");
        Model model = new Model();
        model.setProperties(projectProperties);
        testMojo.project = new MavenProject(model);
        testMojo.bindAllProjectProperties = true;

        testMojo.initializeProperties();

        assertEquals("bar", testMojo.properties.get("foo"));
    }

    @Test
    public void testAddAllSessionUserProperties() {
        MavenSession session = mock(MavenSession.class);
        Properties sessionProperties = new Properties();
        sessionProperties.put("foo", "bar");
        doReturn(sessionProperties).when(session).getUserProperties();
        testMojo.session = session;
        testMojo.bindAllSessionUserProperties = true;

        testMojo.initializeProperties();

        assertEquals("bar", testMojo.properties.get("foo"));
    }

    @Test
    public void testBindAllProjectPropertiesAndBindSessionUserOverridePropertiesWhenEmpty() {
        MavenSession session = mock(MavenSession.class);
        Properties projectProperties = new Properties();
        projectProperties.put("foo", "bar");
        doReturn(new Properties()).when(session).getUserProperties();
        doReturn(projectProperties).when(project).getProperties();
        testMojo.session = session;
        testMojo.bindAllProjectProperties = true;
        testMojo.bindSessionUserOverrideProperties = true;

        testMojo.initializeProperties();
    }

    @Test
    public void testSessionPropertiesOverrideProjectPropertiesAndIncludesOthers() {
        Properties projectProperties = new Properties();
        projectProperties.put("foo", "bar");
        Model model = new Model();
        model.setProperties(projectProperties);
        testMojo.project = new MavenProject(model);
        MavenSession session = mock(MavenSession.class);
        Properties sessionProperties = new Properties();
        sessionProperties.put("foo", "baz");
        sessionProperties.put("bar", "foo");
        doReturn(sessionProperties).when(session).getUserProperties();
        testMojo.session = session;
        testMojo.bindAllProjectProperties = true;
        testMojo.bindAllSessionUserProperties = true;

        testMojo.initializeProperties();
        assertEquals("baz", testMojo.properties.get("foo"));
        assertEquals("foo", testMojo.properties.get("bar"));
    }

    @Test
    public void testSessionPropertiesOverrideProjectProperties() {
        Properties projectProperties = new Properties();
        projectProperties.put("foo", "bar");
        Model model = new Model();
        model.setProperties(projectProperties);
        testMojo.project = new MavenProject(model);
        MavenSession session = mock(MavenSession.class);
        Properties sessionProperties = new Properties();
        sessionProperties.put("foo", "baz");
        sessionProperties.put("bar", "foo");
        doReturn(sessionProperties).when(session).getUserProperties();
        testMojo.session = session;
        testMojo.bindAllProjectProperties = true;
        testMojo.bindSessionUserOverrideProperties = true;

        testMojo.initializeProperties();
        assertEquals("baz", testMojo.properties.get("foo"));
        assertNull(testMojo.properties.get("bar"));
    }

    protected static class TestMojo extends AbstractToolsMojo {
        @Override
        public void execute() {
        }
    }

}
