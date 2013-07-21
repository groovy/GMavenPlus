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

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;


/**
 * The base mojo class, which all other mojos extend.
 *
 * @author Keegan Witt
 *
 * @requiresDependencyResolution compile
 * @configurator include-project-dependencies
 */
public abstract class AbstractGroovyMojo extends AbstractMojo {

    /**
     * The pattern defining Groovy files.
     */
    protected static final String DEFAULT_SOURCE_PATTERN = "**/*.groovy";

    /**
     * The pattern defining Java stub files.
     */
    protected static final String DEFAULT_STUB_PATTERN = "**/*.java";

    /**
     * The Maven project this plugin is being used on.
     *
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Logs the version of groovy used by this mojo.
     *
     * @param goal The goal to mention in the log statement showing Groovy version
     */
    protected void logGroovyVersion(String goal) {
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
        Dependency groovyDependency = getGroovyDependency();

        if (groovyDependency == null) {
            getLog().error("Unable to determine Groovy version.");
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

        Dependency groovyDependency = getGroovyDependency();
        if (groovyDependency == null) {
            getLog().error("Unable to determine Groovy version.");
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
    protected Dependency getGroovyDependency() {
        Dependency groovyDependency = null;

        for (Object dep : project.getCompileDependencies()) {
            Dependency dependency = (Dependency) dep;
            if (isGroovyGroupId(dependency) && isGroovyArtifactId(dependency) && dependency.getType().equals("jar")) {
                groovyDependency = dependency;
                break;
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
    protected boolean isGroovyGroupId(Dependency dependency) {
        return dependency.getGroupId().equals("org.codehaus.groovy") || dependency.getGroupId().equals("groovy");
    }

    /**
     * Whether the artifactId of the dependency is Groovy's artifactId.
     *
     * @param dependency The dependency to inspect
     * @return <code>true</code> if the dependency's groupId is a Groovy groupId, <code>false</code> otherwise
     */
    protected boolean isGroovyArtifactId(Dependency dependency) {
        return dependency.getArtifactId().equals("groovy-all") || dependency.getArtifactId().equals("groovy-all-minimal")
                                    || dependency.getArtifactId().equals("groovy") || dependency.getArtifactId().equals("groovy-all-jdk14")
                                    || dependency.getArtifactId().equals("groovy-jdk14");
    }

}
