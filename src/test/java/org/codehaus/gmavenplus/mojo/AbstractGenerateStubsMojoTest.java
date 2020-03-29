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
        doReturn(new String[]{"STUBBED_INCLUDES"}).when(fileSet).getIncludesArray();
        doReturn("STUBBED_STUBS_DIRECTORY").when(outputDirectory).getAbsolutePath();
        File mockBaseDir = mock(File.class);
        doReturn("STUBBED_BASEDIR").when(mockBaseDir).getAbsolutePath();
        doReturn(mockBaseDir).when(project).getBasedir();
        testMojo = new TestMojo();
        testMojo.project = project;
    }

    @Test
    public void testGetStubs() {
        Set<File> stubs = testMojo.getStubs(outputDirectory);
        assertEquals(0, stubs.size());
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

    @Test
    public void testResetStubModifiedDates() {
        File stub = mock(File.class);
        Set<File> stubs = new HashSet<>();
        stubs.add(stub);
        testMojo.resetStubModifiedDates(stubs);
        verify(stub, atLeastOnce()).setLastModified(anyLong());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava6WithUnsupportedGroovy() {
        testMojo = new TestMojo("2.1.2");
        testMojo.targetBytecode = "1.6";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava6WithSupportedGroovy() {
        testMojo = new TestMojo("2.1.3");
        testMojo.targetBytecode = "1.6";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava7WithUnsupportedGroovy() {
        testMojo = new TestMojo("2.1.2");
        testMojo.targetBytecode = "1.7";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava7WithSupportedGroovy() {
        testMojo = new TestMojo("2.1.3");
        testMojo.targetBytecode = "1.7";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava8WithUnsupportedGroovy() {
        testMojo = new TestMojo("2.3.2");
        testMojo.targetBytecode = "1.8";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava8WithSupportedGroovy() {
        testMojo = new TestMojo("2.3.3");
        testMojo.targetBytecode = "1.8";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovy2_5() {
        testMojo = new TestMojo("2.5.2");
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava9WithSupportedGroovy2_5() {
        testMojo = new TestMojo("2.5.3");
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovy2_6() {
        testMojo = new TestMojo("2.6.0-alpha-3");
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava9WithSupportedGroovy2_6() {
        testMojo = new TestMojo("2.6.0-alpha-4");
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-alpha-1");
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava9WithSupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-alpha-2");
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovyIndy() {
        testMojo = new TestMojo("2.5.2", true);
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava9WithSupportedGroovyIndy() {
        testMojo = new TestMojo("2.5.3", true);
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovy3Indy() {
        testMojo = new TestMojo("3.0.0-alpha-3", true);
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava9WithSupportedGroovy3Indy() {
        testMojo = new TestMojo("3.0.0-alpha-4", true);
        testMojo.targetBytecode = "9";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava10WithUnsupportedGroovy() {
        testMojo = new TestMojo("2.5.2");
        testMojo.targetBytecode = "10";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava10WithSupportedGroovy() {
        testMojo = new TestMojo("2.5.3");
        testMojo.targetBytecode = "10";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava10WithUnsupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-alpha-3");
        testMojo.targetBytecode = "10";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava10WithSupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-alpha-4");
        testMojo.targetBytecode = "10";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava11WithUnsupportedGroovy() {
        testMojo = new TestMojo("2.5.2");
        testMojo.targetBytecode = "11";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava11WithSupportedGroovy() {
        testMojo = new TestMojo("2.5.3");
        testMojo.targetBytecode = "11";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava11WithUnsupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-alpha-3");
        testMojo.targetBytecode = "11";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava11WithSupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-alpha-4");
        testMojo.targetBytecode = "11";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava12WithUnsupportedGroovy() {
        testMojo = new TestMojo("2.5.2");
        testMojo.targetBytecode = "12";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava12WithSupportedGroovy() {
        testMojo = new TestMojo("2.5.3");
        testMojo.targetBytecode = "12";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava12WithUnsupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-alpha-3");
        testMojo.targetBytecode = "12";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava12WithSupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-alpha-4");
        testMojo.targetBytecode = "12";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava13WithUnsupportedGroovy() {
        testMojo = new TestMojo("2.5.6");
        testMojo.targetBytecode = "13";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava13WithSupportedGroovy() {
        testMojo = new TestMojo("2.5.7");
        testMojo.targetBytecode = "13";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava13WithUnsupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-alpha-4");
        testMojo.targetBytecode = "13";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava13WithSupportedGroovy3() {
        testMojo = new TestMojo("3.0.0-beta-1");
        testMojo.targetBytecode = "13";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava14WithUnsupportedGroovy() {
        testMojo = new TestMojo("3.0.0-beta-1");
        testMojo.targetBytecode = "14";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava14WithSupportedGroovy() {
        testMojo = new TestMojo("3.0.0-beta-2");
        testMojo.targetBytecode = "14";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    protected static class TestMojo extends AbstractGenerateStubsMojo {
        protected TestMojo() {
            this(GROOVY_1_8_2.toString(), false);
        }

        protected TestMojo(String groovyVersion) {
            this(groovyVersion, false);
        }

        protected TestMojo(String groovyVersion, boolean indy) {
            classWrangler = mock(ClassWrangler.class);
            doReturn(Version.parseFromString(groovyVersion)).when(classWrangler).getGroovyVersion();
            doReturn(indy).when(classWrangler).isGroovyIndy();
        }

        @Override
        public void execute() {
        }
    }

}
