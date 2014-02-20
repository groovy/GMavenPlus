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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.gmavenplus.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;


/**
 * Launches a Groovy shell bound to the current project.
 * Note that this mojo requires Groovy >= 1.5.0.
 *
 * @author Keegan Witt
 * @since 1.1
 *
 * @goal shell
 * @configurator include-project-test-dependencies
 * @requiresDependencyResolution test
 * @threadSafe
 */
public class ShellMojo extends AbstractToolsMojo {

    /**
     * Executes this mojo.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws org.apache.maven.plugin.MojoFailureException If an expected problem (such as a invocation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (groovyVersionSupportsAction()) {
            logGroovyVersion("shell");

            try {
                // get classes we need with reflection
                Class shellClass = Class.forName("org.codehaus.groovy.tools.shell.Groovysh");
                Class bindingClass = Class.forName("groovy.lang.Binding");
                Class ioClass = Class.forName("org.codehaus.groovy.tools.shell.IO");
                Class loggerClass = Class.forName("org.codehaus.groovy.tools.shell.util.Logger");

                // create shell to run
                Object binding = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(bindingClass));
                initializeProperties();
                for (Object k : properties.keySet()) {
                    String key = (String) k;
                    ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, key, properties.get(key));
                }
                Object io = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(ioClass));
                ReflectionUtils.findField(loggerClass, "io", ioClass).set(null, io);
                Object shell = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(shellClass, ClassLoader.class, bindingClass, ioClass), bindingClass.getClassLoader(), binding, io);

                // run the shell
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(shellClass, "run", String.class), shell, (String) null);
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath.  Do you have Groovy as a compile dependency in your project?", e);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof NoClassDefFoundError && e.getCause().getMessage() != null && e.getCause().getMessage().contains("jline")) {
                    throw new MojoExecutionException("Unable to get a JLine class from classpath.  Do you have JLine as a plugin dependency in your project?  Use JLine 1.x for Groovy < 2.2.0, or JLine 2.x for Groovy >= 2.2.0.", e);
                } else {
                    throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
                }
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
            } catch (InstantiationException e) {
                throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
            }
        } else {
            getLog().error("Your Groovy version (" + getGroovyVersion() + ") doesn't support running a shell.  The minimum version of Groovy required is " + minGroovyVersion + ".  Skipping shell startup.");
        }
    }

}
