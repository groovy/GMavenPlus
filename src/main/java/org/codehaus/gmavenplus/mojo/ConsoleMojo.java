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
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.codehaus.gmavenplus.util.NoExitSecurityManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import static org.codehaus.gmavenplus.util.ReflectionUtils.findConstructor;
import static org.codehaus.gmavenplus.util.ReflectionUtils.findField;
import static org.codehaus.gmavenplus.util.ReflectionUtils.findMethod;
import static org.codehaus.gmavenplus.util.ReflectionUtils.getField;
import static org.codehaus.gmavenplus.util.ReflectionUtils.invokeConstructor;
import static org.codehaus.gmavenplus.util.ReflectionUtils.invokeMethod;


/**
 * Launches a Groovy console window bound to the current project.
 * Note that this mojo requires Groovy >= 1.5.0.
 * Note that it references the plugin classloader to pull in dependencies
 * Groovy didn't include (for things like Ant for AntBuilder, Ivy for @grab,
 * and Jansi for Groovysh).
 *
 * @author Keegan Witt
 * @since 1.1
 */
@Mojo(name="console", requiresDependencyResolution=ResolutionScope.TEST, configurator="include-project-test-dependencies")
public class ConsoleMojo extends AbstractToolsMojo {

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws MojoFailureException If an expected problem (such as an invocation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        classWrangler = new ClassWrangler(Thread.currentThread().getContextClassLoader(), getLog());

        try {
            getLog().debug("Project test classpath:\n" + project.getTestClasspathElements());
        } catch (DependencyResolutionRequiredException e) {
            getLog().warn("Unable to log project test classpath", e);
        }
        logPluginClasspath();
        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());

        if (groovyVersionSupportsAction()) {

            final SecurityManager sm = System.getSecurityManager();
            try {
                if (!allowSystemExits) {
                    System.setSecurityManager(new NoExitSecurityManager());
                }

                // get classes we need with reflection
                Class<?> consoleClass = classWrangler.getClass("groovy.ui.Console");
                Class<?> bindingClass = classWrangler.getClass("groovy.lang.Binding");

                // create console to run
                Object console = setupConsole(consoleClass, bindingClass);

                // run the console
                invokeMethod(findMethod(consoleClass, "run"), console);

                // TODO: for some reason instantiating AntBuilder before calling run() causes its stdout and stderr streams to not be captured by the Console
                bindAntBuilder(consoleClass, bindingClass, console);

                // wait for console to be closed
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
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
                        throw new MojoExecutionException("Mojo interrupted while waiting for Console thread to end.", e);
                    }
                } else {
                    throw new MojoFailureException("Unable to locate Console thread to wait on.");
                }
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath.  Do you have Groovy as a compile dependency in your project or the plugin?", e);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof NoClassDefFoundError && "org/apache/ivy/core/report/ResolveReport".equals(e.getCause().getMessage())) {
                    throw new MojoExecutionException("Groovy 1.7.6 and 1.7.7 have a dependency on Ivy to run the console.  Either change your Groovy version or add Ivy as a project or plugin dependency.", e);
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
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support running a console.  The minimum version of Groovy required is " + minGroovyVersion + ".  Skipping console startup.");
        }
    }

    /**
     * Binds a new AntBuilder to the project properties.
     *
     * @param consoleClass the groovy.ui.Console class to use
     * @param bindingClass the groovy.lang.Binding class to use
     * @param console the groovy.ui.Console object to use
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    protected void bindAntBuilder(Class<?> consoleClass, Class<?> bindingClass, Object console) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (properties.containsKey("ant")) {
            Class<?> groovyShellClass = classWrangler.getClass("groovy.lang.GroovyShell");
            Object shell = getField(findField(consoleClass, "shell", groovyShellClass), console);
            Object binding = invokeMethod(findMethod(groovyShellClass, "getContext"), shell);
            Object antBuilder = invokeConstructor(findConstructor(classWrangler.getClass("groovy.util.AntBuilder")));
            if (bindPropertiesToSeparateVariables) {
                invokeMethod(findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "ant", antBuilder);
            } else {
                properties.put("ant", antBuilder);
            }
        }
    }

    /**
     * Instantiates a Groovy Console.
     *
     * @param consoleClass the Console class
     * @param bindingClass the Binding class
     * @return the instantiated Console
     * @throws InstantiationException when a class needed for creating a console cannot be instantiated
     * @throws IllegalAccessException when a method needed for creating a console cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for creating a console cannot be completed
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

}
