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
import java.util.*;


/**
 * This mojo provides access to the Groovy sources.
 *
 * @author Keegan Witt
 */
public abstract class AbstractGroovySourcesMojo extends AbstractGroovyMojo {
    public static final String MAIN = "main";
    public static final String TEST = "test";

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
     * @param includeJavaSources Whether to include Java sources
     * @return The filesets of the the main sources.
     */
    protected FileSet[] getSourceRoots(final boolean includeJavaSources) {
        return getFilesets(sources, MAIN, includeJavaSources);
    }

    /**
     * Gets the filesets of the the main sources (not including Java sources).
     *
     * @return The filesets of the the main sources.
     */
    protected FileSet[] getSourceRoots() {
        return getFilesets(sources, MAIN, false);
    }

    /**
     * Gets the set of files of the the main sources.
     *
     * @param includeJavaSources Whether to include Java sources
     * @return The set of files of the the main sources.
     */
    protected Set<File> getSources(final boolean includeJavaSources) {
        return getFiles(sources, MAIN, includeJavaSources);
    }

    /**
     * Gets the set of files of the the main sources (not including Java sources).
     *
     * @return The set of files of the the main sources.
     */
    protected Set<File> getSources() {
        return getFiles(sources, MAIN, false);
    }

    /**
     * Gets the filesets of the test sources.
     *
     * @param includeJavaSources Whether to include Java sources
     * @return The filesets of the test sources.
     */
    protected FileSet[] getTestSourceRoots(final boolean includeJavaSources) {
        return getFilesets(testSources, TEST, includeJavaSources);
    }

    /**
     * Gets the filesets of the test sources (not including Java sources).
     *
     * @return The filesets of the test sources.
     */
    protected FileSet[] getTestSourceRoots() {
        return getFilesets(testSources, TEST, false);
    }

    /**
     * Gets the set of files of the test sources.
     *
     * @param includeJavaSources Whether to include Java sources
     * @return The set of files of the test sources.
     */
    protected Set<File> getTestSources(final boolean includeJavaSources) {
        return getFiles(testSources, TEST, includeJavaSources);
    }

    /**
     * Gets the set of files of the test sources (not including Java sources).
     *
     * @return The set of files of the test sources.
     */
    protected Set<File> getTestSources() {
        return getFiles(testSources, TEST, false);
    }

    /**
     * Gets the set of included files from the specified source files or source directory (if sources are null).
     *
     * @param fromSources The sources to get the included files from
     * @param defaultSourceDirectory The source directory to fall back on if sources are null
     * @param includeJavaSources Whether to include Java sources
     * @return The included files from the specified sources
     */
    protected Set<File> getFiles(final FileSet[] fromSources, final String defaultSourceDirectory, final boolean includeJavaSources) {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        for (FileSet fileSet : getFilesets(fromSources, defaultSourceDirectory, includeJavaSources)) {
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
     * @param defaultSubDirectory The source subdirectory to fall back on if sources are null
     * @param includeJavaSources Whether to include Java sources
     * @return The included filesets from the specified sources
     */
    protected FileSet[] getFilesets(final FileSet[] fromSources, final String defaultSubDirectory, boolean includeJavaSources) {
        if (fromSources != null) {
            return fromSources;
        } else {
            FileSet groovyFileSet = new FileSet();
            String groovyDirectory = "src" + File.separator + defaultSubDirectory + File.separator + "groovy";
            groovyFileSet.setDirectory(groovyDirectory);
            groovyFileSet.setIncludes(Arrays.asList(GROOVY_SOURCES_PATTERN));
            if (includeJavaSources) {
                List<FileSet> javaFileSets = new ArrayList<FileSet>();
                if (TEST.equals(defaultSubDirectory)) {
                    for (Object sourceRoot : project.getTestCompileSourceRoots()) {
                        FileSet javaFileSet = new FileSet();
                        javaFileSet.setDirectory((String) sourceRoot);
                        javaFileSet.setIncludes(Arrays.asList(JAVA_SOURCES_PATTERN));
                        javaFileSets.add(javaFileSet);
                    }
                } else {
                    for (Object sourceRoot : project.getCompileSourceRoots()) {
                        FileSet javaFileSet = new FileSet();
                        javaFileSet.setDirectory((String) sourceRoot);
                        javaFileSet.setIncludes(Arrays.asList(JAVA_SOURCES_PATTERN));
                        javaFileSets.add(javaFileSet);
                    }
                }
                FileSet[] groovyFileSets = new FileSet[] {groovyFileSet};
                FileSet[] javaFileSetsArr = javaFileSets.toArray(new FileSet[javaFileSets.size()]);
                FileSet[] result = Arrays.copyOf(groovyFileSets, groovyFileSets.length + javaFileSetsArr.length);
                System.arraycopy(javaFileSetsArr, 0, result, groovyFileSets.length, javaFileSetsArr.length);
                return result;
            } else {
                return new FileSet[] {groovyFileSet};
            }
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
