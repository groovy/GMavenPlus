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

package gmavenplus.mojo;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * This mojo adds Groovy sources to the project's sources
 *
 * @phase initialize
 * @goal addSource
 * @executionStrategy always
 * @requiresDirectInvocation false
 *
 * @author Keegan Witt
 */
public class AddSourceMojo extends AbstractCompileMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        logGroovyVersion("addSource");

        FileSetManager fileSetManager = new FileSetManager(getLog());
        for (FileSet source : sources) {
            for (String include : Arrays.asList(fileSetManager.getIncludedFiles(source))) {
                addSourcePath(project.getBasedir().getAbsolutePath() + File.separator + source.getDirectory() + File.separator + include);
            }
        }
        for (FileSet testSource : testSources) {
            for (String include : Arrays.asList(fileSetManager.getIncludedFiles(testSource))) {
                addTestSourcePath(project.getBasedir().getAbsolutePath() + File.separator + testSource.getDirectory() + File.separator + include);
            }
        }
    }

     /**
     * @return
     * @throws DependencyResolutionRequiredException
     */
    @SuppressWarnings("unchecked")
    protected List getProjectClasspathElements() throws DependencyResolutionRequiredException {
        throw new UnsupportedOperationException("This method does not apply for this mojo");
    }

    /**
     * @return
     */
    protected List<File> getJavaSources() {
        throw new UnsupportedOperationException("This method does not apply for this mojo");
    }

    protected void addSourcePath(String path) {
        if (!project.getCompileSourceRoots().contains(path)) {
            getLog().debug("Added Source directory: " + path);
            project.addCompileSourceRoot(path);
        }
    }

    protected void addTestSourcePath(String path) {
        if (!project.getTestCompileSourceRoots().contains(path)) {
            getLog().debug("Added Test Source directory: " + path);
            project.addTestCompileSourceRoot(path);
        }
    }

}
