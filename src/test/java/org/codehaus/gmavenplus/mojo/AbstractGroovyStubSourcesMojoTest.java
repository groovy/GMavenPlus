/*
 * Copyright 2013 Keegan Witt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;


/**
 * Unit tests for the AbstractGroovyStubSourcesMojo class.
 *
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractGroovyStubSourcesMojoTest {
    private TestMojo testMojo;
    @Mock
    private File stubsOutputDirectory;
    @Mock
    private File testStubsOutputDirectory;
    private static final String PATH = "path";

    @Before
    public void setup() {
        testMojo = new TestMojo();
        Mockito.when(stubsOutputDirectory.getAbsolutePath()).thenReturn(PATH);
        Mockito.when(testStubsOutputDirectory.getAbsolutePath()).thenReturn(PATH);
        testMojo.stubsOutputDirectory = stubsOutputDirectory;
        testMojo.testStubsOutputDirectory = testStubsOutputDirectory;
    }

    @Test
    public void testGetStubsEmpty() {
        Assert.assertEquals(0, testMojo.getStubs().size());
    }

    @Test
    public void testGetTestStubsEmpty() {
        Assert.assertEquals(0, testMojo.getStubs().size());
    }

    private class TestMojo extends AbstractGroovyStubSourcesMojo {
        public void execute() throws MojoExecutionException, MojoFailureException { }
    }

}
