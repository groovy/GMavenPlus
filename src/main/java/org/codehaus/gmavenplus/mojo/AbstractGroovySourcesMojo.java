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

import static java.util.Collections.singletonList;


/**
 * This mojo provides access to the Groovy sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-2
 */
public abstract class AbstractGroovySourcesMojo extends AbstractGroovyMojo {

    /**
     * Main source directory name.
     */
    protected static final String MAIN = "main";

    /**
     * Test source directory name.
     */
    protected static final String TEST = "test";

    /**
     * Gets the set of included files from the specified source files or source directory (if sources are null).
     *
     * @param fromSources The sources to get the included files from
     * @param includeJavaSources Whether to include Java sources
     * @return The included files from the specified sources
     */
    protected Set<File> getFiles(final FileSet[] fromSources, final boolean includeJavaSources) {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        for (FileSet fileSet : getFilesets(fromSources, includeJavaSources)) {
            for (String include : fileSetManager.getIncludedFiles(fileSet)) {
                files.add(new File(fileSet.getDirectory(), include));
            }
        }

        return files;
    }

    /**
     * Gets the set of included files from the specified source files or source directory (if sources are null).
     *
     * @param fromSources The sources to get the included files from
     * @param includeJavaSources Whether to include Java sources
     * @return The included files from the specified sources
     */
    protected Set<File> getTestFiles(final FileSet[] fromSources, final boolean includeJavaSources) {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        for (FileSet fileSet : getTestFilesets(fromSources, includeJavaSources)) {
            for (String include : fileSetManager.getIncludedFiles(fileSet)) {
                files.add(new File(fileSet.getDirectory(), include));
            }
        }

        return files;
    }

    /**
     * Gets the set of included filesets from the specified source files or source directory (if sources are null).
     *
     * @param fromSources The sources to get the included files from
     * @param includeJavaSources Whether to include Java sources
     * @return The included filesets from the specified sources
     */
    protected FileSet[] getFilesets(final FileSet[] fromSources, final boolean includeJavaSources) {
        FileSet[] result;
        FileSet[] groovyFileSets;

        if (fromSources != null) {
            groovyFileSets = fromSources;
        } else {
            FileSet groovyFileSet = new FileSet();
            String groovyDirectory = "src" + File.separator + MAIN + File.separator + "groovy";
            groovyFileSet.setDirectory(project.getBasedir() + File.separator + groovyDirectory);
            groovyFileSet.setIncludes(singletonList(GROOVY_SOURCES_PATTERN));
            groovyFileSets = new FileSet[] {groovyFileSet};
        }

        if (includeJavaSources) {
            List<FileSet> javaFileSets = new ArrayList<FileSet>();
            for (Object sourceRoot : project.getCompileSourceRoots()) {
                FileSet javaFileSet = new FileSet();
                javaFileSet.setDirectory((String) sourceRoot);
                javaFileSet.setIncludes(singletonList(JAVA_SOURCES_PATTERN));
                javaFileSets.add(javaFileSet);
            }
            FileSet[] javaFileSetsArr = javaFileSets.toArray(new FileSet[0]);
            result = Arrays.copyOf(groovyFileSets, groovyFileSets.length + javaFileSetsArr.length);
            System.arraycopy(javaFileSetsArr, 0, result, groovyFileSets.length, javaFileSetsArr.length);
        } else {
            result = groovyFileSets;
        }

        return result;
    }

    /**
     * Gets the set of included filesets from the specified source files or source directory (if sources are null).
     *
     * @param fromSources The sources to get the included files from
     * @param includeJavaSources Whether to include Java sources
     * @return The included filesets from the specified sources
     */
    protected FileSet[] getTestFilesets(final FileSet[] fromSources, final boolean includeJavaSources) {
        FileSet[] result;
        FileSet[] groovyFileSets;

        if (fromSources != null) {
            groovyFileSets = fromSources;
        } else {
            FileSet groovyFileSet = new FileSet();
            String groovyDirectory = "src" + File.separator + TEST + File.separator + "groovy";
            groovyFileSet.setDirectory(project.getBasedir() + File.separator + groovyDirectory);
            groovyFileSet.setIncludes(singletonList(GROOVY_SOURCES_PATTERN));
            groovyFileSets = new FileSet[] {groovyFileSet};
        }

        if (includeJavaSources) {
            List<FileSet> javaFileSets = new ArrayList<FileSet>();
            for (Object sourceRoot : project.getTestCompileSourceRoots()) {
                FileSet javaFileSet = new FileSet();
                javaFileSet.setDirectory((String) sourceRoot);
                javaFileSet.setIncludes(singletonList(JAVA_SOURCES_PATTERN));
                javaFileSets.add(javaFileSet);
            }
            FileSet[] javaFileSetsArr = javaFileSets.toArray(new FileSet[0]);
            result = Arrays.copyOf(groovyFileSets, groovyFileSets.length + javaFileSetsArr.length);
            System.arraycopy(javaFileSetsArr, 0, result, groovyFileSets.length, javaFileSetsArr.length);
        } else {
            result = groovyFileSets;
        }

        return result;
    }
}
