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

import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Assert;
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
public class CompileMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private CompileMojo compileMojo;

    @Before
    public void setup() throws Exception {
        Mockito.doReturn(new HashSet<File>()).when(compileMojo).getSources();
        compileMojo.project = Mockito.mock(MavenProject.class);
        Mockito.doReturn(Mockito.mock(Build.class)).when(compileMojo.project).getBuild();
        Mockito.doReturn(true).when(compileMojo).groovyVersionSupportsAction();
        compileMojo.classWrangler = Mockito.mock(ClassWrangler.class);
        Mockito.doReturn(new Version(1, 5, 0)).when(compileMojo.classWrangler).getGroovyVersion();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCallsExpectedMethods() throws Exception {
        Mockito.doNothing().when(compileMojo).doCompile(Mockito.anySet(), Mockito.anyList(), Mockito.any(File.class));
        compileMojo.execute();
        Mockito.verify(compileMojo, Mockito.never()).logPluginClasspath();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(Mockito.anySet(), Mockito.anyList(), Mockito.any(File.class));
        compileMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doThrow(new InvocationTargetException(Mockito.mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(Mockito.anySet(), Mockito.anyList(), Mockito.any(File.class));
        compileMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(Mockito.anySet(), Mockito.anyList(), Mockito.any(File.class));
        compileMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(Mockito.anySet(), Mockito.anyList(), Mockito.any(File.class));
        compileMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testMalformedURLExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doThrow(new MalformedURLException(INTENTIONAL_EXCEPTION_MESSAGE)).when(compileMojo).doCompile(Mockito.anySet(), Mockito.anyList(), Mockito.any(File.class));
        compileMojo.execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGroovyVersionDoesntSupportAction() throws Exception {
        Mockito.doReturn(false).when(compileMojo).groovyVersionSupportsAction();
        compileMojo.execute();
        Mockito.verify(compileMojo, Mockito.never()).logPluginClasspath();
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        compileMojo = new CompileMojo();
        compileMojo.classWrangler = Mockito.mock(ClassWrangler.class);
        Mockito.doReturn(new Version(1, 5, 0)).when(compileMojo.classWrangler).getGroovyVersion();
        Assert.assertTrue(compileMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        compileMojo = new CompileMojo();
        compileMojo.classWrangler = Mockito.mock(ClassWrangler.class);
        Mockito.doReturn(new Version(1, 0)).when(compileMojo.classWrangler).getGroovyVersion();
        Assert.assertFalse(compileMojo.groovyVersionSupportsAction());
    }

}
