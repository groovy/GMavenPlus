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
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;


/*
 * The mojo doesn't match the goal name, but it was decided it was better
 * to avoid breaking existing builds than to correct the discrepancy
 * (http://jira.codehaus.org/browse/GMAVENPLUS-33)
 */

/**
 * Generates GroovyDoc for the test sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
@Mojo(name = "groovydocTests", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class GroovyDocTestsMojo extends AbstractGroovyDocMojo {

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws MojoFailureException If an expected problem (such as a compilation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            try {
                getLog().debug("Project test classpath:\n" + project.getCompileClasspathElements());
            } catch (DependencyResolutionRequiredException e) {
                getLog().warn("Unable to log project tset classpath", e);
            }
            doGroovyDocGeneration(getTestSourceRoots(groovyDocJavaSources), project.getTestClasspathElements(), testGroovyDocOutputDirectory);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Unable to get a Groovy class from classpath. Do you have Groovy as a compile dependency in your project?", e);
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
