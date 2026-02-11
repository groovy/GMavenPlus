package org.codehaus.gmavenplus.mojo;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the AddSourcesMojo class.
 *
 * @author Keegan Witt
 */
public class AddSourcesMojoTest {
    private AddSourcesMojo addSourcesMojo;

    private static final String PATH = "PATH";

    @Mock
    private MavenProject project;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        addSourcesMojo = new AddSourcesMojo();
        addSourcesMojo.project = project;
    }

    @Test
    public void testAddSourcePathContainsPath() {
        doReturn(singletonList(PATH)).when(project).getCompileSourceRoots();
        FileSet fs = new FileSet();
        fs.setDirectory(PATH);
        addSourcesMojo.sources = new FileSet[]{fs};
        addSourcesMojo.execute();
        verify(project, never()).addCompileSourceRoot(anyString());
    }

    @Test
    public void testAddSourcePathNotContainsPath() {
        doReturn(singletonList(PATH)).when(project).getCompileSourceRoots();
        FileSet fs = new FileSet();
        fs.setDirectory("OTHER PATH");
        addSourcesMojo.sources = new FileSet[]{fs};
        addSourcesMojo.execute();
        verify(project, times(1)).addCompileSourceRoot(anyString());
    }

}
