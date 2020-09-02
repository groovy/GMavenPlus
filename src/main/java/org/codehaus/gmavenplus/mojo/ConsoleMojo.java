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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.gmavenplus.util.NoExitSecurityManager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Set;

import static org.codehaus.gmavenplus.util.ReflectionUtils.*;


/**
 * Launches a Groovy console window bound to the current project.
 * Note that this mojo requires Groovy &gt;= 1.5.0.
 * Note that it references the plugin classloader to pull in dependencies Groovy didn't include
 * (for things like Ant for AntBuilder, Ivy for @grab, and Jansi for Groovysh).
 * Note that using the <code>ant</code> property requires Java 8, as the included Ant version was compiled for Java 8.
 *
 * @author Keegan Witt
 * @since 1.1
 */
@Mojo(name = "console", requiresDependencyResolution = ResolutionScope.TEST, configurator = "include-project-test-dependencies")
public class ConsoleMojo extends AbstractToolsMojo {

    /**
     * Script file to load into console. Can also be a project property referring to a file.
     *
     * @since 1.10.1
     */
    @Parameter(property = "consoleScript")
    protected String consoleScript;

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs (causes a "BUILD ERROR" message to be displayed)
     * @throws MojoFailureException   If unable to await console exit
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
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
            getLog().debug("Project test classpath:\n" + project.getTestClasspathElements());
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
                Class<?> consoleClass;
                try {
                    consoleClass = classWrangler.getClass("groovy.console.ui.Console");
                } catch (ClassNotFoundException e) {
                    consoleClass = classWrangler.getClass("groovy.ui.Console");
                }
                Class<?> bindingClass = classWrangler.getClass("groovy.lang.Binding");

                // create console to run
                Object console = setupConsole(consoleClass, bindingClass);

                // run the console
                invokeMethod(findMethod(consoleClass, "run"), console);

                // TODO: for some reason instantiating AntBuilder before calling run() causes its stdout and stderr streams to not be captured by the Console
                bindAntBuilder(consoleClass, bindingClass, console);

                // open script file
                loadScript(consoleClass, console);

                // wait for console to be closed
                waitForConsoleClose();
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath (" + e.getMessage() + "). Do you have Groovy as a compile dependency in your project or the plugin?", e);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof NoClassDefFoundError && "org/apache/ivy/core/report/ResolveReport".equals(e.getCause().getMessage())) {
                    throw new MojoExecutionException("Groovy 1.7.6 and 1.7.7 have a dependency on Ivy to run the console. Either change your Groovy version or add Ivy as a project or plugin dependency.", e);
                } else {
                    throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
                }
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
            } catch (InstantiationException e) {
                throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
            } finally {
                if (!allowSystemExits) {
                    System.setSecurityManager(sm);
                }
            }
        } else {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support running a console. The minimum version of Groovy required is " + minGroovyVersion + ". Skipping console startup.");
        }
    }

    protected void loadScript(Class<?> consoleClass, Object console) throws InvocationTargetException, IllegalAccessException {
        if (consoleScript != null) {
            Method loadScriptFile = findMethod(consoleClass, "loadScriptFile", File.class);
            File consoleScriptFile = new File(consoleScript);
            if (consoleScriptFile.isFile()) {
                invokeMethod(loadScriptFile, console, consoleScriptFile);
            } else if (project.getProperties().containsKey(consoleScript)) {
                consoleScriptFile = new File(project.getProperties().getProperty(consoleScript));
                if (consoleScriptFile.isFile()) {
                    invokeMethod(loadScriptFile, consoleScriptFile);
                } else {
                    getLog().warn("consoleScript ('" + consoleScript + "') doesn't exist in project properties or as a file.");
                }
            } else {
                getLog().warn("consoleScript ('" + consoleScript + "') doesn't exist in project properties or as a file.");
            }
        }
    }

    /**
     * Instantiates a groovy.ui.Console object.
     *
     * @param consoleClass the groovy.ui.Console class to use
     * @param bindingClass the groovy.lang.Binding class to use
     * @return a new groovy.ui.Console object
     * @throws InvocationTargetException when a reflection invocation needed for instantiating a console object cannot be completed
     * @throws IllegalAccessException    when a method needed for instantiating a console object cannot be accessed
     * @throws InstantiationException    when a class needed for instantiating a console object cannot be instantiated
     */
    protected Object setupConsole(final Class<?> consoleClass, final Class<?> bindingClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object binding = invokeConstructor(findConstructor(bindingClass));
        initializeProperties();
        Method setVariable = findMethod(bindingClass, "setVariable", String.class, Object.class);
        if (bindPropertiesToSeparateVariables) {
            for (Object k : properties.keySet()) {
                invokeMethod(setVariable, binding, k, properties.get(k));
            }
        } else {
            invokeMethod(setVariable, binding, "properties", properties);
        }

        return invokeConstructor(findConstructor(consoleClass, ClassLoader.class, bindingClass), Thread.currentThread().getContextClassLoader(), binding);
    }

    /**
     * Binds a new AntBuilder to the project properties.
     *
     * @param consoleClass the groovy.ui.Console class to use
     * @param bindingClass the groovy.lang.Binding class to use
     * @param console      the groovy.ui.Console object to use
     * @throws ClassNotFoundException    when a class needed for binding an AntBuilder object cannot be found
     * @throws IllegalAccessException    when a method needed for binding an AntBuilder object cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for binding an AntBuilder object cannot be completed
     */
    protected void bindAntBuilder(Class<?> consoleClass, Class<?> bindingClass, Object console) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        if (properties.containsKey("ant")) {
            Class<?> groovyShellClass = classWrangler.getClass("groovy.lang.GroovyShell");
            Object shell = getField(findField(consoleClass, "shell", groovyShellClass), console);
            Object binding = invokeMethod(findMethod(groovyShellClass, "getContext"), shell);
            Object antBuilder = null;
            try {
                antBuilder = invokeConstructor(findConstructor(classWrangler.getClass("groovy.ant.AntBuilder")));
            } catch (ClassNotFoundException e1) {
                getLog().debug("groovy.ant.AntBuilder not available, trying groovy.util.AntBuilder.");
                try {
                    antBuilder = invokeConstructor(findConstructor(classWrangler.getClass("groovy.util.AntBuilder")));
                } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException e2) {
                    logUnableToInitializeAntBuilder(e2);
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                logUnableToInitializeAntBuilder(e);
            }
            if (antBuilder != null) {
                if (bindPropertiesToSeparateVariables) {
                    invokeMethod(findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "ant", antBuilder);
                } else {
                    properties.put("ant", antBuilder);
                }
            }
        }
    }

    /**
     * Waits for the console in use to be closed.
     *
     * @throws MojoFailureException if the execution was interrupted while running or it was unable to find the console thread to wait on
     */
    protected void waitForConsoleClose() throws MojoFailureException {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[0]);
        Thread consoleThread = null;
        for (Thread thread : threadArray) {
            if ("AWT-Shutdown".equals(thread.getName())) {
                consoleThread = thread;
                break;
            }
        }
        if (consoleThread != null) {
            try {
                consoleThread.join();
            } catch (InterruptedException e) {
                throw new MojoFailureException("Mojo interrupted while waiting for Console thread to end.", e);
            }
        } else {
            throw new MojoFailureException("Unable to locate Console thread to wait on.");
        }
    }

}
