package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.model.internal.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.codehaus.gmavenplus.util.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for the ExecuteMojo class.
 *
 * @author Keegan Witt
 */
public class ExecuteMojoTest {
    private ExecuteMojo executeMojo;

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        executeMojo = new ExecuteMojo();
        executeMojo.bindPropertiesToSeparateVariables = true;
        executeMojo.mojoExecution = mock(MojoExecution.class);
        executeMojo.project = mock(MavenProject.class);
        MojoDescriptor mockMojoDescriptor = mock(MojoDescriptor.class);
        doReturn(mockMojoDescriptor).when(executeMojo.mojoExecution).getMojoDescriptor();
        doReturn("execute").when(mockMojoDescriptor).getGoal();
    }

    @Test
    public void testScriptString() throws Exception {
        File file = tmpDir.newFile();
        String line = "hello world";
        executeMojo.scripts = new String[]{"new File('" + file.getAbsolutePath().replaceAll("\\\\", "/") + "').withWriter { w -> w << '" + line + "' }"};

        executeMojo.execute();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String actualLine = reader.readLine();
        FileUtils.closeQuietly(reader);

        assertEquals(line, actualLine);
    }

    @Test
    public void testScriptPath() throws Exception {
        executeMojo.sourceEncoding = "UTF-8";
        File file = new File("target/testFile.txt");
        String line = "Hello world!";
        executeMojo.scripts = new String[]{new File("src/test/resources/testScript.groovy").getCanonicalPath()};

        String actualLine;
        try {
            executeMojo.execute();
        } finally {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            actualLine = reader.readLine();
            FileUtils.closeQuietly(reader);
            if (!file.delete()) {
                System.err.println("Unable to delete " + file.getAbsolutePath());
            }
        }

        assertEquals(line, actualLine);
    }

    @Test
    public void testScriptURL() throws Exception {
        executeMojo.sourceEncoding = "UTF-8";
        File file = new File("target/testFile.txt");
        String line = "Hello world!";
        executeMojo.scripts = new String[]{new File("src/test/resources/testScript.groovy").toURI().toURL().toString()};

        String actualLine;
        try {
            executeMojo.execute();
        } finally {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            actualLine = reader.readLine();
            FileUtils.closeQuietly(reader);
            if (!file.delete()) {
                System.err.println("Unable to delete " + file.getAbsolutePath());
            }
        }

        assertEquals(line, actualLine);
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        executeMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(Version.parseFromString("1.5.0")).when(executeMojo.classWrangler).getGroovyVersion();
        assertTrue(executeMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        executeMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(Version.parseFromString("1.0")).when(executeMojo.classWrangler).getGroovyVersion();
        assertFalse(executeMojo.groovyVersionSupportsAction());
    }

}
