package org.codehaus.gmavenplus.mojo;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.util.Locale;

/**
 * Generates a Maven Site report for the main sources.
 *
 * @since 5.1.1
 */
@Mojo(name = "groovydoc-report", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class GroovyDocReportMojo extends GroovyDocMojo implements MavenReport {

    /**
     * The shared output directory for Maven Site reports.
     */
    @Parameter(defaultValue = "${project.reporting.outputDirectory}")
    protected File reportOutputDirectory;

    @Override
    public File getReportOutputDirectory() {
        return reportOutputDirectory;
    }

    @Override
    public void setReportOutputDirectory(File outputDirectory) {
        reportOutputDirectory = outputDirectory;
    }

    @Override
    public String getOutputName() {
        return getOutputPath();
    }

    @Override
    public String getOutputPath() {
        return "gapidocs/index";
    }

    @Override
    public String getName(Locale locale) {
        return "Groovydoc";
    }

    @Override
    public String getDescription(Locale locale) {
        return "Groovydoc API documentation for the main sources.";
    }

    @Override
    public String getCategoryName() {
        return CATEGORY_PROJECT_REPORTS;
    }

    @Override
    public boolean isExternalReport() {
        return true;
    }

    @Override
    public boolean canGenerateReport() {
        return !skipGroovyDoc && !getFiles(sources, groovyDocJavaSources).isEmpty();
    }

    @Override
    public void generate(Sink sink, Locale locale) throws MavenReportException {
        try {
            execute();
        } catch (MojoExecutionException e) {
            throw new MavenReportException("Unable to generate Groovydoc report.", e);
        }
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (!canGenerateReport()) {
            return;
        }
        File standaloneOutputDirectory = groovyDocOutputDirectory;
        try {
            groovyDocOutputDirectory = new File(reportOutputDirectory, "gapidocs");
            super.execute();
            File landingPage = new File(groovyDocOutputDirectory, "index.html");
            if (!landingPage.isFile()) {
                throw new MojoExecutionException("Groovydoc report did not generate " + landingPage
                        + ". Ensure Groovy supports Groovydoc generation and defaultDocTemplates includes index.html.");
            }
        } finally {
            groovyDocOutputDirectory = standaloneOutputDirectory;
        }
    }
}
