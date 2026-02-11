package org.codehaus.gmavenplus.mojo;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.mockito.Mockito.verify;


/**
 * Unit tests for the AddStubSourcesMojo class.
 *
 * @author Keegan Witt
 */
public class AddStubSourcesMojoTest {
    private AddStubSourcesMojo addStubSourcesMojo;
    @Mock
    private MavenProject project;
    @Mock
    private File stubsOutputDirectory;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        addStubSourcesMojo = new AddStubSourcesMojo();
        addStubSourcesMojo.project = project;
        addStubSourcesMojo.stubsOutputDirectory = stubsOutputDirectory;
    }

    @Test
    public void testAddsStubsToSources() {
        addStubSourcesMojo.execute();
        verify(project).addCompileSourceRoot(stubsOutputDirectory.getAbsolutePath());
    }

}
