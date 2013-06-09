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

import org.codehaus.gmavenplus.util.ReflectionUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Executes Groovy scripts (in the pom or external).
 *
 * @author Keegan Witt
 *
 * @goal execute
 */
public class ExecuteMojo extends AbstractGroovyMojo {

    /**
     * Groovy scripts to run (in order).  Can be an actual Groovy script or a
     * {@link java.net.URL URL} to a Groovy script (local or remote).
     *
     * @parameter
     * @required
     */
    protected String[] scripts;

    /**
     * Whether to continue executing remaining scripts when a script fails
     *
     * @parameter default-value="false"
     */
    protected boolean continueExecuting;

    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        logGroovyVersion("execute");

        try {
            // get classes we need with reflection
            Class<?> groovyShellClass = Class.forName("groovy.lang.GroovyShell");

            // create a GroovyShell to run scripts in
            Object shell = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyShellClass));

            // TODO: load runtime project dependencies onto classpath before executing so they can be used in scripts?

            // run the scripts
            int scriptNum = 1;
            for (String script : scripts) {
                try {
                    URL url = new URL(script);
                    // it's a URL to a script
                    getLog().info("Fetching Groovy script from " + url.toString() + ".");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder scriptSource = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        scriptSource.append(line).append("\n");
                    }
                    if (!scriptSource.toString().isEmpty()) {
                        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyShellClass, "evaluate", String.class), shell, scriptSource.toString());
                    }
                } catch (MalformedURLException e) {
                    // it's not a URL to a script, treat as a script body
                    ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyShellClass, "evaluate", String.class), shell, script);
                } catch (IOException e) {
                    if (continueExecuting) {
                        getLog().error("An Exception occurred while executing script " + scriptNum + ".  Continuing to execute remaining scripts.", e);
                    } else {
                        throw new MojoExecutionException("An Exception occurred while executing script " + scriptNum + ".", e);
                    }
                }
                scriptNum++;
            }
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Unable to get a Groovy class from classpath.  Do you have Groovy as a compile dependency in your project?", e);
        } catch (InvocationTargetException e) {
            throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
        } catch (InstantiationException e) {
            throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
        }
    }

}
