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
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.util.Arrays;


/**
 * This mojo adds Groovy sources to the project's sources.
 *
 * @author Keegan Witt
 *
 * @phase initialize
 * @goal addSource
 * @executionStrategy always
 * @requiresDirectInvocation false
 * @configurator include-project-test-dependencies
 * @requiresDependencyResolution test
 * @threadSafe
 */
public class AddSourceMojo extends AbstractGroovySourcesMojo {

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws MojoFailureException If an expected problem (such as a compilation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        logGroovyVersion("addSource");

        FileSetManager fileSetManager = new FileSetManager(getLog());
        for (FileSet source : getSourceRoots()) {
            for (String include : Arrays.asList(fileSetManager.getIncludedFiles(source))) {
                addSourcePath(project.getBasedir().getAbsolutePath() + File.separator + source.getDirectory() + File.separator + include);
            }
        }
        for (FileSet testSource : getTestSourceRoots()) {
            for (String include : Arrays.asList(fileSetManager.getIncludedFiles(testSource))) {
                addTestSourcePath(project.getBasedir().getAbsolutePath() + File.separator + testSource.getDirectory() + File.separator + include);
            }
        }
    }

    /**
     * Adds the specified source path to the project's main compile sources.
     *
     * @param path The source path to add to the project's main compile sources
     */
    protected void addSourcePath(final String path) {
        if (!project.getCompileSourceRoots().contains(path)) {
            getLog().debug("Added Source directory: " + path);
            project.addCompileSourceRoot(path);
        }
    }

    /**
     * Adds the specified source path to the project's test compile sources.
     *
     * @param path The source path to add to the project's test compile sources
     */
    protected void addTestSourcePath(final String path) {
        if (!project.getTestCompileSourceRoots().contains(path)) {
            getLog().debug("Added Test Source directory: " + path);
            project.addTestCompileSourceRoot(path);
        }
    }

}
