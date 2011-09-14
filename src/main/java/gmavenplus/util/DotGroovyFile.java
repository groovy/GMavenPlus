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

package gmavenplus.util;

import java.io.File;
import java.net.URI;


/**
 * This class exists solely to trick <a href="http://groovy.codehaus.org/api/org/codehaus/groovy/tools/javac/JavaStubCompilationUnit.html#addSource%28java.io.File%29">JavaStubCompilationUnit.addSource(java.io.File)</a>
 * into letting us use files that don't end in ".groovy"
 *
 * @author Keegan Witt
 */
public class DotGroovyFile extends File {
    // TODO: make this class unnecessary?

    /**
     * @param pathname
     */
    public DotGroovyFile(String pathname) {
        super(pathname);
    }

    /**
     * @param parent
     * @param child
     */
    public DotGroovyFile(String parent, String child) {
        super(parent, child);
    }

    /**
     * @param parent
     * @param child
     */
    public DotGroovyFile(File parent, String child) {
        super(parent, child);
    }

    /**
     * @param uri
     */
    public DotGroovyFile(URI uri) {
        super(uri);
    }

    /**
     * A convenience constructor to turn a regular file into a DotGroovyFile
     *
     * @param file
     */
    public DotGroovyFile(File file) {
        this(file.getAbsolutePath());
    }

    /**
     * A method to lie about the file extension and say it is ".groovy" as long
     * as the original extension was now ".java"
     *
     * @return
     */
    @Override
    public String getName() {
        if (!super.getName().toLowerCase().endsWith(".java")) {
            return super.getName().replaceAll("\\.[A-z]+$", ".groovy");
        } else {
            return super.getName();
        }
    }

}
