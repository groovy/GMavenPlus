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
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the AbstractCompileMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGenerateStubsMojoTest {
    private TestMojo testMojo;

    @Mock
    private MavenProject project;

    @Mock
    private FileSet fileSet;

    @Mock
    private File outputDirectory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn("STUBBED_DIRECTORY").when(fileSet).getDirectory();
        doReturn(new String[] {"STUBBED_INCLUDES"}).when(fileSet).getIncludesArray();
        doReturn("STUBBED_STUBS_DIRECTORY").when(outputDirectory).getAbsolutePath();
        File mockBaseDir = mock(File.class);
        doReturn("STUBBED_BASEDIR").when(mockBaseDir).getAbsolutePath();
        doReturn(mockBaseDir).when(project).getBasedir();
        testMojo = new TestMojo();
        testMojo.project = project;
        testMojo.sources = new FileSet[] {};
        testMojo.testSources = new FileSet[] {};
    }

    @Test
    public void testGetSources() {
        Set<File> sources = testMojo.getSources();
        assertEquals(0, sources.size());
    }

    @Test
    public void testGetTestSources() {
        Set<File> testSources = testMojo.getTestSources();
        assertEquals(0, testSources.size());
    }

    @Test
    public void testGetSourcesWithNullSources() {
        testMojo.sources = null;
        Set<File> sources = testMojo.getSources();
        assertEquals(0, sources.size());
    }

    @Test
    public void testGetTestSourcesWithNullTestSources() {
        testMojo.testSources = null;
        Set<File> testSources = testMojo.getTestSources();
        assertEquals(0, testSources.size());
    }

    @Test
    public void testGetStubs() {
        Set<File> stubs = testMojo.getStubs(outputDirectory);
        assertEquals(0, stubs.size());
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        testMojo = new TestMojo("1.8.2");
        assertTrue(testMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        testMojo = new TestMojo("1.8.1");
        assertFalse(testMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testResetStubModifiedDates() {
        File stub = mock(File.class);
        Set<File> stubs = new HashSet<File>();
        stubs.add(stub);
        testMojo.resetStubModifiedDates(stubs);
        verify(stub, atLeastOnce()).setLastModified(anyLong());
    }

    public class TestMojo extends AbstractGenerateStubsMojo {
        private String overrideGroovyVersion = minGroovyVersion.toString();

        protected TestMojo() {
            minGroovyVersion = new Version(1, 8, 2);
            classWrangler = mock(ClassWrangler.class);
            doReturn(Version.parseFromString(overrideGroovyVersion)).when(classWrangler).getGroovyVersion();
        }

        protected TestMojo(String newOverrideGroovyVersion) {
            minGroovyVersion = new Version(1, 8, 2);
            overrideGroovyVersion = newOverrideGroovyVersion;
            classWrangler = mock(ClassWrangler.class);
            doReturn(Version.parseFromString(overrideGroovyVersion)).when(classWrangler).getGroovyVersion();
        }

        public void execute() throws MojoExecutionException, MojoFailureException { }

    }

}
