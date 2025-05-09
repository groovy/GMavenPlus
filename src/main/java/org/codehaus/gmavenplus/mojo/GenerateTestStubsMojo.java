/*
 * Copyright (C) 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * Generates stubs for the test Groovy sources and adds them to Maven's test sources for the Maven compiler plugin to find.
 * Note that this mojo requires Groovy &gt;= 1.8.2.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
@Mojo(name = "generateTestStubs", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class GenerateTestStubsMojo extends AbstractGenerateStubsMojo {

    /**
     * The Groovy test source files (relative paths).
     * Default: "${project.basedir}/src/test/groovy/&#42;&#42;/&#42;.groovy"
     */
    @Parameter
    protected FileSet[] testSources;

    /**
     * The location for the compiled test classes.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/groovy-stubs/test")
    protected File testStubsOutputDirectory;

    /**
     * Flag to allow test stub generation to be skipped.
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
            getLog().info("Generation of test stubs is skipped.");
            return;
        }

        minGroovyVersion = GROOVY_1_8_2;
        try {
            try {
                getLog().debug("Project test classpath:\n" + project.getTestClasspathElements());
            } catch (DependencyResolutionRequiredException e) {
                getLog().debug("Unable to log project test classpath");
            }

            doStubGeneration(getTestFiles(testSources, false), project.getTestClasspathElements(), testStubsOutputDirectory);
            logGeneratedStubs(testStubsOutputDirectory);
            resetStubModifiedDates(getStubs(testStubsOutputDirectory));

            // add stubs to project source so the Maven Compiler Plugin can find them
            project.addTestCompileSourceRoot(testStubsOutputDirectory.getAbsolutePath());
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
