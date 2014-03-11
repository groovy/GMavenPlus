/*
 * Copyright 2013 the original author or authors.
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

import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.util.*;


/**
 * This mojo provides access to the Groovy sources (including stubs).
 *
 * @author Keegan Witt
 * @since 1.0-beta-3
 */
public abstract class AbstractGroovyStubSourcesMojo extends AbstractGroovySourcesMojo {
    /**
     * The location for the compiled classes.
     *
     * @parameter default-value="${project.build.directory}/generated-sources/groovy-stubs/main"
     */
    protected File stubsOutputDirectory;

    /**
     * The location for the compiled test classes.
     *
     * @parameter default-value="${project.build.directory}/generated-sources/groovy-stubs/test"
     */
    protected File testStubsOutputDirectory;

    /**
     * Gets the set of files of the main stubs.
     *
     * @return The set of files of the main stubs
     */
    protected Set<File> getStubs() {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        FileSet fileSet = new FileSet();
        fileSet.setDirectory(stubsOutputDirectory.getAbsolutePath());
        fileSet.setIncludes(Arrays.asList(JAVA_SOURCES_PATTERN));
        for (String file : fileSetManager.getIncludedFiles(fileSet)) {
            files.add(new File(stubsOutputDirectory, file));
        }

        return files;
    }

    /**
     * Gets the set of files of the test stubs.
     *
     * @return The set of files of the test stubs
     */
    protected Set<File> getTestStubs() {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        FileSet fileSet = new FileSet();
        fileSet.setDirectory(testStubsOutputDirectory.getAbsolutePath());
        fileSet.setIncludes(Arrays.asList(JAVA_SOURCES_PATTERN));
        for (String file : fileSetManager.getIncludedFiles(fileSet)) {
            files.add(new File(testStubsOutputDirectory, file));
        }

        return files;
    }

}
