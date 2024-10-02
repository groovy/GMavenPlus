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
import org.codehaus.gmavenplus.model.internal.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


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
        MockitoAnnotations.openMocks(this);
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

    @Test(expected = IllegalArgumentException.class)
    public void testJava15WithUnsupportedGroovy() {
        testMojo = new TestMojo("3.0.2");
        testMojo.targetBytecode = "15";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava15WithSupportedGroovy() {
        testMojo = new TestMojo("3.0.3");
        testMojo.targetBytecode = "15";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava16WithUnsupportedGroovy() {
        testMojo = new TestMojo("3.0.5");
        testMojo.targetBytecode = "16";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava16WithSupportedGroovy() {
        testMojo = new TestMojo("3.0.6");
        testMojo.targetBytecode = "16";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava17WithUnsupportedGroovy() {
        testMojo = new TestMojo("3.0.7");
        testMojo.targetBytecode = "17";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava17WithSupportedGroovy() {
        testMojo = new TestMojo("3.0.8");
        testMojo.targetBytecode = "17";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava17WithUnsupportedGroovy4() {
        testMojo = new TestMojo("4.0.0-alpha-2");
        testMojo.targetBytecode = "17";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava17WithSupportedGroovy4() {
        testMojo = new TestMojo("4.0.0-alpha-3");
        testMojo.targetBytecode = "17";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava18WithUnsupportedGroovy() {
        testMojo = new TestMojo("4.0.0-alpha-3");
        testMojo.targetBytecode = "18";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava18WithSupportedGroovy() {
        testMojo = new TestMojo("4.0.0-beta-1");
        testMojo.targetBytecode = "18";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava19WithUnsupportedGroovy() {
        testMojo = new TestMojo("4.0.1");
        testMojo.targetBytecode = "19";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava19WithSupportedGroovy() {
        testMojo = new TestMojo("4.0.2");
        testMojo.targetBytecode = "19";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava20WithUnsupportedGroovy() {
        testMojo = new TestMojo("4.0.5");
        testMojo.targetBytecode = "20";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava20WithSupportedGroovy() {
        testMojo = new TestMojo("4.0.6");
        testMojo.targetBytecode = "20";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava21WithUnsupportedGroovy() {
        testMojo = new TestMojo("4.0.10");
        testMojo.targetBytecode = "21";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava21WithSupportedGroovy() {
        testMojo = new TestMojo("4.0.11");
        testMojo.targetBytecode = "21";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava22WithUnsupportedGroovy() {
        testMojo = new TestMojo("4.0.15");
        testMojo.targetBytecode = "22";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava22WithSupportedGroovy() {
        testMojo = new TestMojo("4.0.16");
        testMojo.targetBytecode = "22";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava23WithUnsupportedGroovy() {
        testMojo = new TestMojo("4.0.20");
        testMojo.targetBytecode = "23";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test
    public void testJava23WithSupportedGroovy() {
        testMojo = new TestMojo("4.0.21");
        testMojo.targetBytecode = "23";
        testMojo.verifyGroovyVersionSupportsTargetBytecode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnrecognizedJava() {
        testMojo = new TestMojo("2.1.2");
        testMojo.targetBytecode = "unknown";
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
