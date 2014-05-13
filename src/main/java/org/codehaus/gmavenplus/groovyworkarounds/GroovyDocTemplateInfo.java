/*
 * Copyright 2003-2010 the original author or authors.
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

import org.codehaus.gmavenplus.model.Version;


/**
 * This class was taken mostly from the Groovy project
 * (<a href="https://github.com/groovy/groovy-core/blob/master/subprojects/groovy-groovydoc/src/main/java/org/codehaus/groovy/tools/groovydoc/gstringTemplates/GroovyDocTemplateInfo.java">GroovyDocTemplateInfo.java</a> and <a href="https://github.com/groovy/groovy-core/blob/master/subprojects/groovy-groovydoc/src/main/java/org/codehaus/groovy/groovydoc/GroovyDoc.java">GroovyDoc.java</a>)
 * because it wasn't available prior to Groovy 1.7.
 */
public class GroovyDocTemplateInfo {
    private static final String DOCGEN_BASEDIR = "org/codehaus/groovy/tools/";
    private static final Version GROOVY_1_6_2 = new Version(1, 6, 2);
    private static final Version GROOVY_1_6_0 = new Version(1, 6, 0);
    private static final Version GROOVY_1_6_0_RC2 = new Version(1, 6, 0, "RC-2");
    private Version groovyVersion;
    private String templateBaseDir;

    public GroovyDocTemplateInfo(Version version) {
        groovyVersion = version;
        if (groovyVersion.compareTo(GROOVY_1_6_2) >= 0) {
            templateBaseDir = "org/codehaus/groovy/tools/groovydoc/gstringTemplates/";
        } else {
            templateBaseDir = "org/codehaus/groovy/tools/groovydoc/gstring-templates/";
        }
    }

    public String[] defaultDocTemplates() {
        if (groovyVersion.compareTo(GROOVY_1_6_2) >= 0) {
            return new String[] {
                    templateBaseDir + "topLevel/index.html",
                    templateBaseDir + "topLevel/overview-frame.html",
                    templateBaseDir + "topLevel/allclasses-frame.html",
                    templateBaseDir + "topLevel/overview-summary.html",
                    templateBaseDir + "topLevel/help-doc.html",
                    templateBaseDir + "topLevel/index-all.html",
                    templateBaseDir + "topLevel/deprecated-list.html",
                    templateBaseDir + "topLevel/stylesheet.css",
                    templateBaseDir + "topLevel/inherit.gif",
                    DOCGEN_BASEDIR + "groovy.ico"
            };
        } else if (groovyVersion.compareTo(GROOVY_1_6_0) >= 0) {
            return new String[] {
                    templateBaseDir + "top-level/index.html",
                    templateBaseDir + "top-level/overview-frame.html",
                    templateBaseDir + "top-level/allclasses-frame.html",
                    templateBaseDir + "top-level/overview-summary.html",
                    templateBaseDir + "top-level/help-doc.html",
                    templateBaseDir + "top-level/index-all.html",
                    templateBaseDir + "top-level/deprecated-list.html",
                    templateBaseDir + "top-level/stylesheet.css",
                    templateBaseDir + "top-level/inherit.gif"
            };
        } else if (groovyVersion.compareTo(GROOVY_1_6_0_RC2) >= 0) {
            return new String[] {
                    templateBaseDir + "top-level/index.html",
                    templateBaseDir + "top-level/overview-frame.html",
                    templateBaseDir + "top-level/allclasses-frame.html",
                    templateBaseDir + "top-level/overview-summary.html",
                    templateBaseDir + "top-level/stylesheet.css",
                    templateBaseDir + "top-level/inherit.gif"
            };
        } else {
            return new String[] {
                    templateBaseDir + "top-level/index.html",
                    templateBaseDir + "top-level/overview-frame.html",
                    templateBaseDir + "top-level/allclasses-frame.html",
                    templateBaseDir + "top-level/overview-summary.html",
                    templateBaseDir + "top-level/stylesheet.css"
            };
        }
    }

    public String[] defaultPackageTemplates() {
        if (groovyVersion.compareTo(GROOVY_1_6_2) >= 0) {
            return new String[] {
                    templateBaseDir + "packageLevel/package-frame.html",
                    templateBaseDir + "packageLevel/package-summary.html"
            };
        } else {
            return new String[] {
                    templateBaseDir + "package-level/package-frame.html",
                    templateBaseDir + "package-level/package-summary.html"
            };
        }
    }

    public String[] defaultClassTemplates() {
        if (groovyVersion.compareTo(GROOVY_1_6_2) >= 0) {
            return new String[] {
                    templateBaseDir + "classLevel/classDocName.html"
            };
        } else {
            return new String[] {
                    templateBaseDir + "class-level/classDocName.html"
            };
        }
    }

}
