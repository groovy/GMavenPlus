/*
 * Copyright 2013 the original author or authors.
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;


/**
 * Unit tests for the AbstractGroovyStubSourcesMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGroovyStubSourcesMojoTest {
    private TestMojo testMojo;
    @Mock
    private File stubsOutputDirectory;
    @Mock
    private File testStubsOutputDirectory;
    private static final String PATH = "path";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testMojo = new TestMojo();
        doReturn(PATH).when(stubsOutputDirectory).getAbsolutePath();
        doReturn(PATH).when(testStubsOutputDirectory).getAbsolutePath();
        testMojo.stubsOutputDirectory = stubsOutputDirectory;
        testMojo.testStubsOutputDirectory = testStubsOutputDirectory;
    }

    @Test
    public void testGetStubsEmpty() {
        assertEquals(0, testMojo.getStubs().size());
    }

    @Test
    public void testGetTestStubsEmpty() {
        assertEquals(0, testMojo.getStubs().size());
    }

    public class TestMojo extends AbstractGroovyStubSourcesMojo {
        public void execute() throws MojoExecutionException, MojoFailureException { }
    }

}
