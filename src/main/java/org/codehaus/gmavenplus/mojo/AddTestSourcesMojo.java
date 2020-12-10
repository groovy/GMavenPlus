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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;


/**
 * This mojo adds Groovy test sources to the project's test sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-3
 */
@Mojo(name = "addTestSources", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class AddTestSourcesMojo extends AbstractGroovySourcesMojo {

    /**
     * The Groovy test source files (relative paths).
     * Default: "${project.basedir}/src/test/groovy/&#42;&#42;/&#42;.groovy"
     */
    @Parameter
    protected FileSet[] testSources;

    /**
     * Executes this mojo.
     */
    @Override
    public void execute() {
        for (FileSet testSource : getTestFilesets(testSources, false)) {
            addTestSourcePath(testSource.getDirectory());
        }
    }

    /**
     * Adds the specified test source path to the project's test compile sources.
     *
     * @param path The test source path to add to the project's test compile sources
     */
    protected void addTestSourcePath(final String path) {
        if (!project.getTestCompileSourceRoots().contains(path)) {
            if (getLog().isDebugEnabled()) {
                getLog().debug("Added test source directory: " + path);
            }
            project.addTestCompileSourceRoot(path);
        }
    }

}
