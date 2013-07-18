/*
 * Copyright 2013 Keegan Witt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, softwaredistributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus.mojo;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashSet;


/**
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class GenerateStubsMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private GenerateStubsMojo generateStubsMojo;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        Mockito.doNothing().when(generateStubsMojo).logGroovyVersion(Mockito.anyString());
        Mockito.doReturn(new HashSet<File>()).when(generateStubsMojo).getSources();
        Mockito.doReturn(new HashSet<File>()).when(generateStubsMojo).getStubs();
        generateStubsMojo.project = Mockito.mock(MavenProject.class);
        generateStubsMojo.stubsOutputDirectory = Mockito.mock(File.class);
        Mockito.doReturn(Mockito.mock(Build.class)).when(generateStubsMojo.project).getBuild();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCallsExpectedMethods() throws Exception {
        Mockito.doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        Mockito.doNothing().when(generateStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateStubsMojo.execute();
        Mockito.verify(generateStubsMojo, Mockito.times(1)).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGroovyVersionDoesntSupportAction() throws Exception {
        Mockito.doReturn(false).when(generateStubsMojo).groovyVersionSupportsAction();
        generateStubsMojo.execute();
        Mockito.verify(generateStubsMojo, Mockito.never()).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new InvocationTargetException(Mockito.mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(generateStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateStubsMojo.execute();
    }

}
