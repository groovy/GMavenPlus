package org.codehaus.gmavenplus.mojo;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;


/**
 * Unit tests for the RemoveStubsMojo class.
 *
 * @author Keegan Witt
 */
public class RemoveTestStubsMojoTest {
    private RemoveTestStubsMojo removeTestStubsMojo;
    private static final String PATH = "FAKE_PATH";
    private MavenProject project;

    @Before
    public void setup() {
        removeTestStubsMojo = new RemoveTestStubsMojo();
        project = new MavenProject();
        removeTestStubsMojo.project = project;
        removeTestStubsMojo.testStubsOutputDirectory = new File(PATH);
    }

    @Test
    public void testRemoveTestSourcePathContainsPath() {
        project.addTestCompileSourceRoot(removeTestStubsMojo.testStubsOutputDirectory.getAbsolutePath());
        assertEquals(1, project.getTestCompileSourceRoots().size());
        removeTestStubsMojo.execute();
        assertEquals(0, project.getTestCompileSourceRoots().size());
    }

    @Test
    public void testRemoveTestSourcePathNotContainsPath() {
        assertEquals(0, project.getCompileSourceRoots().size());
        removeTestStubsMojo.execute();
        assertEquals(0, project.getTestCompileSourceRoots().size());
    }

    @Test
    public void testDoesNothingWhenSkipFlagIsSet() {
        project.addTestCompileSourceRoot(removeTestStubsMojo.testStubsOutputDirectory.getAbsolutePath());
        removeTestStubsMojo.skipTests = true;
        removeTestStubsMojo.execute();
        assertEquals(1, project.getTestCompileSourceRoots().size());
    }

}
