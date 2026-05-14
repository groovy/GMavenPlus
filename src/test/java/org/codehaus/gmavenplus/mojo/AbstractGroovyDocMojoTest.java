package org.codehaus.gmavenplus.mojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.toolchain.ToolchainManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AbstractGroovyDocMojo class.
 *
 * @author Rick Venutolo
 */
public class AbstractGroovyDocMojoTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

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
        testMojo.windowTitle = "Groovy Documentation";
        testMojo.docTitle = "Groovy Documentation";
        testMojo.footer = "Groovy Documentation";
        testMojo.header = "Groovy Documentation";
        testMojo.scope = "private";
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

    @Test
    public void testSetupProperties() {
        testMojo.windowTitle = "windowTitle";
        testMojo.docTitle = "docTitle";
        testMojo.footer = "footer";
        testMojo.header = "header";
        testMojo.displayAuthor = true;
        testMojo.overviewFile = new File("overviewFile");
        testMojo.syntaxHighlighter = "syntaxHighlighter";
        testMojo.theme = "theme";
        testMojo.showInternal = true;
        testMojo.noIndex = true;
        testMojo.noDeprecatedList = true;
        testMojo.noHelp = true;
        testMojo.noTimestamp = true;
        testMojo.noVersionStamp = true;
        testMojo.processScripts = false;
        testMojo.includeMainForScripts = false;
        testMojo.charset = "charset";
        testMojo.fileEncoding = "fileEncoding";
        testMojo.addStylesheet = Arrays.asList(new File("style1.css"), new File("style2.css"));
        testMojo.scope = "public";

        Properties properties = testMojo.setupProperties();

        assertEquals("windowTitle", properties.getProperty("windowTitle"));
        assertEquals("docTitle", properties.getProperty("docTitle"));
        assertEquals("footer", properties.getProperty("footer"));
        assertEquals("header", properties.getProperty("header"));
        assertEquals("true", properties.getProperty("author"));
        assertEquals(testMojo.overviewFile.getAbsolutePath(), properties.getProperty("overviewFile"));
        assertEquals("syntaxHighlighter", properties.getProperty("syntaxHighlighter"));
        assertEquals("theme", properties.getProperty("theme"));
        assertEquals("true", properties.getProperty("showInternal"));
        assertEquals("true", properties.getProperty("noIndex"));
        assertEquals("true", properties.getProperty("noDeprecatedList"));
        assertEquals("true", properties.getProperty("noHelp"));
        assertEquals("false", properties.getProperty("timestamp"));
        assertEquals("false", properties.getProperty("versionStamp"));
        assertEquals("false", properties.getProperty("processScripts"));
        assertEquals("false", properties.getProperty("includeMainForScripts"));
        assertEquals("charset", properties.getProperty("charset"));
        assertEquals("fileEncoding", properties.getProperty("fileEncoding"));
        assertEquals("style1.css,style2.css", properties.getProperty("additionalStylesheets"));
        assertEquals("true", properties.getProperty("publicScope"));
    }

    @Test
    public void testDoGroovyDocGenerationSetsPreLanguage() throws Exception {
        testMojo.preLanguage = "groovy";
        testMojo.doGroovyDocGeneration(new FileSet[]{new FileSet()}, emptyList(), new File("output"));

        ArgumentCaptor<org.codehaus.gmavenplus.model.GroovyDocConfiguration> captor = ArgumentCaptor.forClass(org.codehaus.gmavenplus.model.GroovyDocConfiguration.class);
        verify(testMojo).performInProcessGroovyDocGeneration(captor.capture());
        assertEquals("groovy", captor.getValue().getPreLanguage());
    }

    @Test
    public void testDoGroovyDocGenerationCopiesAdditionalStylesheets() throws Exception {
        File sourceDir = tempFolder.newFolder("source");
        File outputDir = tempFolder.newFolder("output");
        File styleFile = new File(sourceDir, "style.css");
        styleFile.createNewFile();
        testMojo.addStylesheet = Arrays.asList(styleFile);

        testMojo.doGroovyDocGeneration(new FileSet[]{new FileSet()}, emptyList(), outputDir);

        assertTrue(new File(outputDir, "style.css").exists());
    }

    public static class TestMojo extends AbstractGroovyDocMojo {
        @Override
        public void execute() {
        }
    }

}
