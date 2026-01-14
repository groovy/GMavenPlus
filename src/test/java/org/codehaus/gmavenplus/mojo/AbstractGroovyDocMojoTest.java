package org.codehaus.gmavenplus.mojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.toolchain.ToolchainManager;
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

    @Mock
    private ToolchainManager toolchainManager;

    @Mock
    private MavenSession session;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        testMojo.mojoExecution = mojoExecution;
        doReturn(mojoDescriptor).when(mojoExecution).getMojoDescriptor();
        testMojo.project = project;
        testMojo.toolchainManager = toolchainManager;
        testMojo.session = session;
        doReturn(new Properties()).when(testMojo).setupProperties();
        doNothing().when(testMojo).performInProcessGroovyDocGeneration(any(org.codehaus.gmavenplus.model.GroovyDocConfiguration.class));
    }

    @Test
    public void testDontSkipGroovyDoc() throws Exception {
        testMojo.doGroovyDocGeneration(new FileSet[]{new FileSet()}, emptyList(), new File(""));
        verify(testMojo, times(1)).performInProcessGroovyDocGeneration(any(org.codehaus.gmavenplus.model.GroovyDocConfiguration.class));
    }

    @Test
    public void testSkipGroovyDoc() throws Exception {
        testMojo.skipGroovyDoc = true;
        testMojo.doGroovyDocGeneration(new FileSet[]{new FileSet()}, emptyList(), new File(""));
        verify(testMojo, never()).performInProcessGroovyDocGeneration(any(org.codehaus.gmavenplus.model.GroovyDocConfiguration.class));
    }

    public static class TestMojo extends AbstractGroovyDocMojo {
        @Override
        public void execute() {
        }
    }

}
