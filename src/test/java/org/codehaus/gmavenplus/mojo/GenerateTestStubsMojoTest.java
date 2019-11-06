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
import org.codehaus.gmavenplus.model.Version;
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
public class GenerateTestStubsMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private GenerateTestStubsMojo generateTestStubsMojo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn(new TreeSet<File>()).when(generateTestStubsMojo).getTestFiles(any(FileSet[].class), eq(false));
        doReturn(new TreeSet<File>()).when(generateTestStubsMojo).getStubs(any(File.class));
        generateTestStubsMojo.project = mock(MavenProject.class);
        generateTestStubsMojo.testStubsOutputDirectory = mock(File.class);
        doReturn(mock(Build.class)).when(generateTestStubsMojo.project).getBuild();
        generateTestStubsMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(new Version(1, 8, 2)).when(generateTestStubsMojo.classWrangler).getGroovyVersion();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCallsExpectedMethods() throws Exception {
        doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        doNothing().when(generateTestStubsMojo).doStubGeneration(anySetOf(File.class), anyList(), any(File.class));
        generateTestStubsMojo.execute();
        verify(generateTestStubsMojo, times(1)).doStubGeneration(anySetOf(File.class), anyList(), any(File.class));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSkipped() throws Exception {
        doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        generateTestStubsMojo.skipTests = true;
        generateTestStubsMojo.execute();
        verify(generateTestStubsMojo, never()).doStubGeneration(anySetOf(File.class), anyList(), any(File.class));
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateTestStubsMojo).doStubGeneration(anySetOf(File.class), anyList(), any(File.class));
        generateTestStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        doThrow(new InvocationTargetException(mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(generateTestStubsMojo).doStubGeneration(anySetOf(File.class), anyList(), any(File.class));
        generateTestStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateTestStubsMojo).doStubGeneration(anySetOf(File.class), anyList(), any(File.class));
        generateTestStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateTestStubsMojo).doStubGeneration(anySetOf(File.class), anyList(), any(File.class));
        generateTestStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    public void testDependencyResolutionRequiredExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(mock(DependencyResolutionRequiredException.class)).when(generateTestStubsMojo.project).getTestClasspathElements();
        generateTestStubsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testMalformedURLExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(generateTestStubsMojo).groovyVersionSupportsAction();
        doThrow(new MalformedURLException(INTENTIONAL_EXCEPTION_MESSAGE)).when(generateTestStubsMojo).doStubGeneration(anySetOf(File.class), anyList(), any(File.class));
        generateTestStubsMojo.execute();
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        doReturn(Version.parseFromString("1.5.0")).when(generateTestStubsMojo.classWrangler).getGroovyVersion();
        assertTrue(generateTestStubsMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        doReturn(Version.parseFromString("1.0")).when(generateTestStubsMojo.classWrangler).getGroovyVersion();
        assertFalse(generateTestStubsMojo.groovyVersionSupportsAction());
    }

}
