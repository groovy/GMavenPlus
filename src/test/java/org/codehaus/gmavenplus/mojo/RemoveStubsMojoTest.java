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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit tests for the RemoveStubsMojo class.
 *
 * @author Keegan Witt
 */
public class RemoveStubsMojoTest {
    private RemoveStubsMojo removeStubsMojo;
    private MavenProject project;
    private static final String PATH = "PATH";

    @Before
    public void setup() {
        project = new MavenProject();
        removeStubsMojo = new RemoveStubsMojo();
        removeStubsMojo.project = project;
    }

    @Test
    public void testRemoveSourcePathContainsPath() {
        project.addCompileSourceRoot(PATH);
        removeStubsMojo.removeSourcePath(PATH);
        Assert.assertEquals(0, project.getCompileSourceRoots().size());
    }

    @Test
    public void testRemoveSourcePathNotContainsPath() {
        removeStubsMojo.removeSourcePath(PATH);
        Assert.assertEquals(0, project.getCompileSourceRoots().size());
    }

}
