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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.FileUtils;
import org.codehaus.gmavenplus.util.NoExitSecurityManager;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import static org.codehaus.gmavenplus.util.ReflectionUtils.*;


/**
 * Executes Groovy scripts (in the pom or external), bound to the current project.
 * Note that this mojo requires Groovy &gt;= 1.5.0.
 * Note that it references the plugin classloader to pull in dependencies Groovy didn't include
 * (for things like Ant for AntBuilder, Ivy for @grab, and Jansi for Groovysh).
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
@Mojo(name = "execute", requiresDependencyResolution = ResolutionScope.TEST, configurator = "include-project-test-dependencies", threadSafe = true)
public class ExecuteMojo extends AbstractToolsMojo {

    /**
     * Groovy 1.7.0 version.
     */
    protected static final Version GROOVY_1_7_0 = new Version(1, 7, 0);

    /**
     * Groovy scripts to run (in order). Can be a script body, a {@link java.net.URL URL} to a script
     * (local or remote), or a filename.
     */
    @Parameter(required = true)
    protected String[] scripts;

    /**
     * Whether to continue executing remaining scripts when a script fails.
     */
    @Parameter(defaultValue = "false")
    protected boolean continueExecuting;

    /**
     * The encoding of script files.
     *
     * @since 1.0-beta-2
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    protected String sourceEncoding;

    /**
     * Flag to allow script execution to be skipped.
     *
     * @since 1.9.1
     */
    @Parameter(defaultValue = "false")
    protected boolean skipScriptExecution;

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs (causes a "BUILD ERROR" message to be displayed)
     */
    @Override
    public void execute() throws MojoExecutionException {
        doExecute();
    }

    /**
     * Does the actual execution.
     *
     * @throws MojoExecutionException If an unexpected problem occurs (causes a "BUILD ERROR" message to be displayed)
     */
    protected synchronized void doExecute() throws MojoExecutionException {
        if (skipScriptExecution) {
            getLog().info("Skipping script execution because ${skipScriptExecution} was set to true.");
            return;
        }

        if (scripts == null || scripts.length == 0) {
            getLog().info("No scripts specified for execution. Skipping.");
            return;
        }

        try {
            setupClassWrangler(project.getTestClasspathElements(), includeClasspath);
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to add project test dependencies to classpath.", e);
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Test dependencies weren't resolved.", e);
        }

        logPluginClasspath();
        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());

        try {
            if (getLog().isDebugEnabled()) {
                getLog().debug("Project test classpath:\n" + project.getTestClasspathElements());
            }
        } catch (DependencyResolutionRequiredException e) {
            getLog().debug("Unable to log project test classpath");
        }

        if (groovyVersionSupportsAction()) {
            final SecurityManager sm = System.getSecurityManager();
            try {
                if (!allowSystemExits) {
                    System.setSecurityManager(new NoExitSecurityManager());
                }

                // get classes we need with reflection
                Class<?> groovyShellClass = classWrangler.getClass("groovy.lang.GroovyShell");

                // create a GroovyShell to run scripts in
                Object shell = setupShell(groovyShellClass);

                // run the scripts
                executeScripts(groovyShellClass, shell);
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath (" + e.getMessage() + "). Do you have Groovy as a compile dependency in your project or the plugin?", e);
            } catch (InvocationTargetException e) {
                throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
            } catch (InstantiationException e) {
                throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
            } finally {
                if (!allowSystemExits) {
                    System.setSecurityManager(sm);
                }
            }
        } else {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support script execution. The minimum version of Groovy required is " + minGroovyVersion + ". Skipping script execution.");
        }
    }

    /**
     * Instantiates a new groovy.lang.GroovyShell object.
     *
     * @param groovyShellClass the groovy.lang.GroovyShell class
     * @return a new groovy.lang.GroovyShell object
     * @throws InvocationTargetException when a reflection invocation needed for shell configuration cannot be completed
     * @throws IllegalAccessException    when a method needed for shell configuration cannot be accessed
     * @throws InstantiationException    when a class needed for shell configuration cannot be instantiated
     * @throws ClassNotFoundException    when a class needed for shell configuration cannot be found
     */
    protected Object setupShell(final Class<?> groovyShellClass) throws InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Object shell;
        if (sourceEncoding != null) {
            Class<?> compilerConfigurationClass = classWrangler.getClass("org.codehaus.groovy.control.CompilerConfiguration");
            Object compilerConfiguration = invokeConstructor(findConstructor(compilerConfigurationClass));
            invokeMethod(findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, sourceEncoding);
            shell = invokeConstructor(findConstructor(groovyShellClass, compilerConfigurationClass), compilerConfiguration);
        } else {
            shell = invokeConstructor(findConstructor(groovyShellClass));
        }
        initializeProperties();
        Method setProperty = findMethod(groovyShellClass, "setProperty", String.class, Object.class);
        if (bindPropertiesToSeparateVariables) {
            for (Object k : properties.keySet()) {
                invokeMethod(setProperty, shell, k, properties.get(k));
            }
        } else {
            invokeMethod(setProperty, shell, "properties", properties);
        }

        return shell;
    }

    /**
     * Executes the configured scripts.
     *
     * @param groovyShellClass the groovy.lang.GroovyShell class
     * @param shell            a groovy.lag.GroovyShell object
     * @throws InvocationTargetException when a reflection invocation needed for script execution cannot be completed
     * @throws IllegalAccessException    when a method needed for script execution cannot be accessed
     * @throws MojoExecutionException    when an exception occurred during script execution (causes a "BUILD ERROR" message to be displayed)
     */
    protected void executeScripts(final Class<?> groovyShellClass, final Object shell) throws InvocationTargetException, IllegalAccessException, MojoExecutionException {
        int scriptNum = 1;
        for (String script : scripts) {
            try {
                // TODO: try as file first, then as URL?
                try {
                    // it's a URL to a script
                    executeScriptFromUrl(groovyShellClass, shell, script);
                } catch (MalformedURLException e) {
                    // it's not a URL to a script, try as a filename
                    File scriptFile = new File(script);
                    if (scriptFile.isFile()) {
                        if (getLog().isInfoEnabled()) {
                            getLog().info("Running Groovy script from " + scriptFile.getCanonicalPath() + ".");
                        }
                        Method evaluateFile = findMethod(groovyShellClass, "evaluate", File.class);
                        invokeMethod(evaluateFile, shell, scriptFile);
                    } else {
                        // it's neither a filename or URL, treat as a script body
                        Method evaluateString = findMethod(groovyShellClass, "evaluate", String.class);
                        invokeMethod(evaluateString, shell, script);
                    }
                }
            } catch (IOException ioe) {
                if (continueExecuting) {
                    getLog().error("An Exception occurred while executing script " + scriptNum + ". Continuing to execute remaining scripts.", ioe);
                } else {
                    throw new MojoExecutionException("An Exception occurred while executing script " + scriptNum + ".", ioe);
                }
            }
            scriptNum++;
        }
    }

    /**
     * Executes a script at a URL location.
     *
     * @param groovyShellClass the GroovyShell class
     * @param shell            a groovy.lag.GroovyShell object
     * @param script           the script URL to execute
     * @throws IOException               when the stream can't be opened on the URL
     * @throws InvocationTargetException when a reflection invocation needed for script execution cannot be completed
     * @throws IllegalAccessException    when a method needed for script execution cannot be accessed
     */
    protected void executeScriptFromUrl(Class<?> groovyShellClass, Object shell, String script) throws IOException, InvocationTargetException, IllegalAccessException {
        URL url = new URL(script);
        if (getLog().isInfoEnabled()) {
            getLog().info("Running Groovy script from " + url + ".");
        }
        if (groovyAtLeast(GROOVY_1_7_0)) {
            Method evaluateUrlWithReader = findMethod(groovyShellClass, "evaluate", Reader.class);
            BufferedReader reader = null;
            try {
                if (sourceEncoding != null) {
                    reader = new BufferedReader(new InputStreamReader(url.openStream(), sourceEncoding));
                } else {
                    reader = new BufferedReader(new InputStreamReader(url.openStream()));
                }
                invokeMethod(evaluateUrlWithReader, shell, reader);
            } finally {
                FileUtils.closeQuietly(reader);
            }
        } else {
            Method evaluateUrlWithStream = findMethod(groovyShellClass, "evaluate", InputStream.class);
            InputStream inputStream = null;
            try {
                if (sourceEncoding != null) {
                    getLog().warn("Source encoding does not apply to Groovy versions previous to 1.7.0, ignoring.");
                }
                inputStream = url.openStream();
                invokeMethod(evaluateUrlWithStream, shell, inputStream);
            } finally {
                FileUtils.closeQuietly(inputStream);
            }
        }
    }

}
