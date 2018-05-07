/*
 * Copyright (C) 2012 the original author or authors.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;


/**
 * This mojo removes Groovy test stubs from the project's sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-3
 */
@Mojo(name = "removeTestStubs", defaultPhase = LifecyclePhase.TEST_COMPILE, threadSafe = true)
public class RemoveTestStubsMojo extends AbstractGroovyStubSourcesMojo {

    /**
     * The location for the compiled test classes.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/groovy-stubs/test")
    protected File outputDirectory;

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs.  Throwing this exception causes a "BUILD ERROR" message to be displayed
     * @throws MojoFailureException If an expected problem (such as a compilation failure) occurs.  Throwing this exception causes a "BUILD FAILURE" message to be displayed
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        project.getTestCompileSourceRoots().remove(outputDirectory.getAbsolutePath());
    }

}
