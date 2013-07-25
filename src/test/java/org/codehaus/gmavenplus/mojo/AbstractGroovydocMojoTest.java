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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.List;


/**
 * Unit tests for the AbstractGroovydocMojo class.
 *
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractGroovydocMojoTest {
    private TestMojo testMojo;

    @Mock
    private MavenProject project;

    @Mock
    private FileSet fileSet;

    @Before
    public void setup() {
        Mockito.doReturn("STUBBED_DIRECTORY").when(fileSet).getDirectory();
        Mockito.doReturn(new String[] { "STUBBED_INCLUDES" }).when(fileSet).getIncludesArray();
        File mockBaseDir = Mockito.mock(File.class);
        Mockito.doReturn("STUBBED_BASEDIR").when(mockBaseDir).getAbsolutePath();
        Mockito.doReturn(mockBaseDir).when(project).getBasedir();
        testMojo = new TestMojo();
        testMojo.project = project;
        testMojo.sources = new FileSet[] { };
        testMojo.testSources = new FileSet[] { };
    }

    @Test
    public void testGetSources() {
        List<String> sources = testMojo.getSources(fileSet);
        Assert.assertEquals(0, sources.size());
    }

    @Test
    public void testGetSourcesWithNullFileset() {
        List<String> sources = testMojo.getSources(null);
        Assert.assertEquals(0, sources.size());
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        testMojo = new TestMojo("1.6.3");
        Assert.assertTrue(testMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        testMojo = new TestMojo("1.6.1");
        Assert.assertFalse(testMojo.groovyVersionSupportsAction());
    }

    private class TestMojo extends AbstractGroovydocMojo {
        private String overrideGroovyVersion = MIN_GROOVY_VERSION.toString();

        private TestMojo() { }

        private TestMojo(String overrideGroovyVersion) {
            this.overrideGroovyVersion = overrideGroovyVersion;
        }

        protected String getGroovyVersion() {
            return overrideGroovyVersion;
        }

        public void execute() throws MojoExecutionException, MojoFailureException { }

    }

}
