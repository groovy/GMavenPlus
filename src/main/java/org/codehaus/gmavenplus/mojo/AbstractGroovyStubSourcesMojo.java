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
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;


/**
 * This mojo provides access to the Groovy sources (including stubs).
 *
 * @author Keegan Witt
 * @since 1.0-beta-3
 */
public abstract class AbstractGroovyStubSourcesMojo extends AbstractGroovySourcesMojo {

    /**
     * Gets the set of stub files in specified directory.
     * @param outputDirectory the directory to write stubs to
     *
     * @return The set of stub files in specified directory
     */
    protected Set<File> getStubs(File outputDirectory) {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        FileSet fileSet = new FileSet();
        fileSet.setDirectory(outputDirectory.getAbsolutePath());
        fileSet.setIncludes(singletonList(JAVA_SOURCES_PATTERN));
        for (String file : fileSetManager.getIncludedFiles(fileSet)) {
            files.add(new File(outputDirectory, file));
        }

        return files;
    }

}
