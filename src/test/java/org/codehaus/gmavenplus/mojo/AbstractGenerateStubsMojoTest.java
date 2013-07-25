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
import java.util.Set;


/**
 * Unit tests for the AbstractCompileMojo class.
 *
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractGenerateStubsMojoTest {
    private TestMojo testMojo;

    @Mock
    private MavenProject project;

    @Mock
    private FileSet fileSet;

    @Mock
    private File testStubsOutputDirectory;

    @Mock
    private File stubsOutputDirectory;

    @Before
    public void setup() {
        Mockito.doReturn("STUBBED_DIRECTORY").when(fileSet).getDirectory();
        Mockito.doReturn(new String[] { "STUBBED_INCLUDES" }).when(fileSet).getIncludesArray();
        Mockito.doReturn("STUBBED_STUBS_DIRECTORY").when(stubsOutputDirectory).getAbsolutePath();
        Mockito.doReturn("STUBBED_TEST_STUBS_DIRECTORY").when(testStubsOutputDirectory).getAbsolutePath();
        File mockBaseDir = Mockito.mock(File.class);
        Mockito.doReturn("STUBBED_BASEDIR").when(mockBaseDir).getAbsolutePath();
        Mockito.doReturn(mockBaseDir).when(project).getBasedir();
        testMojo = new TestMojo();
        testMojo.project = project;
        testMojo.sources = new FileSet[] { };
        testMojo.testSources = new FileSet[] { };
        testMojo.stubsOutputDirectory = stubsOutputDirectory;
        testMojo.testStubsOutputDirectory = testStubsOutputDirectory;
    }

    @Test
    public void testGetSources() {
        Set<File> sources = testMojo.getSources();
        Assert.assertEquals(0, sources.size());
    }

    @Test
    public void testGetTestSources() {
        Set<File> testSources = testMojo.getTestSources();
        Assert.assertEquals(0, testSources.size());
    }

    @Test
    public void testGetSourcesWithNullSources() {
        testMojo.sources = null;
        Set<File> sources = testMojo.getSources();
        Assert.assertEquals(0, sources.size());
    }

    @Test
    public void testGetTestSourcesWithNullTestSources() {
        testMojo.testSources = null;
        Set<File> testSources = testMojo.getTestSources();
        Assert.assertEquals(0, testSources.size());
    }

    @Test
    public void testGetStubs() {
        Set<File> stubs = testMojo.getStubs();
        Assert.assertEquals(0, stubs.size());
    }

    @Test
    public void testGetTestStubs() {
        Set<File> testStubs = testMojo.getTestStubs();
        Assert.assertEquals(0, testStubs.size());
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        testMojo = new TestMojo("1.7.0");
        Assert.assertTrue(testMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        testMojo = new TestMojo("1.6.9");
        Assert.assertFalse(testMojo.groovyVersionSupportsAction());
    }

    private class TestMojo extends AbstractGenerateStubsMojo {
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
