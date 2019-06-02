/*
 * Copyright (C) 2011 the original author or authors.
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

package org.codehaus.gmavenplus.groovyworkarounds;

import org.codehaus.gmavenplus.util.FileUtils;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;


/**
 * This class exists solely to trick
 * <a href="http://docs.groovy-lang.org/docs/latest/html/api/org/codehaus/groovy/tools/javac/JavaStubCompilationUnit.html#addSource%28java.io.File%29">JavaStubCompilationUnit.addSource(java.io.File)</a>
 * into letting us use files that don't end in ".groovy" (as a workaround for <a href="http://jira.codehaus.org/browse/GROOVY-5021">GROOVY-5021</a>).
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public class DotGroovyFile extends File {

    private static final long serialVersionUID = -3325908173654793431L;

    /**
     * The file extensions to consider as Groovy files.
     */
    private Set<String> scriptExtensions = new HashSet<String>();

    /**
     * Constructs a new DotGroovyFile object with the specified parameters.
     *
     * @param pathname Pathname to use to create DotGroovyFile
     */
    public DotGroovyFile(final String pathname) {
        super(pathname);
    }

    /**
     * Constructs a new DotGroovyFile object with the specified parameters.
     *
     * @param parent Parent pathname to use to create DotGroovyFile
     * @param child Child pathname to use to create DotGroovyFile
     */
    public DotGroovyFile(final String parent, final String child) {
        super(parent, child);
    }

    /**
     * Constructs a new DotGroovyFile object with the specified parameters.
     *
     * @param parent Parent file to use to create DotGroovyFile
     * @param child Child pathname to use to create DotGroovyFile
     */
    public DotGroovyFile(final File parent, final String child) {
        super(parent, child);
    }

    /**
     * Constructs a new DotGroovyFile object with the specified parameters.
     *
     * @param uri URI to use to create DotGroovyFile
     */
    public DotGroovyFile(final URI uri) {
        super(uri);
    }

    /**
     * A convenience constructor to turn a regular file into a DotGroovyFile.
     *
     * @param file File to use to create DotGroovyFile
     */
    public DotGroovyFile(final File file) {
        super(file.getAbsolutePath());
    }

    /**
     * A method to lie about the file extension and say it is ".groovy".
     *
     * @return Filename with forced <i>.groovy</i> extension
     */
    @Override
    public String getName() {
        if (scriptExtensions != null && !scriptExtensions.isEmpty() && scriptExtensions.contains(FileUtils.getFileExtension(super.getAbsolutePath()))) {
            return FileUtils.getNameWithoutExtension(super.getName()) + ".groovy";
        } else {
            return super.getName();
        }
    }

    /**
     * Gets the script extensions for this Groovy file.
     *
     * @return The script extensions for this Groovy file
     */
    public Set<String> getScriptExtensions() {
        return scriptExtensions;
    }

    /**
     * Sets the script extensions for this Groovy file.
     *
     * @param newScriptExtensions The script extensions to set on this Groovy file
     * @return This object (for fluent invocation)
     */
    public DotGroovyFile setScriptExtensions(final Set<String> newScriptExtensions) {
        scriptExtensions = newScriptExtensions;
        return this;
    }

}
