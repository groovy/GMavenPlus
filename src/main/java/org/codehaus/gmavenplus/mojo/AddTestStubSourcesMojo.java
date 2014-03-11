/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Adds Groovy test stubs directory back to Maven's list of test source directories.
 * Normally, you won't need to use this mojo.
 *
 * @author Keegan Witt
 * @since 1.1
 *
 * @goal addTestStubSources
 * @threadSafe
 */
public class AddTestStubSourcesMojo extends AbstractGroovyStubSourcesMojo {

    /**
     * Executes this mojo.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws org.apache.maven.plugin.MojoFailureException If an expected problem (such as a compilation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (groovyVersionSupportsAction()) {
            logGroovyVersion("addTestStubsSources");

            project.addTestCompileSourceRoot(testStubsOutputDirectory.getAbsolutePath());
        } else {
            getLog().error("Your Groovy version (" + getGroovyVersion() + ") doesn't support stub sources.  The minimum version of Groovy required is " + minGroovyVersion + ".  Skipping adding stub sources.");
        }
    }

}
