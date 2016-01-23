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
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;


/**
 * Unit tests for the RemoveStubsMojo class.
 *
 * @author Keegan Witt
 */
public class RemoveStubsMojoTest {
    @Spy
    private RemoveStubsMojo removeStubsMojo;
    @Mock
    private File stubsDir;
    private static final String PATH = "FAKE_PATH";
    private MavenProject project;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        project = new MavenProject();
        removeStubsMojo.project = project;
        doReturn(PATH).when(stubsDir).getAbsolutePath();
        removeStubsMojo.stubsOutputDirectory = stubsDir;
    }

    @Test
    public void testRemoveSourcePathContainsPath() throws Exception {
        project.addCompileSourceRoot(stubsDir.getAbsolutePath());
        assertEquals(1, project.getCompileSourceRoots().size());
        removeStubsMojo.execute();
        assertEquals(0, project.getCompileSourceRoots().size());
    }

    @Test
    public void testRemoveSourcePathNotContainsPath() throws Exception {
        assertEquals(0, project.getCompileSourceRoots().size());
        removeStubsMojo.execute();
        assertEquals(0, project.getCompileSourceRoots().size());
    }

}
