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

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;


/**
 * Adds Groovy test stubs directory back to Maven's list of test source directories. Normally, you won't need to use this mojo.
 *
 * @author Keegan Witt
 * @since 1.1
 */
@Mojo(name = "addTestStubSources", threadSafe = true)
public class AddTestStubSourcesMojo extends AbstractGroovyStubSourcesMojo {

    /**
     * The location for the compiled test classes.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/groovy-stubs/test")
    protected File testStubsOutputDirectory;

    /**
     * Flag to allow adding test sources to be skipped.
     */
    @Parameter(property = "maven.test.skip", defaultValue = "false")
    protected boolean skipTests;

    /**
     * Executes this mojo.
     */
    @Override
    public void execute() {
        if (!skipTests) {
            getLog().debug("Added test stub directory " + testStubsOutputDirectory.getAbsolutePath() + " to project test sources.");
            project.addTestCompileSourceRoot(testStubsOutputDirectory.getAbsolutePath());
        }
    }

}
