package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.File;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AbstractGroovyDocMojo class.
 *
 * @author Rick Venutolo
 */
public class AbstractGroovyDocMojoTest {
    @Spy
    private TestMojo testMojo;

    @Mock
    private MojoExecution mojoExecution;

    @Mock
    private MojoDescriptor mojoDescriptor;

    @Mock
    private MavenProject project;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        testMojo.mojoExecution = mojoExecution;
        doReturn(mojoDescriptor).when(mojoExecution).getMojoDescriptor();
        testMojo.project = project;
        doReturn(new Properties()).when(testMojo).setupProperties();
        doReturn(emptyList()).when(testMojo).setupGroovyDocSources(any(FileSet[].class), any(FileSetManager.class));
        doNothing().when(testMojo).generateGroovyDoc(any(File.class), any(Class.class), any(Class.class), any(), anyList(), any());
    }

    @Test
    public void testDontSkipGroovyDoc() throws Exception {
        testMojo.doGroovyDocGeneration(new FileSet[]{new FileSet()}, emptyList(), new File(""));
        verify(testMojo, times(1)).generateGroovyDoc(any(File.class), any(Class.class), any(Class.class), any(), anyList(), any());
    }

    @Test
    public void testSkipGroovyDoc() throws Exception {
        testMojo.skipGroovyDoc = true;
        testMojo.doGroovyDocGeneration(new FileSet[]{new FileSet()}, emptyList(), new File(""));
        verify(testMojo, never()).generateGroovyDoc(any(File.class), any(Class.class), any(Class.class), any(), anyList(), any());
    }

    public static class TestMojo extends AbstractGroovyDocMojo {
        @Override
        public void execute() {
        }
    }

}
