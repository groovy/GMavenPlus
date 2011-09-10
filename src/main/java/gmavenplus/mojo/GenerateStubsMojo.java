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

import java.lang.reflect.InvocationTargetException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Generates stubs for the main sources
 *
 * @author Keegan Witt
 *
 * @goal generateStubs
 * @phase generate-sources
 */
public class GenerateStubsMojo extends AbstractGenerateStubsMojo {

    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        logGroovyVersion("generateStubs");

        try {
            doStubGeneration(getSources(), stubsOutputDirectory);
            resetStubModifiedDates(getStubs());
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Unable to get a Groovy class from classpath. Do you have Groovy as a compile dependency in your project?", e);
        } catch (InvocationTargetException e) {
            throw new MojoExecutionException("Unable to call a method on a Groovy class from classpath.", e);
        } catch (InstantiationException e) {
            throw new MojoExecutionException("Unable to instantiate a Groovy class from classpath.", e);
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
        }
    }

}
