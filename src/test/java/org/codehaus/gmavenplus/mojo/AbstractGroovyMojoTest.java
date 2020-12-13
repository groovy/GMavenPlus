/*
 * Copyright (C) 2011 the original author or authors.
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

import org.codehaus.gmavenplus.model.internal.Version;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;


/**
 * Unit tests for the AbstractGroovyMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGroovyMojoTest {
    private AbstractGroovyMojo testMojo;

    @Before
    public void setup() {
        testMojo = spy(new TestGroovyMojo());
    }

    @Test
    public void testGetJavaVersion() {
        assertTrue(testMojo.getJavaVersionString() != null && testMojo.getJavaVersionString().length() != 0);
        assertNotNull(testMojo.getJavaVersion());
    }

    @Test
    public void testIsJavaSupportIndy() {
        doReturn(Version.parseFromString("1.7.0_45")).when(testMojo).getJavaVersion();
        assertTrue(testMojo.isJavaSupportIndy());
    }

    @Test
    public void testIsJavaSupportIndyNo() {
        doReturn(Version.parseFromString("1.6.0_45")).when(testMojo).getJavaVersion();
        assertFalse(testMojo.isJavaSupportIndy());
    }

    @Test
    public void testIsJavaSupportPreviewFeatures() {
        doReturn(Version.parseFromString("12.0.1")).when(testMojo).getJavaVersion();
        assertTrue(testMojo.isJavaSupportPreviewFeatures());
    }

    @Test
    public void testIsJavaSupportPreviewFeaturesNo() {
        doReturn(Version.parseFromString("11.0.3")).when(testMojo).getJavaVersion();
        assertFalse(testMojo.isJavaSupportPreviewFeatures());
    }

    public static class TestGroovyMojo extends AbstractGroovyMojo {
        @Override
        public void execute() {
        }
    }

}
