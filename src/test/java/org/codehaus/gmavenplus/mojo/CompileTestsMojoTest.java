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
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * @author Keegan Witt
 */
public class CompileTestsMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private CompileTestsMojo compileTestsMojo;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(new HashSet<File>()).when(compileTestsMojo).getTestSources();
        compileTestsMojo.project = mock(MavenProject.class);
        doReturn(mock(Build.class)).when(compileTestsMojo.project).getBuild();
        doReturn(true).when(compileTestsMojo).groovyVersionSupportsAction();
        compileTestsMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(new Version(1, 5, 0)).when(compileTestsMojo.classWrangler).getGroovyVersion();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCallsExpectedMethods() throws Exception {
        doNothing().when(compileTestsMojo).doCompile(anySet(), anyList(), any(File.class));
        compileTestsMojo.execute();
        verify(compileTestsMojo, times(1)).doCompile(anySet(), anyList(), any(File.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSkipped() throws Exception {
        compileTestsMojo.skipTests = true;
        compileTestsMojo.execute();
        verify(compileTestsMojo, never()).doCompile(anySet(), anyList(), any(File.class));
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileTestsMojo).doCompile(anySet(), anyList(), any(File.class));
        compileTestsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new InvocationTargetException(mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(compileTestsMojo).doCompile(anySet(), anyList(), any(File.class));
        compileTestsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileTestsMojo).doCompile(anySet(), anyList(), any(File.class));
        compileTestsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileTestsMojo).doCompile(anySet(), anyList(), any(File.class));
        compileTestsMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testMalformedURLExceptionThrowsMojoExecutionException() throws Exception {
        doThrow(new MalformedURLException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileTestsMojo).doCompile(anySet(), anyList(), any(File.class));
        compileTestsMojo.execute();
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        compileTestsMojo = new CompileTestsMojo();
        compileTestsMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(new Version(1, 5, 0)).when(compileTestsMojo.classWrangler).getGroovyVersion();
        assertTrue(compileTestsMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        compileTestsMojo = new CompileTestsMojo();
        compileTestsMojo.classWrangler = mock(ClassWrangler.class);
        doReturn(new Version(1, 0)).when(compileTestsMojo.classWrangler).getGroovyVersion();
        assertFalse(compileTestsMojo.groovyVersionSupportsAction());
    }

}
