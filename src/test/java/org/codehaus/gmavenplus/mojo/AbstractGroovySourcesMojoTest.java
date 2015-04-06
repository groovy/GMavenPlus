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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.HashSet;


/**
 * Unit tests for the AbstractGroovySourcesMojo class.
 *
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractGroovySourcesMojoTest {
    private TestMojo testMojo;
    @Mock
    private MavenProject project;
    @Mock
    private File basedir;

    @Before
    public void setup() {
        testMojo = new TestMojo();
        Mockito.doReturn(basedir).when(project).getBasedir();
        testMojo.project = project;
    }

    @Test
    public void testGetSourceRootsFromDefaultSources() {
        Assert.assertEquals(1, testMojo.getSourceRoots().length);
    }

    @Test
    public void testGetSourceRootsFromDefaultSourcesIncludingJava() {
        Assert.assertEquals(1, testMojo.getSourceRoots(true).length);
    }

    @Test
    public void testGetSourcesFromDefaultSourcesEmpty() {
        Assert.assertEquals(new HashSet<File>(), testMojo.getSources());
    }

    @Test
    public void testGetSourcesFromDefaultSourcesIncludingJavaEmpty() {
        Assert.assertEquals(new HashSet<File>(), testMojo.getSources(true));
    }

    @Test
    public void testGetTestSourceRootsFromDefaultSources() {
        Assert.assertEquals(1, testMojo.getTestSourceRoots().length);
    }

    @Test
    public void testGetTestSourceRootsFromDefaultSourcesIncludingJava() {
        Assert.assertEquals(1, testMojo.getTestSourceRoots(true).length);
    }

    @Test
    public void testGetTestSourcesFromDefaultSourcesEmpty() {
        Assert.assertEquals(new HashSet<File>(), testMojo.getTestSources());
    }

    @Test
    public void testGetTestSourcesFromDefaultSourcesIncludingJavaEmpty() {
        Assert.assertEquals(new HashSet<File>(), testMojo.getTestSources(true));
    }

    public class TestMojo extends AbstractGroovySourcesMojo {
        public void execute() throws MojoExecutionException, MojoFailureException { }
    }

}
