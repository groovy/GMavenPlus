package org.codehaus.gmavenplus.mojo;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.shared.model.fileset.FileSet;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;


/**
 * Generates GroovyDoc for the test sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
@Mojo(name = "groovydocTests", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class GroovyDocTestsMojo extends AbstractGroovyDocMojo {

    /**
     * The Groovy test source files (relative paths).
     * Default: "${project.basedir}/src/test/groovy/&#42;&#42;/&#42;.groovy"
     */
    @Parameter
    protected FileSet[] testSources;

    /**
     * The location for the generated test API docs.
     */
    @Parameter(defaultValue = "${project.build.directory}/testgapidocs")
    protected File testGroovyDocOutputDirectory;

    /**
     * Whether to include test Java sources in GroovyDoc generation.
     *
     * @since 1.7
     */
    @Parameter(defaultValue = "true")
    protected boolean testGroovyDocJavaSources;

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs (causes a "BUILD ERROR" message to be displayed)
     */
    @Override
    public void execute() throws MojoExecutionException {
        try {
            try {
                getLog().debug("Project test classpath:\n" + project.getCompileClasspathElements());
            } catch (DependencyResolutionRequiredException e) {
                getLog().debug("Unable to log project tset classpath");
            }
            doGroovyDocGeneration(getTestFilesets(testSources, testGroovyDocJavaSources), project.getTestClasspathElements(), testGroovyDocOutputDirectory);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Unable to get a Groovy class from classpath (" + e.getMessage() + "). Do you have Groovy as a compile dependency in your project?", e);
        } catch (InvocationTargetException e) {
            throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
        } catch (InstantiationException e) {
            throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Test dependencies weren't resolved.", e);
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to add project test dependencies to classpath.", e);
        }
    }

}
