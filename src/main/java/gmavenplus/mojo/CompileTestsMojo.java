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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;


/**
 * Compiles the test sources
 *
 * @author Keegan Witt
 *
 * @phase test-compile
 * @goal testCompile
 * @requiresDependencyResolution test
 */
public class CompileTestsMojo extends AbstractCompileMojo {

    /**
     * Flag to allow test compilation to be skipped
     *
     * @parameter expression="${maven.test.skip}" default-value="false"
     */
    protected boolean skip;

    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!skip) {
            logGroovyVersion("compileTests");

            try {
                doCompile(getTestSources(), getProjectClasspathElements(), testOutputDirectory);
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath.  Do you have Groovy as a compile dependency in your project?", e);
            } catch (InvocationTargetException e) {
                throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
            } catch (InstantiationException e) {
                throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
            } catch (DependencyResolutionRequiredException e) {
                throw new MojoExecutionException("Test dependencies weren't resolved.", e);
            } catch (MalformedURLException e) {
                throw new MojoExecutionException("Unable to add project dependencies to classpath.", e);
            }
        } else {
            getLog().info("Skipping compilation of tests because ${maven.test.skip} was set to true.");
        }
    }

    /**
     * @return
     * @throws DependencyResolutionRequiredException
     */
    protected List getProjectClasspathElements() throws DependencyResolutionRequiredException {
        return project.getTestClasspathElements();
    }

    /**
     * @return
     */
    protected List<File> getJavaSources() {
        List<File> javaSources = new ArrayList<File>();

        FileSetManager fileSetManager = new FileSetManager();
        for (Object root : project.getTestCompileSourceRoots()) {
            String directory = (String) root;
            FileSet fs = new FileSet();
            fs.setDirectory(directory);
            fs.setIncludes(Arrays.asList(JAVA_PATTERN));
            String[] includes = fileSetManager.getIncludedFiles(fs);
            for (String file : includes) {
                javaSources.add(new File(directory + File.separator + file));
            }
        }

        return javaSources;
    }

}
