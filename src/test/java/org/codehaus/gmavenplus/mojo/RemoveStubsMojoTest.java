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
public class RemoveStubsMojoTest {
    private RemoveStubsMojo removeStubsMojo;
    private static final String PATH = "FAKE_PATH";
    private MavenProject project;

    @Before
    public void setup() {
        removeStubsMojo = new RemoveStubsMojo();
        project = new MavenProject();
        removeStubsMojo.project = project;
        removeStubsMojo.stubsOutputDirectory = new File(PATH);
    }

    @Test
    public void testRemoveSourcePathContainsPath() {
        project.addCompileSourceRoot(removeStubsMojo.stubsOutputDirectory.getAbsolutePath());
        assertEquals(1, project.getCompileSourceRoots().size());
        removeStubsMojo.execute();
        assertEquals(0, project.getCompileSourceRoots().size());
    }

    @Test
    public void testRemoveSourcePathNotContainsPath() {
        assertEquals(0, project.getCompileSourceRoots().size());
        removeStubsMojo.execute();
        assertEquals(0, project.getCompileSourceRoots().size());
    }

}
