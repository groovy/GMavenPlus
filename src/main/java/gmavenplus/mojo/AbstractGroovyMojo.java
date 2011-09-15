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

package gmavenplus.mojo;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;


/**
 * @author Keegan Witt
 *
 * @requiresDependencyResolution compile
 * @configurator include-project-dependencies
 */
public abstract class AbstractGroovyMojo extends AbstractMojo {
    private Log log;
    protected static final String DEFAULT_SOURCE_PATTERN = "**/*.groovy";
    protected static final String DEFAULT_STUB_PATTERN = "**/*.java";

    /**
     * The Maven project this plugin is being used on
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    @Override
    public Log getLog() {
        if (log == null) {
            log = new SystemStreamLog();
        }

        return log;
    }

    /**
     * @param goal
     */
    protected void logGroovyVersion(String goal) {
        if (getLog().isInfoEnabled()) {
            getLog().info("Using Groovy " + getGroovyVersion() + " from project compile classpath to perform " + goal + ".");
        }
    }

    /**
     * Gets the version of Groovy used from the dependency information
     *
     * @return
     */
    protected String getGroovyVersion() {
        String groovyVersion = null;

        /*
         * You can call InvokerHelper.getVersion() for versions 1.0 - 1.8.x but
         * not for 1.9+
         * You can call GroovySystem.getVersion() for versions 1.6.6+
         * And for some reason InvokerHelper.getVersion() was returning an empty
         * String for 1.5.0, so I decided to just get it from the dependency itself.
         */

        for (Object dependency : project.getCompileDependencies()) {
            Dependency dep = (Dependency) dependency;
            if (dep.getGroupId().equals("org.codehaus.groovy") &&
                    (dep.getArtifactId().equals("groovy-all") || dep.getArtifactId().equals("groovy")) &&
                    dep.getType().equals("jar")) {
                groovyVersion = dep.getVersion();
                break;
            }
        }

        if (groovyVersion == null || groovyVersion.isEmpty()) {
            getLog().error("Unable to determine Groovy version.");
        }

        return groovyVersion;
    }

}
