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

import java.lang.reflect.InvocationTargetException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Generates stubs for the main sources.
 * Note that this mojo requires Groovy >= 1.7.0.
 *
 * @author Keegan Witt
 *
 * @goal generateStubs
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @threadSafe
 */
public class GenerateStubsMojo extends AbstractGenerateStubsMojo {

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws MojoFailureException If an expected problem (such as a compilation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (groovyVersionSupportsAction()) {
            logGroovyVersion("generateStubs");

            try {
                doStubGeneration(getSources(), stubsOutputDirectory);
                resetStubModifiedDates(getStubs());

                // add stubs to project source so the Maven Compiler Plugin can find them
                project.addCompileSourceRoot(stubsOutputDirectory.getAbsolutePath());

                // log generated stubs
                int stubCount = getStubs().size();
                getLog().info("Generated " + stubCount + " stubs.");
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath.  Do you have Groovy as a compile dependency in your project?", e);
            } catch (InvocationTargetException e) {
                throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
            } catch (InstantiationException e) {
                throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
            }
        } else {
            getLog().error("Your Groovy version (" + getGroovyVersion() + ") doesn't support stub generation.  The minimum version of Groovy required is " + MIN_GROOVY_VERSION + ".  Skipping stub generation.");
        }
    }

}
