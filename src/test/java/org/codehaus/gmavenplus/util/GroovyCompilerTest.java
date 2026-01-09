/*
 * Copyright (C) 2025 the original author or authors.
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

package org.codehaus.gmavenplus.util;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.gmavenplus.model.internal.Version;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

/**
 * Unit tests for the GroovyCompiler class.
 */
public class GroovyCompilerTest {

    @Mock
    private Log log;

    @Mock
    private ClassWrangler classWrangler;

    private TestGroovyCompiler compiler;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private void setupCompiler(String groovyVersion) {
        doReturn(Version.parseFromString(groovyVersion)).when(classWrangler).getGroovyVersion();
        doReturn(false).when(classWrangler).isGroovyIndy();
        compiler = new TestGroovyCompiler(classWrangler, log);
    }

    private void setupCompiler(String groovyVersion, boolean indy) {
        doReturn(Version.parseFromString(groovyVersion)).when(classWrangler).getGroovyVersion();
        doReturn(indy).when(classWrangler).isGroovyIndy();
        compiler = new TestGroovyCompiler(classWrangler, log);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava6WithUnsupportedGroovy() {
        setupCompiler("2.1.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("1.6");
    }

    @Test
    public void testJava6WithSupportedGroovy() {
        setupCompiler("2.1.3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("1.6");
        compiler.verifyGroovyVersionSupportsTargetBytecode("6");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava7WithUnsupportedGroovy() {
        setupCompiler("2.1.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("1.7");
    }

    @Test
    public void testJava7WithSupportedGroovy() {
        setupCompiler("2.1.3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("1.7");
        compiler.verifyGroovyVersionSupportsTargetBytecode("7");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava8WithUnsupportedGroovy() {
        setupCompiler("2.3.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("1.8");
    }

    @Test
    public void testJava8WithSupportedGroovy() {
        setupCompiler("2.3.3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("1.8");
        compiler.verifyGroovyVersionSupportsTargetBytecode("8");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovy2_5() {
        setupCompiler("2.5.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
    }

    @Test
    public void testJava9WithSupportedGroovy2_5() {
        setupCompiler("2.5.3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
        compiler.verifyGroovyVersionSupportsTargetBytecode("1.9");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovy2_6() {
        setupCompiler("2.6.0-alpha-3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
    }

    @Test
    public void testJava9WithSupportedGroovy2_6() {
        setupCompiler("2.6.0-alpha-4");
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovy3() {
        setupCompiler("3.0.0-alpha-1");
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
    }

    @Test
    public void testJava9WithSupportedGroovy3() {
        setupCompiler("3.0.0-alpha-2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovyIndy() {
        setupCompiler("2.5.2", true);
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
    }

    @Test
    public void testJava9WithSupportedGroovyIndy() {
        setupCompiler("2.5.3", true);
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava9WithUnsupportedGroovy3Indy() {
        setupCompiler("3.0.0-alpha-3", true);
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
    }

    @Test
    public void testJava9WithSupportedGroovy3Indy() {
        setupCompiler("3.0.0-alpha-4", true);
        compiler.verifyGroovyVersionSupportsTargetBytecode("9");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava10WithUnsupportedGroovy() {
        setupCompiler("2.5.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("10");
    }

    @Test
    public void testJava10WithSupportedGroovy() {
        setupCompiler("2.5.3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("10");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava10WithUnsupportedGroovy3() {
        setupCompiler("3.0.0-alpha-3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("10");
    }

    @Test
    public void testJava10WithSupportedGroovy3() {
        setupCompiler("3.0.0-alpha-4");
        compiler.verifyGroovyVersionSupportsTargetBytecode("10");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava11WithUnsupportedGroovy() {
        setupCompiler("2.5.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("11");
    }

    @Test
    public void testJava11WithSupportedGroovy() {
        setupCompiler("2.5.3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("11");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava11WithUnsupportedGroovy3() {
        setupCompiler("3.0.0-alpha-3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("11");
    }

    @Test
    public void testJava11WithSupportedGroovy3() {
        setupCompiler("3.0.0-alpha-4");
        compiler.verifyGroovyVersionSupportsTargetBytecode("11");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava12WithUnsupportedGroovy() {
        setupCompiler("2.5.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("12");
    }

    @Test
    public void testJava12WithSupportedGroovy() {
        setupCompiler("2.5.3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("12");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava12WithUnsupportedGroovy3() {
        setupCompiler("3.0.0-alpha-3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("12");
    }

    @Test
    public void testJava12WithSupportedGroovy3() {
        setupCompiler("3.0.0-alpha-4");
        compiler.verifyGroovyVersionSupportsTargetBytecode("12");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava13WithUnsupportedGroovy() {
        setupCompiler("2.5.6");
        compiler.verifyGroovyVersionSupportsTargetBytecode("13");
    }

    @Test
    public void testJava13WithSupportedGroovy() {
        setupCompiler("2.5.7");
        compiler.verifyGroovyVersionSupportsTargetBytecode("13");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava13WithUnsupportedGroovy3() {
        setupCompiler("3.0.0-alpha-4");
        compiler.verifyGroovyVersionSupportsTargetBytecode("13");
    }

    @Test
    public void testJava13WithSupportedGroovy3() {
        setupCompiler("3.0.0-beta-1");
        compiler.verifyGroovyVersionSupportsTargetBytecode("13");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava14WithUnsupportedGroovy() {
        setupCompiler("3.0.0-beta-1");
        compiler.verifyGroovyVersionSupportsTargetBytecode("14");
    }

    @Test
    public void testJava14WithSupportedGroovy() {
        setupCompiler("3.0.0-beta-2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("14");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava15WithUnsupportedGroovy() {
        setupCompiler("3.0.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("15");
    }

    @Test
    public void testJava15WithSupportedGroovy() {
        setupCompiler("3.0.3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("15");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava16WithUnsupportedGroovy() {
        setupCompiler("3.0.5");
        compiler.verifyGroovyVersionSupportsTargetBytecode("16");
    }

    @Test
    public void testJava16WithSupportedGroovy() {
        setupCompiler("3.0.6");
        compiler.verifyGroovyVersionSupportsTargetBytecode("16");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava17WithUnsupportedGroovy() {
        setupCompiler("3.0.7");
        compiler.verifyGroovyVersionSupportsTargetBytecode("17");
    }

    @Test
    public void testJava17WithSupportedGroovy() {
        setupCompiler("3.0.8");
        compiler.verifyGroovyVersionSupportsTargetBytecode("17");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava17WithUnsupportedGroovy4() {
        setupCompiler("4.0.0-alpha-2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("17");
    }

    @Test
    public void testJava17WithSupportedGroovy4() {
        setupCompiler("4.0.0-alpha-3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("17");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava18WithUnsupportedGroovy() {
        setupCompiler("4.0.0-alpha-3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("18");
    }

    @Test
    public void testJava18WithSupportedGroovy() {
        setupCompiler("4.0.0-beta-1");
        compiler.verifyGroovyVersionSupportsTargetBytecode("18");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava19WithUnsupportedGroovy() {
        setupCompiler("4.0.1");
        compiler.verifyGroovyVersionSupportsTargetBytecode("19");
    }

    @Test
    public void testJava19WithSupportedGroovy() {
        setupCompiler("4.0.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("19");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava20WithUnsupportedGroovy() {
        setupCompiler("4.0.5");
        compiler.verifyGroovyVersionSupportsTargetBytecode("20");
    }

    @Test
    public void testJava20WithSupportedGroovy() {
        setupCompiler("4.0.6");
        compiler.verifyGroovyVersionSupportsTargetBytecode("20");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava21WithUnsupportedGroovy() {
        setupCompiler("4.0.10");
        compiler.verifyGroovyVersionSupportsTargetBytecode("21");
    }

    @Test
    public void testJava21WithSupportedGroovy() {
        setupCompiler("4.0.11");
        compiler.verifyGroovyVersionSupportsTargetBytecode("21");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava22WithUnsupportedGroovy() {
        setupCompiler("4.0.15");
        compiler.verifyGroovyVersionSupportsTargetBytecode("22");
    }

    @Test
    public void testJava22WithSupportedGroovy() {
        setupCompiler("4.0.16");
        compiler.verifyGroovyVersionSupportsTargetBytecode("22");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava22WithUnsupportedGroovy5() {
        setupCompiler("5.0.0-alpha-2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("22");
    }

    @Test
    public void testJava22WithSupportedGroovy5() {
        setupCompiler("5.0.0-alpha-3");
        compiler.verifyGroovyVersionSupportsTargetBytecode("22");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava23WithUnsupportedGroovy() {
        setupCompiler("4.0.20");
        compiler.verifyGroovyVersionSupportsTargetBytecode("23");
    }

    @Test
    public void testJava23WithSupportedGroovy() {
        setupCompiler("4.0.21");
        compiler.verifyGroovyVersionSupportsTargetBytecode("23");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava23WithUnsupportedGroovy5() {
        setupCompiler("5.0.0-alpha-7");
        compiler.verifyGroovyVersionSupportsTargetBytecode("23");
    }

    @Test
    public void testJava23WithSupportedGroovy5() {
        setupCompiler("5.0.0-alpha-8");
        compiler.verifyGroovyVersionSupportsTargetBytecode("23");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava24WithUnsupportedGroovy() {
        setupCompiler("4.0.23");
        compiler.verifyGroovyVersionSupportsTargetBytecode("24");
    }

    @Test
    public void testJava24WithSupportedGroovy() {
        setupCompiler("4.0.24");
        compiler.verifyGroovyVersionSupportsTargetBytecode("24");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava24WithUnsupportedGroovy5() {
        setupCompiler("5.0.0-alpha-10");
        compiler.verifyGroovyVersionSupportsTargetBytecode("24");
    }

    @Test
    public void testJava24WithSupportedGroovy5() {
        setupCompiler("5.0.0-alpha-11");
        compiler.verifyGroovyVersionSupportsTargetBytecode("24");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava25WithUnsupportedGroovy() {
        setupCompiler("4.0.26");
        compiler.verifyGroovyVersionSupportsTargetBytecode("25");
    }

    @Test
    public void testJava25WithSupportedGroovy() {
        setupCompiler("4.0.27");
        compiler.verifyGroovyVersionSupportsTargetBytecode("25");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJava25WithUnsupportedGroovy5() {
        setupCompiler("5.0.0-alpha-12");
        compiler.verifyGroovyVersionSupportsTargetBytecode("25");
    }

    @Test
    public void testJava25WithSupportedGroovy5() {
        setupCompiler("5.0.0-alpha-13");
        compiler.verifyGroovyVersionSupportsTargetBytecode("25");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnrecognizedJava() {
        setupCompiler("2.1.2");
        compiler.verifyGroovyVersionSupportsTargetBytecode("unknown");
    }

    @Test
    public void testBytecodeTranslation() {
        Map<String, String> expectedTranslations = new LinkedHashMap<>();
        expectedTranslations.put("5", "1.5");
        expectedTranslations.put("6", "1.6");
        expectedTranslations.put("7", "1.7");
        expectedTranslations.put("8", "1.8");
        expectedTranslations.put("1.9", "9");
        for (Map.Entry<String, String> entry : expectedTranslations.entrySet()) {
            String javacVersion = entry.getKey();
            String expectedGroovycVersion = entry.getValue();
            assertEquals(expectedGroovycVersion, GroovyCompiler.translateJavacTargetToTargetBytecode(javacVersion));
        }
    }

    protected static class TestGroovyCompiler extends GroovyCompiler {
        public TestGroovyCompiler(ClassWrangler classWrangler, Log log) {
            super(classWrangler, log);
        }

        @Override
        public void verifyGroovyVersionSupportsTargetBytecode(String targetBytecode) {
           super.verifyGroovyVersionSupportsTargetBytecode(targetBytecode);
        }
    }
}
