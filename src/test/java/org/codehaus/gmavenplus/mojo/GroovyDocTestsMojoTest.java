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
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


/**
 * @author Keegan Witt
 */
public class GroovyDocTestsMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private GroovyDocTestsMojo groovyDocTestsMojo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Set<File> sources = new TreeSet<>();
        sources.add(mock(File.class));
        doReturn(sources).when(groovyDocTestsMojo).getTestFiles(any(FileSet[].class), eq(false));
        groovyDocTestsMojo.testGroovyDocOutputDirectory = mock(File.class);
        groovyDocTestsMojo.project = mock(MavenProject.class);
        doReturn(mock(File.class)).when(groovyDocTestsMojo.project).getBasedir();
        groovyDocTestsMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(new Version(1, 5, 0)).when(groovyDocTestsMojo.classWrangler).getGroovyVersion();
    }

    @Test
    public void testCallsExpectedMethods() throws Exception {
        doReturn(true).when(groovyDocTestsMojo).groovyVersionSupportsAction();
        doNothing().when(groovyDocTestsMojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
        groovyDocTestsMojo.execute();
        verify(groovyDocTestsMojo, times(1)).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
    }

    @Test(expected = MojoExecutionException.class)
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(groovyDocTestsMojo).groovyVersionSupportsAction();
        doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovyDocTestsMojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
        groovyDocTestsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(groovyDocTestsMojo).groovyVersionSupportsAction();
        doThrow(new InvocationTargetException(mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(groovyDocTestsMojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
        groovyDocTestsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(groovyDocTestsMojo).groovyVersionSupportsAction();
        doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovyDocTestsMojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
        groovyDocTestsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(groovyDocTestsMojo).groovyVersionSupportsAction();
        doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovyDocTestsMojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
        groovyDocTestsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testDependencyResolutionRequiredExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(mock(DependencyResolutionRequiredException.class)).when(groovyDocTestsMojo.project).getTestClasspathElements();
        groovyDocTestsMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testMalformedURLExceptionThrowsMojoExecutionException() throws Exception {
        doReturn(true).when(groovyDocTestsMojo).groovyVersionSupportsAction();
        doThrow(new MalformedURLException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovyDocTestsMojo).doGroovyDocGeneration(any(FileSet[].class), anyList(), any(File.class));
        groovyDocTestsMojo.execute();
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        doReturn(Version.parseFromString("1.5.0")).when(groovyDocTestsMojo.classWrangler).getGroovyVersion();
        assertTrue(groovyDocTestsMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        doReturn(Version.parseFromString("1.0")).when(groovyDocTestsMojo.classWrangler).getGroovyVersion();
        assertFalse(groovyDocTestsMojo.groovyVersionSupportsAction());
    }

}
