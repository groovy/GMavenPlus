/*
 * Copyright 2014 Keegan Witt
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

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;


/**
 * Launches a Groovy console window bound to the current project.
 *
 * @author Keegan Witt
 * @since 1.1
 *
 * @goal console
 * @configurator include-project-test-dependencies
 * @requiresDependencyResolution test
 * @threadSafe
 */
public class ConsoleMojo extends AbstractToolsMojo {

    /**
     * Executes this mojo.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException If an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws org.apache.maven.plugin.MojoFailureException If an expected problem (such as a compilation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        logGroovyVersion("console");

        try {
            // get classes we need with reflection
            Class consoleClass = Class.forName("groovy.ui.Console");
            Class bindingClass = Class.forName("groovy.lang.Binding");

            // create console to run
            Object binding = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(bindingClass));
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "settings", settings);
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "project", project);
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "session", session);
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "pluginArtifacts", pluginArtifacts);
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "localRepository", localRepository);
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "reactorProjects", reactorProjects);
            Object console = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(consoleClass, ClassLoader.class, bindingClass), bindingClass.getClassLoader(), binding);
            // this is intentionally after the default properties so that the user can override if desired
            for (String key : properties.stringPropertyNames()) {
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, key, properties.getProperty(key));
            }

            // run the console
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(consoleClass, "run"), console);

            // TODO: replace the active wait with a passive one
            while (((JFrame) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(consoleClass, "getFrame"), console)).isVisible()) {
                // do nothing, wait for console window to be closed
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // do nothing, the sleep is only here to not eat as many CPU cycles
                }
            }
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Unable to get a Groovy class from classpath.  Do you have Groovy as a compile dependency in your project?", e);
        } catch (InvocationTargetException e) {
            throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
        } catch (InstantiationException e) {
            throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
        }
    }

}
