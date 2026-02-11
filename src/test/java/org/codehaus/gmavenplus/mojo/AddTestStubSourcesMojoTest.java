package org.codehaus.gmavenplus.mojo;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for the AddTestStubSourcesMojo class.
 *
 * @author Keegan Witt
 */
public class AddTestStubSourcesMojoTest {
    private AddTestStubSourcesMojo addTestStubSourcesMojo;
    @Mock
    private MavenProject project;
    @Mock
    private File outputDirectory;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        addTestStubSourcesMojo = new AddTestStubSourcesMojo();
        addTestStubSourcesMojo.project = project;
        addTestStubSourcesMojo.testStubsOutputDirectory = outputDirectory;
    }

    @Test
    public void testAddsTestStubsToSources() {
        addTestStubSourcesMojo.execute();
        verify(project).addTestCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

    @Test
    public void testDoesNothingWhenSkipFlagIsSet() {
        addTestStubSourcesMojo.skipTests = true;
        addTestStubSourcesMojo.execute();
        verify(project, never()).addTestCompileSourceRoot(outputDirectory.getAbsolutePath());
    }
}
