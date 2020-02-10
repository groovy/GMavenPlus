/*
 * Copyright (C) 2013 the original author or authors.
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
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


/**
 * @author Keegan Witt
 */
public class CompileMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private CompileMojo compileMojo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Set<File> sources = new TreeSet<>();
        sources.add(mock(File.class));
        doReturn(sources).when(compileMojo).getTestFiles(any(FileSet[].class), eq(false));
        compileMojo.outputDirectory = mock(File.class);
        compileMojo.project = mock(MavenProject.class);
        doReturn(mock(Build.class)).when(compileMojo.project).getBuild();
        doReturn(true).when(compileMojo).groovyVersionSupportsAction();
        compileMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(new Version(1, 5, 0)).when(compileMojo.classWrangler).getGroovyVersion();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCallsExpectedMethods() throws Exception {
        doNothing().when(compileMojo).doCompile(anySetOf(File.class), anyList(), any(File.class));
        compileMojo.execute();
        verify(compileMojo, never()).logPluginClasspath();
    }

    @Test(expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(anySetOf(File.class), anyList(), any(File.class));
        compileMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new InvocationTargetException(mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(anySetOf(File.class), anyList(), any(File.class));
        compileMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(anySetOf(File.class), anyList(), any(File.class));
        compileMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(anySetOf(File.class), anyList(), any(File.class));
        compileMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testDependencyResolutionRequiredExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(mock(DependencyResolutionRequiredException.class)).when(compileMojo.project).getCompileClasspathElements();
        compileMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    @SuppressWarnings("deprecation")
    public void testMalformedURLExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new MalformedURLException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(anySetOf(File.class), anyList(), any(File.class));
        compileMojo.execute();
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        compileMojo = new CompileMojo();
        compileMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(new Version(1, 5, 0)).when(compileMojo.classWrangler).getGroovyVersion();
        assertTrue(compileMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        compileMojo = new CompileMojo();
        compileMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(new Version(1, 0)).when(compileMojo.classWrangler).getGroovyVersion();
        assertFalse(compileMojo.groovyVersionSupportsAction());
    }

}
