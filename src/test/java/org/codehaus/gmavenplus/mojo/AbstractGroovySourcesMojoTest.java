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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;


/**
 * Unit tests for the AbstractGroovySourcesMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGroovySourcesMojoTest {
    private TestMojo testMojo;
    @Mock
    private MavenProject project;
    @Mock
    private File basedir;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testMojo = new TestMojo();
        doReturn(basedir).when(project).getBasedir();
        testMojo.project = project;
    }

    @Test
    public void testGetSourceRootsFromDefaultSources() {
        assertEquals(1, testMojo.getSourceRoots().length);
    }

    @Test
    public void testGetSourceRootsFromDefaultSourcesIncludingJava() {
        assertEquals(1, testMojo.getSourceRoots(true).length);
    }

    @Test
    public void testGetSourcesFromDefaultSourcesEmpty() {
        assertEquals(new HashSet<File>(), testMojo.getSources());
    }

    @Test
    public void testGetSourcesFromDefaultSourcesIncludingJavaEmpty() {
        assertEquals(new HashSet<File>(), testMojo.getSources(true));
    }

    @Test
    public void testGetTestSourceRootsFromDefaultSources() {
        assertEquals(1, testMojo.getTestSourceRoots().length);
    }

    @Test
    public void testGetTestSourceRootsFromDefaultSourcesIncludingJava() {
        assertEquals(1, testMojo.getTestSourceRoots(true).length);
    }

    @Test
    public void testGetTestSourcesFromDefaultSourcesEmpty() {
        assertEquals(new HashSet<File>(), testMojo.getTestSources());
    }

    @Test
    public void testGetTestSourcesFromDefaultSourcesIncludingJavaEmpty() {
        assertEquals(new HashSet<File>(), testMojo.getTestSources(true));
    }

    public class TestMojo extends AbstractGroovySourcesMojo {
        public void execute() throws MojoExecutionException, MojoFailureException { }
    }

}
