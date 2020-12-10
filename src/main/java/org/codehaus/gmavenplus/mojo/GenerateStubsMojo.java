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
 * Generates stubs for the main Groovy sources and adds them to Maven's sources for the Maven compiler plugin to find.
 * Note that this mojo requires Groovy &gt;= 1.8.2.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
@Mojo(name = "generateStubs", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true,
        configurator = "include-project-compile-dependencies")
public class GenerateStubsMojo extends AbstractGenerateStubsMojo {

    /**
     * The Groovy source files (relative paths).
     * Default: "${project.basedir}/src/main/groovy/&#42;&#42;/&#42;.groovy"
     */
    @Parameter
    protected FileSet[] sources;

    /**
     * The location for the compiled classes.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/groovy-stubs/main")
    protected File stubsOutputDirectory;

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs (causes a "BUILD ERROR" message to be displayed)
     */
    @Override
    public void execute() throws MojoExecutionException {
        minGroovyVersion = GROOVY_1_8_2;

        try {
            try {
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Project compile classpath:\n" + project.getCompileClasspathElements());
                }
            } catch (DependencyResolutionRequiredException e) {
                getLog().debug("Unable to log project compile classpath");
            }

            doStubGeneration(getFiles(sources, false), project.getCompileClasspathElements(), stubsOutputDirectory);
            logGeneratedStubs(stubsOutputDirectory);
            resetStubModifiedDates(getStubs(stubsOutputDirectory));

            // add stubs to project source so the Maven Compiler Plugin can find them
            project.addCompileSourceRoot(stubsOutputDirectory.getAbsolutePath());
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Unable to get a Groovy class from classpath (" + e.getMessage() + "). Do you have Groovy as a compile dependency in your project?", e);
        } catch (InvocationTargetException e) {
            throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
        } catch (InstantiationException e) {
            throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Compile dependencies weren't resolved.", e);
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to add project compile dependencies to classpath.", e);
        }
    }

}
