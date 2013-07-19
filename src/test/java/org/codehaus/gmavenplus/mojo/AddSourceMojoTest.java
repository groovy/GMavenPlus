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

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;


/**
 * Unit tests for the AddSourceMojo class.
 *
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class AddSourceMojoTest {
    private AddSourceMojo addSourceMojo;

    private static final String PATH = "PATH";
    @Mock
    private MavenProject project;

    @Before
    public void setup() {
        addSourceMojo = new AddSourceMojo();
        addSourceMojo.project = project;
    }

    @Test
    public void testAddSourcePathContainsPath() {
        Mockito.doReturn(Arrays.asList(PATH)).when(project).getCompileSourceRoots();
        addSourceMojo.addSourcePath(PATH);
        Mockito.verify(project, Mockito.never()).addCompileSourceRoot(Mockito.anyString());
    }

    @Test
    public void testAddSourcePathNotContainsPath() {
        Mockito.doReturn(Arrays.asList(PATH)).when(project).getCompileSourceRoots();
        addSourceMojo.addSourcePath("OTHER_PATH");
        Mockito.verify(project, Mockito.times(1)).addCompileSourceRoot(Mockito.anyString());
    }

    @Test
    public void testAddTestSourcePathContainsPath() {
        Mockito.doReturn(Arrays.asList(PATH)).when(project).getCompileSourceRoots();
        addSourceMojo.addTestSourcePath(PATH);
        Mockito.verify(project, Mockito.never()).addCompileSourceRoot(Mockito.anyString());
    }

    @Test
    public void testAddTestSourcePathNotContainsPath() {
        Mockito.doReturn(Arrays.asList(PATH)).when(project).getCompileSourceRoots();
        addSourceMojo.addTestSourcePath("OTHER_PATH");
        Mockito.verify(project, Mockito.times(1)).addTestCompileSourceRoot(Mockito.anyString());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetProjectClasspathElementsThrowsUnsupportedOperationException() throws Exception {
        addSourceMojo.getProjectClasspathElements();
    }

}
