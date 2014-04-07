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
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.model.Version;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


/**
 * The base mojo class, which all other mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractGroovyMojo extends AbstractMojo {

    /** The pattern defining Groovy files. */
    protected static final String GROOVY_SOURCES_PATTERN = "**" + File.separator + "*.groovy";

    /** The pattern defining Java stub files. */
    protected static final String JAVA_SOURCES_PATTERN = "**" + File.separator + "*.java";

    /** Cached Groovy dependency. */
    private static Artifact groovyDependency = null;

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
    protected Version minGroovyVersion = new Version(1, 5, 0);

    /** Whether to use the plugin's classloader as a parent classloader. */
    protected boolean usePluginClassLoader = false;

    /**
     * Logs the version of groovy used by this mojo.
     *
     * @param goal The goal to mention in the log statement showing Groovy version
     */
    protected void logGroovyVersion(final String goal) {
        if (getLog().isInfoEnabled()) {
            String logMessage = "Using Groovy " + getGroovyVersion();
            if (isGroovyIndy()) {
                logMessage += "-indy";
            }
                        getLog().info(logMessage + " from project compile classpath to perform " + goal + ".");
                    }
    }

    /**
     * Gets the version string of Groovy used from the dependency information.
     *
     * @return The version string of Groovy used by the project
     */
    protected String getGroovyVersionString() {
        String groovyVersion = null;

        /*
         * You can call InvokerHelper.getVersion() for versions 1.0 - 1.8.x but
         * not for 1.9+.
         * You can call GroovySystem.getVersion() for versions 1.6.6+.
         * And for some reason InvokerHelper.getVersion() was returning an empty
         * String for 1.5.0, so I decided to just get it from the dependency itself.
         */
        Artifact groovyDependency = getGroovyDependency();

        if (groovyDependency == null) {
            getLog().error("Unable to determine Groovy version.  Is Groovy declared as a dependency?");
        } else {
            groovyVersion = groovyDependency.getVersion();
        }

        return groovyVersion;
    }

    /**
     * Gets the version of Groovy used from the dependency information.
     *
     * @return The version of Groovy used by the project
     */
    protected Version getGroovyVersion() {
        try {
            return Version.parseFromString(getGroovyVersionString());
        } catch (Exception e) {
            getLog().error("Unable to determine Groovy version.  Is Groovy declared as a dependency?");
            return null;
        }
    }

    /**
     * Gets the version of Groovy used from the dependency information.
     *
     * @return <code>true</code> if the version of Groovy uses InvokeDynamic,
     *         <code>false</code> if not or Groovy dependency cannot be found.
     */
    protected boolean isGroovyIndy() {
        boolean isGroovyIndy = false;

        Artifact groovyDependency = getGroovyDependency();
        if (groovyDependency == null) {
            getLog().error("Unable to determine Groovy version.  Is Groovy declared as a dependency?");
        } else if ("indy".equals(groovyDependency.getClassifier())) {
            isGroovyIndy = true;
        }

        return isGroovyIndy;
    }

    /**
     * Gets the Groovy dependency used by the project.
     *
     * @return The Groovy dependency used by the project
     */
    protected Artifact getGroovyDependency() {
        if (groovyDependency == null) {
            if (usePluginClassLoader && pluginArtifacts != null) {
                for (Object art : pluginArtifacts) {
                    Artifact artifact = (Artifact) art;
                    if (isGroovyJar(artifact)) {
                        groovyDependency = artifact;
                        break;
                    }
                }
            }

            if (groovyDependency == null && project.getCompileDependencies() != null) {
                for (Object dep : project.getCompileDependencies()) {
                    Dependency dependency = (Dependency) dep;
                    if (isGroovyJar(dependency)) {
                        groovyDependency = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), VersionRange.createFromVersion(dependency.getVersion()), dependency.getScope(), dependency.getType(), dependency.getClassifier() != null ? dependency.getClassifier() : "", null);
                        break;
                    }
                }
            }

            if (groovyDependency == null && project.getTestDependencies() != null) {
                for (Object dep : project.getTestDependencies()) {
                    Dependency dependency = (Dependency) dep;
                    if (isGroovyJar(dependency)) {
                        groovyDependency = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), VersionRange.createFromVersion(dependency.getVersion()), dependency.getScope(), dependency.getType(), dependency.getClassifier() != null ? dependency.getClassifier() : "", null);
                        break;
                    }
                }
            }
        }

        return groovyDependency;
    }

    /**
     * Whether the dependency is a Groovy jar.
     *
     * @param dependency The dependency to inspect
     * @return <code>true</code> if the dependency's groupId is a Groovy groupId, <code>false</code> otherwise
     */
    protected boolean isGroovyJar(Dependency dependency) {
        return isGroovyGroupId(dependency) && isGroovyArtifactId(dependency) && dependency.getType().equals("jar");
    }

    /**
     * Whether the groupId of the dependency is Groovy's groupId.
     *
     * @param dependency The dependency to inspect
     * @return <code>true</code> if the dependency's groupId is a Groovy groupId, <code>false</code> otherwise
     */
    protected boolean isGroovyGroupId(final Dependency dependency) {
        return dependency.getGroupId().equals("org.codehaus.groovy") || dependency.getGroupId().equals("groovy");
    }

    /**
     * Whether the artifactId of the dependency is Groovy's artifactId.
     *
     * @param dependency The dependency to inspect
     * @return <code>true</code> if the dependency's groupId is a Groovy groupId, <code>false</code> otherwise
     */
    protected boolean isGroovyArtifactId(final Dependency dependency) {
        return dependency.getArtifactId().equals("groovy-all") || dependency.getArtifactId().equals("groovy-all-minimal")
                                    || dependency.getArtifactId().equals("groovy") || dependency.getArtifactId().equals("groovy-all-jdk14")
                                    || dependency.getArtifactId().equals("groovy-jdk14");
    }

    /**
     * Whether the artifact is a Groovy jar.
     *
     * @param artifact The artifact to inspect
     * @return <code>true</code> if the artifact's groupId is a Groovy groupId, <code>false</code> otherwise
     */
    protected boolean isGroovyJar(Artifact artifact) {
        return isGroovyGroupId(artifact) && isGroovyArtifactId(artifact) && artifact.getType().equals("jar");
    }

    /**
     * Whether the groupId of the artifact is Groovy's groupId.
     *
     * @param artifact The artifact to inspect
     * @return <code>true</code> if the artifact's groupId is a Groovy groupId, <code>false</code> otherwise
     */
    protected boolean isGroovyGroupId(final Artifact artifact) {
        return artifact.getGroupId().equals("org.codehaus.groovy") || artifact.getGroupId().equals("groovy");
    }

    /**
     * Whether the artifactId of the artifact is Groovy's artifactId.
     *
     * @param artifact The artifact to inspect
     * @return <code>true</code> if the artifact's groupId is a Groovy groupId, <code>false</code> otherwise
     */
    protected boolean isGroovyArtifactId(final Artifact artifact) {
        return artifact.getArtifactId().equals("groovy-all") || artifact.getArtifactId().equals("groovy-all-minimal")
                || artifact.getArtifactId().equals("groovy") || artifact.getArtifactId().equals("groovy-all-jdk14")
                || artifact.getArtifactId().equals("groovy-jdk14");
    }

    /**
     * Determines whether the version of Java executing this mojo supports invokedynamic (is at least 1.7).
     *
     * @return <code>true</code> if the running Java supports invokedynamic, <code>false</code> otherwise
     */
    protected boolean isJavaSupportIndy() {
        return getJavaVersion().compareTo(new Version(1, 7), false) >= 0;
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
        return getGroovyVersion() != null && getGroovyVersion().compareTo(minGroovyVersion) >= 0;
    }

    /**
     * Creates a new ClassLoader with the specified classpath.
     *
     * @param classpath the classpath (a list of file path Strings) to include in the new loader
     * @return the new ClassLoader
     * @throws MalformedURLException
     */
    protected ClassLoader createNewClassLoader(List classpath) throws MalformedURLException {
        List<URL> urlsList = new ArrayList<URL>();
        for (Object classPathObject : classpath) {
            String path = (String) classPathObject;
            urlsList.add(new File(path).toURI().toURL());
        }
        URL[] urlsArray = urlsList.toArray(new URL[urlsList.size()]);
        return new URLClassLoader(urlsArray, ClassLoader.getSystemClassLoader());
    }

}
