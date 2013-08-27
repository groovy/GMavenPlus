/*
 * Copyright (C) 2012 the original author or authors.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;


/**
 * This mojo removes Groovy stubs from the project's sources.
 *
 * @author Keegan Witt
 *
 * @phase initialize
 * @goal removeStubs
 * @executionStrategy always
 * @requiresDirectInvocation false
 * @threadSafe
 */
public class RemoveStubsMojo extends AbstractGroovyStubSourcesMojo {

    /**
     * Executes this mojo.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws org.apache.maven.plugin.MojoFailureException If an expected problem (such as a compilation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        logGroovyVersion("removeStubs");

        for (File stub : getStubs()) {
            removeSourcePath(stub.getAbsolutePath());
        }
    }

    /**
     * Removes the specified source path from the project's main compile sources.
     *
     * @param path The source path to remove from the project's main compile sources
     */
    protected void removeSourcePath(final String path) {
        if (project.getCompileSourceRoots().contains(path)) {
            getLog().debug("Removed source directory: " + path);
            project.getCompileSourceRoots().remove(path);
        }
    }

}
