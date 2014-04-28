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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.gmavenplus.model.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for the AbstractGroovyMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGroovyMojoTest {
    private AbstractGroovyMojo testMojo;

    @Before
    public void setup() throws Exception {
        testMojo = Mockito.spy(new TestGroovyMojo());
    }

    @Test
    public void testGetJavaVersion() {
        Assert.assertTrue(testMojo.getJavaVersionString() != null && testMojo.getJavaVersionString().length() != 0);
        Assert.assertNotNull(testMojo.getJavaVersion());
    }

    @Test
    public void testIsJavaSupportIndy() {
        Mockito.doReturn(Version.parseFromString("1.7.0_45")).when(testMojo).getJavaVersion();
        Assert.assertTrue(testMojo.isJavaSupportIndy());
    }

    @Test
    public void testIsJavaSupportIndyNo() {
        Mockito.doReturn(Version.parseFromString("1.6.0_45")).when(testMojo).getJavaVersion();
        Assert.assertFalse(testMojo.isJavaSupportIndy());
    }

    private static class TestGroovyMojo extends AbstractGroovyMojo {
        public void execute() throws MojoExecutionException, MojoFailureException { }
    }

}
