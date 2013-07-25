/*
 * Copyright 2013 Keegan Witt
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


/**
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class GroovydocMojoTest {
    private static final String INTENTIONAL_EXCEPTION_MESSAGE = "Intentionally blowing up.";

    @Spy
    private GroovydocMojo groovydocMojo;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        Mockito.doNothing().when(groovydocMojo).logGroovyVersion(Mockito.anyString());
        Mockito.doReturn(new ArrayList<String>()).when(groovydocMojo).getSources(Mockito.any(FileSet.class));
        groovydocMojo.project = Mockito.mock(MavenProject.class);
        Mockito.doReturn(Mockito.mock(File.class)).when(groovydocMojo.project).getBasedir();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCallsExpectedMethods() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doNothing().when(groovydocMojo).generateGroovydoc(Mockito.any(FileSet[].class), Mockito.any(File.class));
        groovydocMojo.execute();
        Mockito.verify(groovydocMojo, Mockito.times(1)).generateGroovydoc(Mockito.any(FileSet[].class), Mockito.any(File.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGroovyVersionDoesntSupportAction() throws Exception {
        Mockito.doReturn(false).when(groovydocMojo).groovyVersionSupportsAction();
        groovydocMojo.execute();
        Mockito.verify(groovydocMojo, Mockito.never()).generateGroovydoc(Mockito.any(FileSet[].class), Mockito.any(File.class));
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testClassNotFoundExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new ClassNotFoundException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovydocMojo).generateGroovydoc(Mockito.any(FileSet[].class), Mockito.any(File.class));
        groovydocMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInvocationTargetExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new InvocationTargetException(Mockito.mock(Exception.class), INTENTIONAL_EXCEPTION_MESSAGE)).when(groovydocMojo).generateGroovydoc(Mockito.any(FileSet[].class), Mockito.any(File.class));
        groovydocMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testInstantiationExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new InstantiationException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovydocMojo).generateGroovydoc(Mockito.any(FileSet[].class), Mockito.any(File.class));
        groovydocMojo.execute();
    }

    @Test (expected = MojoExecutionException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalAccessExceptionThrowsMojoExecutionException() throws Exception {
        Mockito.doReturn(true).when(groovydocMojo).groovyVersionSupportsAction();
        Mockito.doThrow(new IllegalAccessException(INTENTIONAL_EXCEPTION_MESSAGE)).when(groovydocMojo).generateGroovydoc(Mockito.any(FileSet[].class), Mockito.any(File.class));
        groovydocMojo.execute();
    }

}
