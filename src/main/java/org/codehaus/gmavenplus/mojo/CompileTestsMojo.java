package org.codehaus.gmavenplus.mojo;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.shared.model.fileset.FileSet;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;


/**
 * Compiles the test sources.
 * Note that this mojo requires Groovy &gt;= 1.5.0, and &gt;= 2.0.0-beta-3 (the indy version) for compiling with invokedynamic option.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
@Mojo(name = "compileTests", defaultPhase = LifecyclePhase.TEST_COMPILE, requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class CompileTestsMojo extends AbstractCompileMojo {

    /**
     * The Groovy test source files (relative paths).
     * Default: "${project.basedir}/src/test/groovy/&#42;&#42;/&#42;.groovy"
     */
    @Parameter
    protected FileSet[] testSources;

    /**
     * The location for the compiled test classes.
     */
    @Parameter(defaultValue = "${project.build.testOutputDirectory}")
    protected File testOutputDirectory;

    /**
     * Flag to allow test compilation to be skipped.
     */
    @Parameter(property = "maven.test.skip", defaultValue = "false")
    protected boolean skipTests;

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs (causes a "BUILD ERROR" message to be displayed)
     */
    @Override
    public void execute() throws MojoExecutionException {
        if (skipTests) {
            getLog().info("Compilation of tests is skipped.");
            return;
        }

        try {
            try {
                getLog().debug("Project test classpath:\n" + project.getTestClasspathElements());
            } catch (DependencyResolutionRequiredException e) {
                getLog().debug("Unable to log project test classpath");
            }
            doCompile(getTestFiles(testSources, false), project.getTestClasspathElements(), testOutputDirectory);
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
