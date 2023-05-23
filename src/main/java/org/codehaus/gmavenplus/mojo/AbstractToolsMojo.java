/*
 * Copyright 2014 the original author or authors.
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

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.gmavenplus.model.IncludeClasspath;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import static org.codehaus.gmavenplus.util.ReflectionUtils.findConstructor;
import static org.codehaus.gmavenplus.util.ReflectionUtils.invokeConstructor;


/**
 * The base tools mojo, which all tool mojos extend.
 * Note that it references the plugin classloader to pull in dependencies
 * Groovy didn't include (for things like Ant for AntBuilder, Ivy for @grab, and Jansi for Groovysh).
 * Note that using the <code>ant</code> property requires Java 8, as the included Ant version was compiled for Java 8.
 *
 * @author Keegan Witt
 * @since 1.1
 */
public abstract class AbstractToolsMojo extends AbstractGroovyMojo {

    /**
     * Maven ProjectHelper to use in properties.
     *
     * @since 1.3
     */
    @Component
    protected MavenProjectHelper projectHelper;

    /**
     * Properties to make available in scripts as variables using the property name. By default will include
     * <dl>
     *   <dt>project</dt>
     *     <dd>A org.apache.maven.project.Project object of the current Maven project.</dd>
     *   <dt>session</dt>
     *     <dd>A org.apache.maven.execution.MavenSession object of the current Maven session.</dd>
     *   <dt>pluginArtifacts</dt>
     *     <dd>A list of org.apache.maven.artifact.Artifact objects of this plugin's artifacts.</dd>
     *   <dt>mojoExecution</dt>
     *     <dd>A org.apache.maven.plugin.MojoExecution object of this plugin's mojo execution.</dd>
     *   <dt>log</dt>
     *     <dd>A org.apache.maven.plugin.logging.Log object of Maven's log.</dd>
     *   <dt>ant</dt>
     *     <dd>A groovy.util.AntBuilder object (if groovy.ant.AntBuilder or groovy.util.AntBuilder is available).</dd>
     * </dl>
     * These can be overridden.
     *
     * @since 1.0-beta-3
     */
    @Parameter
    protected Properties properties = new Properties();

    /**
     * Whether to bind each property to a separate variable (otherwise binds properties to a single 'properties' variable).
     *
     * @since 1.2
     */
    @Parameter(defaultValue = "true")
    protected boolean bindPropertiesToSeparateVariables;

    /**
     * What classpath to include. One of
     * <ul>
     *   <li>PROJECT_ONLY</li>
     *   <li>PROJECT_AND_PLUGIN</li>
     *   <li>PLUGIN_ONLY</li>
     * </ul>
     * Uses the same scope as the required dependency resolution of this mojo. Use only if you know what you're doing.
     *
     * @since 1.8.0
     */
    @Parameter(defaultValue = "PROJECT_AND_PLUGIN")
    protected IncludeClasspath includeClasspath;

    /**
     * Whether to add all properties from <code>project.properties</code> into properties.
     *
     * @since 1.10.1
     */
    @Parameter(defaultValue = "false")
    protected boolean bindAllProjectProperties;

    /**
     * Whether to add user session properties from <code>session.userProperties</code> that override project properties
     * into properties. <code>bindAllSessionUserProperties</code> takes priority over this property if present. Has no
     * effect if <code>bindAllProjectProperties</code> is <code>false</code>.
     *
     * @since 1.10.1
     */
    @Parameter(defaultValue = "false")
    protected boolean bindSessionUserOverrideProperties;

    /**
     * Whether to add all properties from <code>session.userProperties</code> into properties. If both
     * <code>bindAllProjectProperties</code> and <code>bindAllSessionUserProperties</code> are specified, the session
     * properties will override the project properties, but it will also include properties not present in project
     * properties. To only include user session properties that are also in project properties, use
     * <code>bindSessionUserOverrideProperties</code>.
     *
     * @since 1.10.1
     */
    @Parameter(defaultValue = "false")
    protected boolean bindAllSessionUserProperties;

    /**
     * Initializes the properties field.
     */
    protected void initializeProperties() {
        if (project != null && !properties.containsKey("project")) {
            properties.put("project", project);
        }
        if (session != null && !properties.containsKey("session")) {
            properties.put("session", session);
        }
        if (pluginArtifacts != null && !properties.containsKey("pluginArtifacts")) {
            properties.put("pluginArtifacts", pluginArtifacts);
        }
        if (mojoExecution != null && !properties.containsKey("mojoExecution")) {
            properties.put("mojoExecution", mojoExecution);
        }
        if (!properties.containsKey("log")) {
            properties.put("log", getLog());
        }
        if (projectHelper != null && !properties.containsKey("projectHelper")) {
            properties.put("projectHelper", projectHelper);
        }
        if (!properties.containsKey("ant")) {
            Object antBuilder = null;
            try {
                antBuilder = invokeConstructor(findConstructor(classWrangler.getClass("groovy.ant.AntBuilder")));
            } catch (ClassNotFoundException e1) {
                getLog().debug("groovy.ant.AntBuilder not available, trying groovy.util.AntBuilder.");
                try {
                    antBuilder = invokeConstructor(findConstructor(classWrangler.getClass("groovy.util.AntBuilder")));
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e2) {
                    logUnableToInitializeAntBuilder(e2);
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                logUnableToInitializeAntBuilder(e);
            }
            if (antBuilder != null) {
                properties.put("ant", antBuilder);
            }
        }
        if (bindSessionUserOverrideProperties && !bindAllProjectProperties) {
            getLog().warn("bindSessionUserOverrideProperties set without bindAllProjectProperties, ignoring.");
        }
        if (bindAllSessionUserProperties && bindSessionUserOverrideProperties) {
            getLog().warn("bindAllSessionUserProperties and bindSessionUserOverrideProperties both set, bindAllSessionUserProperties will take precedence.");
        }
        if (bindAllProjectProperties && project != null) {
            properties.putAll(project.getProperties());
        }
        if (session != null) {
            if (bindAllSessionUserProperties) {
                properties.putAll(session.getUserProperties());
            } else if (bindAllProjectProperties && bindSessionUserOverrideProperties && project != null) {
                for (Object key : project.getProperties().keySet()) {
                    if (session.getUserProperties().get(key) != null) {
                        properties.put(key, session.getUserProperties().get(key));
                    }
                }
            }
        }
    }

    /**
     * Logs errors that caused the 'ant' object to not be populated.
     *
     * @param e the exception causing the failure
     */
    protected void logUnableToInitializeAntBuilder(final Throwable e) {
        getLog().warn("Unable to initialize 'ant' with a new AntBuilder object. Is Groovy a dependency?  If you are using Groovy >= 2.3.0-rc-1, remember to include groovy-ant as a dependency.");
    }

}
