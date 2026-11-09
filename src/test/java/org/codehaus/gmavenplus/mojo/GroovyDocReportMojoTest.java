package org.codehaus.gmavenplus.mojo;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class GroovyDocReportMojoTest {

    @Parameterized.Parameters(name = "testSources={0}")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList(new Object[][]{{false}, {true}});
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final boolean testSources;
    private AbstractGroovyDocMojo mojo;
    private MavenReport report;
    private File sourceDirectory;
    private File javaSourceDirectory;
    private File standaloneOutput;

    public GroovyDocReportMojoTest(boolean testSources) {
        this.testSources = testSources;
    }

    @Before
    public void setup() throws Exception {
        mojo = testSources ? spy(new GroovyDocTestsReportMojo()) : spy(new GroovyDocReportMojo());
        report = (MavenReport) mojo;
        MavenProject project = spy(new MavenProject());
        project.setFile(new File(temporaryFolder.getRoot(), "pom.xml"));
        project.setBuild(new Build());
        sourceDirectory = temporaryFolder.newFolder("src", testSources ? "test" : "main", "groovy");
        javaSourceDirectory = temporaryFolder.newFolder("java");
        if (testSources) {
            project.addTestCompileSourceRoot(javaSourceDirectory.getAbsolutePath());
        } else {
            project.addCompileSourceRoot(javaSourceDirectory.getAbsolutePath());
        }
        mojo.project = project;
        doReturn(Collections.singletonList("runtime-classpath")).when(project).getRuntimeClasspathElements();
        doReturn(Collections.singletonList("test-classpath")).when(project).getTestClasspathElements();
        standaloneOutput = temporaryFolder.newFolder("standalone");
        if (testSources) {
            ((GroovyDocTestsMojo) mojo).testGroovyDocOutputDirectory = standaloneOutput;
            ((GroovyDocTestsMojo) mojo).testGroovyDocJavaSources = true;
        } else {
            ((GroovyDocMojo) mojo).groovyDocOutputDirectory = standaloneOutput;
            ((GroovyDocMojo) mojo).groovyDocJavaSources = true;
        }
        report.setReportOutputDirectory(temporaryFolder.newFolder("site"));
        doAnswer(invocation -> {
            File output = invocation.getArgument(2);
            Files.createDirectories(output.toPath());
            Files.write(new File(output, "index.html").toPath(), "<html></html>".getBytes(StandardCharsets.UTF_8));
            return null;
        }).when(mojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
    }

    @Test
    public void metadataMatchesReportType() {
        assertTrue(report.isExternalReport());
        assertEquals(MavenReport.CATEGORY_PROJECT_REPORTS, report.getCategoryName());
        assertEquals(testSources ? "Test Groovydoc" : "Groovydoc", report.getName(Locale.ENGLISH));
        assertFalse(report.getDescription(Locale.ENGLISH).isEmpty());
        assertEquals(outputDirectoryName() + "/index", report.getOutputPath());
        assertEquals(report.getOutputPath(), report.getOutputName());
    }

    @Test
    public void emptySourcesSuppressReport() throws Exception {
        assertFalse(report.canGenerateReport());
        report.generate(null, Locale.ENGLISH);
        mojo.execute();
        verify(mojo, never()).doGroovyDocGeneration(any(), anyList(), any());
    }

    @Test
    public void sourcesFromOtherScopeDoNotEnableReport() throws Exception {
        File otherSources = temporaryFolder.newFolder("src", testSources ? "main" : "test", "groovy");
        writeSource(otherSources, "Example.groovy");
        assertFalse(report.canGenerateReport());
    }

    @Test
    public void groovySourcesEnableReportUnlessSkipped() throws Exception {
        writeSource(sourceDirectory, "Example.groovy");
        assertTrue(report.canGenerateReport());
        mojo.skipGroovyDoc = true;
        assertFalse(report.canGenerateReport());
        report.generate(null, Locale.ENGLISH);
        mojo.execute();
        verify(mojo, never()).doGroovyDocGeneration(any(), anyList(), any());
    }

    @Test
    public void javaSourcesHonorInclusionSetting() throws Exception {
        writeSource(javaSourceDirectory, "Example.java");
        assertTrue(report.canGenerateReport());
        if (testSources) {
            ((GroovyDocTestsMojo) mojo).testGroovyDocJavaSources = false;
        } else {
            ((GroovyDocMojo) mojo).groovyDocJavaSources = false;
        }
        assertFalse(report.canGenerateReport());
    }

    @Test
    public void configuredSourcesHonorExclusions() throws Exception {
        File customSources = temporaryFolder.newFolder("custom");
        writeSource(customSources, "Example.groovy");
        FileSet fileSet = new FileSet();
        fileSet.setDirectory(customSources.getAbsolutePath());
        fileSet.setIncludes(Collections.singletonList("**/*.groovy"));
        if (testSources) {
            ((GroovyDocTestsMojo) mojo).testSources = new FileSet[]{fileSet};
        } else {
            ((GroovyDocMojo) mojo).sources = new FileSet[]{fileSet};
        }
        assertTrue(report.canGenerateReport());
        fileSet.setExcludes(Collections.singletonList("**/Example.groovy"));
        assertFalse(report.canGenerateReport());
    }

    @Test
    public void generationAndDirectExecutionUseRelocatedSiteDirectory() throws Exception {
        writeSource(sourceDirectory, "Example.groovy");
        File relocated = temporaryFolder.newFolder("relocated");
        report.setReportOutputDirectory(relocated);
        report.generate(null, Locale.ENGLISH);
        mojo.execute();
        assertEquals(relocated, report.getReportOutputDirectory());
        verify(mojo, times(2)).doGroovyDocGeneration(any(FileSet[].class),
                eq(Collections.singletonList(testSources ? "test-classpath" : "runtime-classpath")),
                eq(new File(relocated, outputDirectoryName())));
        assertEquals(standaloneOutput, testSources
                ? ((GroovyDocTestsMojo) mojo).testGroovyDocOutputDirectory
                : ((GroovyDocMojo) mojo).groovyDocOutputDirectory);
    }

    @Test
    public void generationPreservesFailureCause() throws Exception {
        writeSource(sourceDirectory, "Example.groovy");
        ClassNotFoundException failure = new ClassNotFoundException("missing Groovy");
        doThrow(failure).when(mojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
        try {
            report.generate(null, Locale.ENGLISH);
            fail("Expected report failure");
        } catch (MavenReportException e) {
            assertTrue(e.getCause() instanceof MojoExecutionException);
            assertSame(failure, e.getCause().getCause());
        }
        assertEquals(standaloneOutput, testSources
                ? ((GroovyDocTestsMojo) mojo).testGroovyDocOutputDirectory
                : ((GroovyDocMojo) mojo).groovyDocOutputDirectory);
    }

    @Test
    public void missingLandingPageFailsDirectExecution() throws Exception {
        writeSource(sourceDirectory, "Example.groovy");
        doNothing().when(mojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
        try {
            mojo.execute();
            fail("Expected failure for missing report landing page");
        } catch (MojoExecutionException e) {
            assertTrue(e.getMessage().contains(new File(report.getReportOutputDirectory(),
                    outputDirectoryName() + "/index.html").getPath()));
            assertTrue(e.getMessage().contains("defaultDocTemplates"));
        }
    }

    @Test
    public void missingLandingPageFailsReportGeneration() throws Exception {
        writeSource(sourceDirectory, "Example.groovy");
        doNothing().when(mojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
        try {
            report.generate(null, Locale.ENGLISH);
            fail("Expected failure for missing report landing page");
        } catch (MavenReportException e) {
            assertTrue(e.getCause() instanceof MojoExecutionException);
            assertTrue(e.getCause().getMessage().contains("index.html"));
        }
    }

    private String outputDirectoryName() {
        return testSources ? "testgapidocs" : "gapidocs";
    }

    private void writeSource(File directory, String name) throws Exception {
        Files.write(new File(directory, name).toPath(), "class Example {}".getBytes(StandardCharsets.UTF_8));
    }
}
