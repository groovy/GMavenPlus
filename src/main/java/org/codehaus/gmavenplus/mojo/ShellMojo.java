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
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.codehaus.gmavenplus.util.NoExitSecurityManager;
import org.codehaus.gmavenplus.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;


/**
 * Launches a Groovy shell bound to the current project.
 * Note that this mojo requires Groovy >= 1.5.0.
 * Note that it references the plugin ClassLoader to pull in dependencies
 * Groovy didn't include (for things like Ant for AntBuilder, Ivy for @grab,
 * and Jansi for Groovysh)).
 *
 * @author Keegan Witt
 * @since 1.1
 *
 * @goal shell
 * @configurator include-project-test-dependencies
 * @requiresDependencyResolution test
 */
public class ShellMojo extends AbstractToolsMojo {

    /**
     * Groovy shell verbosity level.  Should be one of:
     * <ul>
     *   <li>QUIET</li>
     *   <li>INFO</li>
     *   <li>DEBUG</li>
     *   <li>VERBOSE</li>
     * </ul>
     *
     * @parameter default-value="QUIET"
     */
    protected String verbosity;

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws MojoFailureException If an expected problem (such as a invocation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        classWrangler = new ClassWrangler(Thread.currentThread().getContextClassLoader(), getLog());

        if (groovyVersionSupportsAction()) {
            classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());
            logPluginClasspath();
            if (getLog().isDebugEnabled()) {
                try {
                    getLog().debug("Project test classpath:\n" + project.getTestClasspathElements());
                } catch (DependencyResolutionRequiredException e) {
                    getLog().warn("Unable to log project test classpath", e);
                }
            }

            final SecurityManager sm = System.getSecurityManager();
            try {
                if (!allowSystemExits) {
                    System.setSecurityManager(new NoExitSecurityManager());
                }

                // get classes we need with reflection
                Class shellClass = classWrangler.getClass("org.codehaus.groovy.tools.shell.Groovysh");
                Class bindingClass = classWrangler.getClass("groovy.lang.Binding");
                Class ioClass = classWrangler.getClass("org.codehaus.groovy.tools.shell.IO");
                Class verbosityClass = classWrangler.getClass("org.codehaus.groovy.tools.shell.IO$Verbosity");
                Class loggerClass = classWrangler.getClass("org.codehaus.groovy.tools.shell.util.Logger");

                // create shell to run
                Object shell = setupShell(shellClass, bindingClass, ioClass, verbosityClass, loggerClass);

                // run the shell
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(shellClass, "run", String.class), shell, (String) null);
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath.  Do you have Groovy as a compile dependency in your project or the plugin?", e);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof NoClassDefFoundError && e.getCause().getMessage() != null && e.getCause().getMessage().contains("jline")) {
                    throw new MojoExecutionException("Unable to get a JLine class from classpath.  This might be because of a JLine version mismatch.  If you are using Groovy < 2.2.0-beta-1, make sure you include JLine 1.0 as a runtime dependency in your project or the plugin.", e);
                } else {
                    throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
                }
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
            } catch (InstantiationException e) {
                throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
            } finally {
                System.setSecurityManager(sm);
            }
        } else {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersion() + ") doesn't support running a shell.  The minimum version of Groovy required is " + minGroovyVersion + ".  Skipping shell startup.");
        }
    }

    /**
     * Creates the Groovysh to run.
     *
     * @param shellClass the Groovysh class
     * @param bindingClass the Binding class
     * @param ioClass the IO class
     * @param verbosityClass the Verbosity
     * @param loggerClass the Logger class
     * @return the Groovysh shell to run
     * @throws InstantiationException when a class needed for setting up a shell cannot be instantiated
     * @throws IllegalAccessException when a method needed for setting up a shell cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up a shell cannot be completed
     */
    protected Object setupShell(final Class shellClass, final Class bindingClass, final Class ioClass, final Class verbosityClass, final Class loggerClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object binding = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(bindingClass));
        initializeProperties();
        if (bindPropertiesToSeparateVariables) {
            for (Object k : properties.keySet()) {
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, k, properties.get(k));
            }
        } else {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "properties", properties);
        }
        Object io = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(ioClass));
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(ioClass, "setVerbosity", verbosityClass), io, ReflectionUtils.invokeStaticMethod(ReflectionUtils.findMethod(verbosityClass, "forName", String.class), verbosity));
        ReflectionUtils.findField(loggerClass, "io", ioClass).set(null, io);

        return ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(shellClass, ClassLoader.class, bindingClass, ioClass), Thread.currentThread().getContextClassLoader(), binding, io);
    }

}
