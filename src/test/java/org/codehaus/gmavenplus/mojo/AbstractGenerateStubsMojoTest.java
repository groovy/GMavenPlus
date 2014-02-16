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
import org.codehaus.gmavenplus.model.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.HashSet;
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
        Mockito.doReturn(new String[] {"STUBBED_INCLUDES"}).when(fileSet).getIncludesArray();
        Mockito.doReturn("STUBBED_STUBS_DIRECTORY").when(stubsOutputDirectory).getAbsolutePath();
        Mockito.doReturn("STUBBED_TEST_STUBS_DIRECTORY").when(testStubsOutputDirectory).getAbsolutePath();
        File mockBaseDir = Mockito.mock(File.class);
        Mockito.doReturn("STUBBED_BASEDIR").when(mockBaseDir).getAbsolutePath();
        Mockito.doReturn(mockBaseDir).when(project).getBasedir();
        testMojo = new TestMojo();
        testMojo.project = project;
        testMojo.setSources(new FileSet[] {});
        testMojo.setTestSources(new FileSet[] {});
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
        testMojo.setSources(null);
        Set<File> sources = testMojo.getSources();
        Assert.assertEquals(0, sources.size());
    }

    @Test
    public void testGetTestSourcesWithNullTestSources() {
        testMojo.setTestSources(null);
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
        testMojo = new TestMojo("1.8.2");
        Assert.assertTrue(testMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        testMojo = new TestMojo("1.8.1");
        Assert.assertFalse(testMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testResetStubModifiedDates() {
        File stub = Mockito.mock(File.class);
        Set<File> stubs = new HashSet<File>();
        stubs.add(stub);
        testMojo.resetStubModifiedDates(stubs);
        Mockito.verify(stub, Mockito.atLeastOnce()).setLastModified(Mockito.anyLong());
    }

    private class TestMojo extends AbstractGenerateStubsMojo {
        private String overrideGroovyVersion = minGroovyVersion.toString();

        protected TestMojo() {
            minGroovyVersion = new Version(1, 8, 2);
        }

        protected TestMojo(String overrideGroovyVersion) {
            minGroovyVersion = new Version(1, 8, 2);
            this.overrideGroovyVersion = overrideGroovyVersion;
        }

        protected Version getGroovyVersion() {
            return Version.parseFromString(overrideGroovyVersion);
        }

        public void execute() throws MojoExecutionException, MojoFailureException { }

    }

}
