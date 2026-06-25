package org.codehaus.gmavenplus.mojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.model.internal.Version;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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

    @Test
    public void testTargetBytecodeWinsOverRelease() {
        assertEquals("11", TestGroovyMojo.resolveTargetBytecode("11", "17", "21"));
    }

    @Test
    public void testReleaseIsUsedWhenTargetBytecodeIsUnset() {
        assertEquals("17", TestGroovyMojo.resolveTargetBytecode(null, "17", "21"));
    }

    @Test
    public void testReleaseWinsOverMavenCompilerTarget() {
        assertEquals("17", TestGroovyMojo.resolveTargetBytecode(null, "17", "11"));
    }

    @Test
    public void testMavenCompilerTargetIsUsedWhenTargetBytecodeAndReleaseAreUnset() {
        assertEquals("11", TestGroovyMojo.resolveTargetBytecode(null, null, "11"));
    }

    @Test
    public void testReleaseIsTranslatedFromJavacTargetFormat() {
        assertEquals("1.8", TestGroovyMojo.resolveTargetBytecode(null, "8", null));
    }

    @Test
    public void testMavenCompilerTargetIsTranslatedFromJavacTargetFormat() {
        assertEquals("1.8", TestGroovyMojo.resolveTargetBytecode(null, null, "8"));
    }

    @Test
    public void testDefaultTargetBytecodeIsUsedWhenTargetBytecodeAndReleaseAreUnset() {
        assertEquals("1.8", TestGroovyMojo.resolveTargetBytecode(null, null, null));
    }

    @Test
    public void testBlankTargetBytecodeFallsBackToRelease() {
        assertEquals("17", TestGroovyMojo.resolveTargetBytecode(" ", "17", "21"));
    }

    @Test
    public void testMavenCompilerPropertiesAreNotExposedAsMojoParameters() {
        assertFalse(isMojoParameter(AbstractCompileMojo.class, "release"));
        assertFalse(isMojoParameter(AbstractCompileMojo.class, "compilerRelease"));
        assertFalse(isMojoParameter(AbstractCompileMojo.class, "compilerTarget"));
        assertFalse(isMojoParameter(AbstractGenerateStubsMojo.class, "release"));
        assertFalse(isMojoParameter(AbstractGenerateStubsMojo.class, "compilerRelease"));
        assertFalse(isMojoParameter(AbstractGenerateStubsMojo.class, "compilerTarget"));
    }

    @Test
    public void testResolveTargetBytecodeUsesMavenProperties() {
        Properties projectProperties = new Properties();
        projectProperties.setProperty("maven.compiler.release", "17");
        projectProperties.setProperty("maven.compiler.target", "11");
        testMojo.project = mock(MavenProject.class);
        doReturn(projectProperties).when(testMojo.project).getProperties();

        assertEquals("17", testMojo.resolveTargetBytecode(null));
    }

    @Test
    public void testResolveTargetBytecodeUsesUserPropertyBeforeSystemAndProjectProperties() {
        Properties userProperties = new Properties();
        userProperties.setProperty("maven.compiler.release", "23");
        Properties systemProperties = new Properties();
        systemProperties.setProperty("maven.compiler.release", "21");
        Properties projectProperties = new Properties();
        projectProperties.setProperty("maven.compiler.release", "17");
        testMojo.session = mock(MavenSession.class);
        doReturn(userProperties).when(testMojo.session).getUserProperties();
        doReturn(systemProperties).when(testMojo.session).getSystemProperties();
        testMojo.project = mock(MavenProject.class);
        doReturn(projectProperties).when(testMojo.project).getProperties();

        assertEquals("23", testMojo.resolveTargetBytecode(null));
    }

    @Test
    public void testResolveTargetBytecodeUsesSystemPropertyBeforeProjectProperty() {
        Properties systemProperties = new Properties();
        systemProperties.setProperty("maven.compiler.release", "21");
        Properties projectProperties = new Properties();
        projectProperties.setProperty("maven.compiler.release", "17");
        testMojo.session = mock(MavenSession.class);
        doReturn(new Properties()).when(testMojo.session).getUserProperties();
        doReturn(systemProperties).when(testMojo.session).getSystemProperties();
        testMojo.project = mock(MavenProject.class);
        doReturn(projectProperties).when(testMojo.project).getProperties();

        assertEquals("21", testMojo.resolveTargetBytecode(null));
    }

    private static boolean isMojoParameter(Class<?> mojoClass, String fieldName) {
        try {
            Field field = mojoClass.getDeclaredField(fieldName);
            return field.isAnnotationPresent(Parameter.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static class TestGroovyMojo extends AbstractGroovyMojo {
        @Override
        public void execute() {
        }
    }

}
