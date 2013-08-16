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
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.util.List;


/**
 * The base mojo class, which all other mojos extend.
 *
 * @author Keegan Witt
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
     * The Maven Settings.
     *
     * @parameter property="settings"
     * @required
     * @readonly
     */
    protected Settings settings;

    /**
     * The plugin dependencies.
     *
     * @parameter property="plugin.artifacts"
     * @required
     * @readonly
     */
    protected List<Artifact> pluginArtifacts;

    /**
     * The local repository.
     *
     * @parameter property="localRepository"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * The reactor projects.
     *
     * @parameter property="reactorProjects"
     * @required
     * @readonly
     */
    protected List<MavenProject> reactorProjects;

    /**
     * Logs the version of groovy used by this mojo.
     *
     * @param goal The goal to mention in the log statement showing Groovy version
     */
    protected void logGroovyVersion(final String goal) {
        if (getLog().isInfoEnabled()) {
            getLog().info("Using Groovy " + getGroovyVersion() + " from project compile classpath to perform " + goal + ".");
        }
    }

    /**
     * Gets the version of Groovy used from the dependency information.
     *
     * @return The version Groovy used by the project
     */
    protected String getGroovyVersion() {
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
        Artifact groovyDependency = null;

        if (pluginArtifacts != null) {
            for (Object art : pluginArtifacts) {
                Artifact artifact = (Artifact) art;
                if (isGroovyGroupId(artifact) && isGroovyArtifactId(artifact) && artifact.getType().equals("jar")) {
                    groovyDependency = artifact;
                    break;
                }
            }
        }

        if (groovyDependency == null) {
            if (project.getCompileDependencies() != null) {
                for (Object dep : project.getCompileDependencies()) {
                    Dependency dependency = (Dependency) dep;
                    if (isGroovyGroupId(dependency) && isGroovyArtifactId(dependency) && dependency.getType().equals("jar")) {
                        groovyDependency = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), VersionRange.createFromVersion(dependency.getVersion()), dependency.getScope(), dependency.getType(), dependency.getClassifier() != null ? dependency.getClassifier() : "", null);
                        break;
                    }
                }
            }
        }

        return groovyDependency;
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
     * Whether the groupId of the dependency is Groovy's groupId.
     *
     * @param artifact The dependency to inspect
     * @return <code>true</code> if the dependency's groupId is a Groovy groupId, <code>false</code> otherwise
     */
    protected boolean isGroovyGroupId(final Artifact artifact) {
        return artifact.getGroupId().equals("org.codehaus.groovy") || artifact.getGroupId().equals("groovy");
    }

    /**
     * Whether the artifactId of the dependency is Groovy's artifactId.
     *
     * @param artifact The dependency to inspect
     * @return <code>true</code> if the dependency's groupId is a Groovy groupId, <code>false</code> otherwise
     */
    protected boolean isGroovyArtifactId(final Artifact artifact) {
        return artifact.getArtifactId().equals("groovy-all") || artifact.getArtifactId().equals("groovy-all-minimal")
                || artifact.getArtifactId().equals("groovy") || artifact.getArtifactId().equals("groovy-all-jdk14")
                || artifact.getArtifactId().equals("groovy-jdk14");
    }

}
