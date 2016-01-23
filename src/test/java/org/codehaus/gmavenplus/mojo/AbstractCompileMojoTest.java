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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for the AbstractCompileMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractCompileMojoTest {
    private TestMojo testMojo;

    @Mock
    private MavenProject project;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testMojo = new TestMojo();
    }

    @Test
    public void testGetSourcesEmpty() {
        testMojo.setSources(new FileSet[] {});
        Set<File> sources = testMojo.getSources();
        assertEquals(0, sources.size());
    }

    @Test
    public void testGetTestSourcesEmpty() {
        testMojo.setTestSources(new FileSet[] {});
        Set<File> testSources = testMojo.getTestSources();
        assertEquals(0, testSources.size());
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        testMojo = new TestMojo("1.5.0");
        assertTrue(testMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        testMojo = new TestMojo("1.1-rc-3");
        assertFalse(testMojo.groovyVersionSupportsAction());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava6WithOldGroovy() {
        testMojo = new TestMojo("2.1.2");
        testMojo.targetBytecode = "1.6";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava6WithNewerGroovy() {
        testMojo = new TestMojo("2.1.3");
        testMojo.targetBytecode = "1.6";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava7WithOldGroovy() {
        testMojo = new TestMojo("2.1.2");
        testMojo.targetBytecode = "1.7";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava7WithNewerGroovy() {
        testMojo = new TestMojo("2.1.3");
        testMojo.targetBytecode = "1.7";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava8WithOldGroovy() {
        testMojo = new TestMojo("2.3.2");
        testMojo.targetBytecode = "1.8";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava8WithNewerGroovy() {
        testMojo = new TestMojo("2.3.3");
        testMojo.targetBytecode = "1.8";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9() {
        testMojo = new TestMojo("2.4.3");
        testMojo.targetBytecode = "1.9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnrecognizedJava() {
        testMojo = new TestMojo("2.1.2");
        testMojo.targetBytecode = "unknown";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    public class TestMojo extends AbstractCompileMojo {
        private String overrideGroovyVersion = minGroovyVersion.toString();

        protected TestMojo() {
            classWrangler = mock(ClassWrangler.class);
            doReturn(Version.parseFromString(overrideGroovyVersion)).when(classWrangler).getGroovyVersion();
        }

        protected TestMojo(String newOverrideGroovyVersion) {
            overrideGroovyVersion = newOverrideGroovyVersion;
            classWrangler = mock(ClassWrangler.class);
            doReturn(Version.parseFromString(overrideGroovyVersion)).when(classWrangler).getGroovyVersion();
        }

        public void execute() throws MojoExecutionException, MojoFailureException { }

    }

}
