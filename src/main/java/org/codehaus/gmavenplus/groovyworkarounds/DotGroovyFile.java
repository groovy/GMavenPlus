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

import com.google.common.io.Files;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;


/**
 * This class exists solely to trick
 * <a href="http://groovy.codehaus.org/api/org/codehaus/groovy/tools/javac/JavaStubCompilationUnit.html#addSource%28java.io.File%29">JavaStubCompilationUnit.addSource(java.io.File)</a>
 * into letting us use files that don't end in ".groovy" (as a workaround for <a href="http://jira.codehaus.org/browse/GROOVY-5021">GROOVY-5021</a>).
 *
 * @author Keegan Witt
 */
public class DotGroovyFile extends File {
    private Set<String> scriptExtensions = new HashSet<String>();

    /**
     * Constructs a new DotGroovyFile object with the specified parameters.
     *
     * @param pathname Pathname to use to create DotGroovyFile
     */
    public DotGroovyFile(String pathname) {
        super(pathname);
    }

    /**
     * Constructs a new DotGroovyFile object with the specified parameters.
     *
     * @param parent Parent pathname to use to create DotGroovyFile
     * @param child Child pathname to use to create DotGroovyFile
     */
    public DotGroovyFile(String parent, String child) {
        super(parent, child);
    }

    /**
     * Constructs a new DotGroovyFile object with the specified parameters.
     *
     * @param parent Parent file to use to create DotGroovyFile
     * @param child Child pathname to use to create DotGroovyFile
     */
    public DotGroovyFile(File parent, String child) {
        super(parent, child);
    }

    /**
     * Constructs a new DotGroovyFile object with the specified parameters.
     *
     * @param uri URI to use to create DotGroovyFile
     */
    public DotGroovyFile(URI uri) {
        super(uri);
    }

    /**
     * A convenience constructor to turn a regular file into a DotGroovyFile.
     *
     * @param file File to use to create DotGroovyFile
     */
    public DotGroovyFile(File file) {
        super(file.getAbsolutePath());
    }

    /**
     * Default Groovy file extensions (currently '.groovy', '.gvy', '.gy', and '.gsh').
     *
     * @return The default Groovy file extensions
     */
    public static Set<String> defaultScriptExtensions() {
        Set<String> defaultScriptExtensions = new HashSet<String>();

        defaultScriptExtensions.add("groovy");
        defaultScriptExtensions.add("gvy");
        defaultScriptExtensions.add("gy");
        defaultScriptExtensions.add("gsh");

        return defaultScriptExtensions;
    }

    /**
     * A method to lie about the file extension and say it is ".groovy".
     *
     * @return Filename with forced <tt>.groovy</tt> extension
     */
    public String getName() {
        if (scriptExtensions != null && !scriptExtensions.isEmpty() && scriptExtensions.contains(Files.getFileExtension(super.getAbsolutePath()))) {
            return Files.getNameWithoutExtension(super.getName()) + ".groovy";
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
    public DotGroovyFile setScriptExtensions(Set<String> newScriptExtensions) {
        this.scriptExtensions = newScriptExtensions;
        return this;
    }

}
