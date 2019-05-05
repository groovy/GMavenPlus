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
import org.apache.maven.shared.model.fileset.FileSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the AddTestSourcesMojo class.
 *
 * @author Keegan Witt
 */
public class AddTestSourcesMojoTest {
    private AddTestSourcesMojo addTestSourcesMojo;

    private static final String PATH = "PATH";

    @Mock
    private MavenProject project;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        addTestSourcesMojo = new AddTestSourcesMojo();
        addTestSourcesMojo.project = project;
    }

    @Test
    public void testAddSourcePathContainsPath() {
        doReturn(singletonList(PATH)).when(project).getTestCompileSourceRoots();
        FileSet fs = new FileSet();
        fs.setDirectory(PATH);
        addTestSourcesMojo.testSources = new FileSet[] {fs};
        addTestSourcesMojo.execute();
        verify(project, never()).addTestCompileSourceRoot(anyString());
    }

    @Test
    public void testAddSourcePathNotContainsPath() {
        doReturn(singletonList(PATH)).when(project).getTestCompileSourceRoots();
        FileSet fs = new FileSet();
        fs.setDirectory("OTHER PATH");
        addTestSourcesMojo.testSources = new FileSet[] {fs};
        addTestSourcesMojo.execute();
        verify(project, times(1)).addTestCompileSourceRoot(anyString());
    }

}
