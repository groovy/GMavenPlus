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

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;


/*
 * The mojo doesn't match the goal name, but it was decided it was better
 * to avoid breaking existing builds than to correct the discrepancy
 * (http://jira.codehaus.org/browse/GMAVENPLUS-33)
 */

/**
 * Compiles the test sources.
 * Note that this mojo requires Groovy >= 1.5.0, and >= 2.0.0-beta-3 (the indy
 * version) for compiling with invokedynamic option.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 *
 * @phase test-compile
 * @goal testCompile
 * @requiresDependencyResolution test
 * @threadSafe
 */
public class CompileTestsMojo extends AbstractCompileMojo {

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws MojoFailureException If an expected problem (such as a compilation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!skipTests) {
            try {
                doCompile(getTestSources(), project.getTestClasspathElements(), testOutputDirectory);
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath.  Do you have Groovy as a compile dependency in your project?", e);
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
        } else {
            getLog().info("Skipping compilation of tests because ${maven.test.skip} was set to true.");
        }
    }

}
