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

package org.codehaus.gmavenplus.mojo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;

import java.io.File;
import java.util.List;


/**
 * The base mojo class, which all other mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractGroovyMojo extends AbstractMojo {

    /**
     * The pattern defining Groovy files.
     */
    protected static final String GROOVY_SOURCES_PATTERN = "**" + File.separator + "*.groovy";

    /**
     * The pattern defining Java stub files.
     */
    protected static final String JAVA_SOURCES_PATTERN = "**" + File.separator + "*.java";

    /**
     * Java 1.7 version.
     */
    protected static final Version JAVA_1_7 = new Version(1, 7);

    /**
     * Java 1.8 version.
     */
    protected static final Version JAVA_1_8 = new Version(1, 8);

    /**
     * Groovy 1.5.0 version.
     */
    protected static final Version GROOVY_1_5_0 = new Version(1, 5, 0);

    /**
     * The wrangler to use to work with Groovy classes, classpaths, classLoaders, and versions.
     */
    protected ClassWrangler classWrangler;

    // note that all supported parameter expressions can be found here: https://git-wip-us.apache.org/repos/asf?p=maven.git;a=blob;f=maven-core/src/main/java/org/apache/maven/plugin/PluginParameterExpressionEvaluator.java;hb=HEAD

    /**
     * The Maven project this plugin is being used on.
     *
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The Maven Session this plugin is being used on.
     *
     * @parameter property="session"
     * @required
     * @readonly
     */
    protected MavenSession session;

    /**
     * The plugin dependencies.
     *
     * @parameter property="plugin.artifacts"
     * @required
     * @readonly
     */
    protected List<Artifact> pluginArtifacts;

    /**
     * The plugin's mojo execution.
     *
     * @parameter property="mojoExecution"
     * @required
     * @readonly
     */
    protected MojoExecution mojoExecution;

    /**
     * The minimum version of Groovy that this mojo supports (1.5.0 by
     * default, but other mojos can override).
     */
    protected Version minGroovyVersion = GROOVY_1_5_0;

    /**
     * Flag to allow test compilation to be skipped.
     *
     * @parameter property="maven.test.skip" default-value="false"
     */
    protected boolean skipTests;

    /**
     * Logs the plugin classpath.
     */
    protected void logPluginClasspath() {
        if (getLog().isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (Artifact artifact : pluginArtifacts) {
                sb.append(artifact.getFile());
            }
            getLog().debug("Plugin classpath:\n" + sb.toString());
        }
    }

    /**
     * Determines whether the version of Java executing this mojo supports invokedynamic (is at least 1.7).
     *
     * @return <code>true</code> if the running Java supports invokedynamic, <code>false</code> otherwise
     */
    protected boolean isJavaSupportIndy() {
        return getJavaVersion().compareTo(JAVA_1_7, false) >= 0;
    }

    /**
     * Determines whether the version of Java executing this mojo supports JEP 118 (is at least 1.8).
     *
     * @return <code>true</code> if the running Java supports parameters, <code>false</code> otherwise
     */
    protected boolean isJavaSupportParameters() {
        return getJavaVersion().compareTo(JAVA_1_8, false) >= 0;
    }

    /**
     * Gets the version of Java executing this mojo as a Version object.
     *
     * @return a Version object of the running Java version
     */
    protected Version getJavaVersion() {
        return Version.parseFromString(getJavaVersionString());
    }

    /**
     * Gets the version of Java executing this mojo as a String.
     *
     * @return a String of the running Java version
     */
    protected String getJavaVersionString() {
        return System.getProperty("java.version");
    }

    /**
     * Determines whether this mojo can be run with the version of Groovy supplied.
     *
     * @return <code>true</code> only if the version of Groovy supports this mojo.
     */
    protected boolean groovyVersionSupportsAction() {
        return classWrangler.getGroovyVersion() != null && groovyAtLeast(minGroovyVersion);
    }

    /**
     * Determines whether the detected Groovy version is the specified version
     * or newer.
     *
     * @param version the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is the specified version or newer, <code>false</code> otherwise
     */
    protected boolean groovyAtLeast(Version version) {
        return ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), version);
    }

    /**
     * Determines whether the detected Groovy version is the specified version.
     *
     * @param version the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is the specified version, <code>false</code> otherwise
     */
    protected boolean groovyIs(Version version) {
        return ClassWrangler.groovyIs(classWrangler.getGroovyVersion(), version);
    }

    /**
     * Determines whether the detected Groovy version is
     * newer than the specified version.
     *
     * @param version the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is newer than the specified version, <code>false</code> otherwise
     */
    protected boolean groovyNewerThan(Version version) {
        return ClassWrangler.groovyNewerThan(classWrangler.getGroovyVersion(), version);
    }

    /**
     * Determines whether the detected Groovy version is
     * older than the specified version.
     *
     * @param version the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is older than the specified version, <code>false</code> otherwise
     */
    protected boolean groovyOlderThan(Version version) {
        return ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), version);
    }

}
