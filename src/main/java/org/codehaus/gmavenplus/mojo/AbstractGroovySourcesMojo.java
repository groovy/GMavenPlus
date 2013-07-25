/*
 * Copyright 2013 Keegan Witt
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * This mojo provides access to the Groovy sources.
 *
 * @author Keegan Witt
 */
public abstract class AbstractGroovySourcesMojo extends AbstractGroovyMojo {

    /**
     * The Groovy source files (relative paths).
     * Default: "${project.basedir}/src/main/groovy/&#42;&#42;/&#42;.groovy"
     *
     * @parameter
     */
    private FileSet[] sources;

    /**
     * The Groovy test source files (relative paths).
     * Default: "${project.basedir}/src/test/groovy/&#42;&#42;/&#42;.groovy"
     *
     * @parameter
     */
    private FileSet[] testSources;

    /**
     * Gets the filesets of the the main sources.
     *
     * @return The filesets of the the main sources.
     */
    protected FileSet[] getSourceRoots() {
        return getFileset(sources, "main");
    }

    /**
     * Gets the set of files of the the main sources.
     *
     * @return The set of files of the the main sources.
     */
    protected Set<File> getSources() {
        return getFiles(sources, "main");
    }

    /**
     * Gets the filesets of the test sources.
     *
     * @return The filesets of the test sources.
     */
    protected FileSet[] getTestSourceRoots() {
        return getFileset(testSources, "test");
    }

    /**
     * Gets the set of files of the test sources.
     *
     * @return The set of files of the test sources.
     */
    protected Set<File> getTestSources() {
        return getFiles(testSources, "test");
    }

    /**
     * Gets the set of included files from the specified source files or source directory (if sources are null).
     *
     * @param fromSources The sources to get the included files from
     * @param defaultSourceDirectory The source directory to fall back on if sources are null
     * @return The included files from the specified sources.
     */
    protected Set<File> getFiles(final FileSet[] fromSources, final String defaultSourceDirectory) {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        for (FileSet fileSet : getFileset(fromSources, defaultSourceDirectory)) {
            for (String include : Arrays.asList(fileSetManager.getIncludedFiles(fileSet))) {
                files.add(new File(fileSet.getDirectory(), include));
            }
        }

        return files;
    }

    /**
     * Gets the set of included filesets from the specified source files or source directory (if sources are null).
     *
     * @param fromSources The sources to get the included files from
     * @param defaultSourceDirectory The source directory to fall back on if sources are null
     * @return The included filesets from the specified sources.
     */
    protected FileSet[] getFileset(final FileSet[] fromSources, final String defaultSourceDirectory) {
        if (fromSources != null) {
            return fromSources;
        } else {
            FileSet fileSet = new FileSet();
            String directory = "src" + File.separator + defaultSourceDirectory + File.separator + "groovy";
            fileSet.setDirectory(directory);
            fileSet.setIncludes(Arrays.asList(DEFAULT_SOURCE_PATTERN));
            return new FileSet[] {fileSet};
        }
    }

    /**
     * Sets the sources to specified.
     *
     * @param newSources the sources to set
     */
    public void setSources(final FileSet[] newSources) {
        this.sources = newSources;
    }

    /**
     * Sets the test sources to specified.
     *
     * @param newTestSources the test sources to set
     */
    public void setTestSources(final FileSet[] newTestSources) {
        this.testSources = newTestSources;
    }

}
