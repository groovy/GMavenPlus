/*
 * Copyright 2013 the original author or authors.
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

import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.model.Version;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;


/**
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class GenerateTestStubsMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private GenerateTestStubsMojo generateTestStubsMojo;

    @Before
    public void setup() throws Exception {
        Mockito.doNothing().when(generateTestStubsMojo).logGroovyVersion(Mockito.anyString());
        Mockito.doReturn(new HashSet<File>()).when(generateTestStubsMojo).getTestSources();
        Mockito.doReturn(new HashSet<File>()).when(generateTestStubsMojo).getTestStubs();
        generateTestStubsMojo.project = Mockito.mock(MavenProject.class);
        generateTestStubsMojo.testStubsOutputDirectory = Mockito.mock(File.class);
        Mockito.doReturn(Mockito.mock(Build.class)).when(generateTestStubsMojo.project).getBuild();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCallsExpectedMethods() throws Exception {
        Mockito.doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        Mockito.doNothing().when(generateTestStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateTestStubsMojo.execute();
        Mockito.verify(generateTestStubsMojo, Mockito.times(1)).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGroovyVersionDoesntSupportAction() throws Exception {
        Mockito.doReturn(new Version(0)).when(generateTestStubsMojo).getGroovyVersion();
        Mockito.doReturn(false).when(generateTestStubsMojo).groovyVersionSupportsAction();
        generateTestStubsMojo.execute();
        Mockito.verify(generateTestStubsMojo, Mockito.never()).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSkipped() throws Exception {
        Mockito.doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        generateTestStubsMojo.skip = true;
        generateTestStubsMojo.execute();
        Mockito.verify(generateTestStubsMojo, Mockito.never()).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateTestStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateTestStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new InvocationTargetException(Mockito.mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(generateTestStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateTestStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateTestStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateTestStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateTestStubsMojo).doStubGeneration(Mockito.anySet(), Mockito.any(File.class));
        generateTestStubsMojo.execute();
    }

}
