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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.gmavenplus.model.internal.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


/**
 * @author Keegan Witt
 */
public class GenerateStubsMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private GenerateStubsMojo generateStubsMojo;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        doReturn(new TreeSet<File>()).when(generateStubsMojo).getFiles(any(FileSet[].class), eq(false));
        doReturn(new TreeSet<File>()).when(generateStubsMojo).getStubs(any(File.class));
        generateStubsMojo.project = mock(MavenProject.class);
        generateStubsMojo.stubsOutputDirectory = mock(File.class);
        doReturn(mock(Build.class)).when(generateStubsMojo.project).getBuild();
        generateStubsMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(new Version(1, 8, 2)).when(generateStubsMojo.classWrangler).getGroovyVersion();
    }

    @Test
    public void testCallsExpectedMethods() throws Exception {
        doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        doNothing().when(generateStubsMojo).doStubGeneration(anySet(), anyList(), any(File.class));
        generateStubsMojo.execute();
        verify(generateStubsMojo, times(1)).doStubGeneration(anySet(), anyList(), any(File.class));
    }

    @Test(expected = MojoExecutionException.class)
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateStubsMojo).doStubGeneration(anySet(), anyList(), any(File.class));
        generateStubsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        doThrow(new InvocationTargetException(mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(generateStubsMojo).doStubGeneration(anySet(), anyList(), any(File.class));
        generateStubsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateStubsMojo).doStubGeneration(anySet(), anyList(), any(File.class));
        generateStubsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateStubsMojo).doStubGeneration(anySet(), anyList(), any(File.class));
        generateStubsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testDependencyResolutionRequiredExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(mock(DependencyResolutionRequiredException.class)).when(generateStubsMojo.project).getCompileClasspathElements();
        generateStubsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testMalformedURLExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateStubsMojo).groovyVersionSupportsAction();
        doThrow(new MalformedURLException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateStubsMojo).doStubGeneration(anySet(), anyList(), any(File.class));
        generateStubsMojo.execute();
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        doReturn(Version.parseFromString("1.5.0")).when(generateStubsMojo.classWrangler).getGroovyVersion();
        assertTrue(generateStubsMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        doReturn(Version.parseFromString("1.0")).when(generateStubsMojo.classWrangler).getGroovyVersion();
        assertFalse(generateStubsMojo.groovyVersionSupportsAction());
    }

}
