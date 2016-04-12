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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the AddSourcesMojo class.
 *
 * @author Keegan Witt
 */
public class AddSourcesMojoTest {
    private AddSourcesMojo addSourcesMojo;

    private static final String PATH = "PATH";

    @Mock
    private MavenProject project;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        addSourcesMojo = new AddSourcesMojo();
        addSourcesMojo.project = project;
    }

    @Test
    public void testAddSourcePathContainsPath() throws Exception {
        doReturn(singletonList(PATH)).when(project).getCompileSourceRoots();
        FileSet fs = new FileSet();
        fs.setDirectory(PATH);
        addSourcesMojo.sources = new FileSet[] {fs};
        addSourcesMojo.execute();
        verify(project, never()).addCompileSourceRoot(anyString());
    }

    @Test
    public void testAddSourcePathNotContainsPath() throws Exception {
        doReturn(singletonList(PATH)).when(project).getCompileSourceRoots();
        FileSet fs = new FileSet();
        fs.setDirectory("OTHER PATH");
        addSourcesMojo.sources = new FileSet[] {fs};
        addSourcesMojo.execute();
        verify(project, times(1)).addCompileSourceRoot(anyString());
    }

}
