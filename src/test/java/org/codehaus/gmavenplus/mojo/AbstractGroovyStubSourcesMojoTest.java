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
    private File outputDirectory;
    private static final String PATH = "path";

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testMojo = new TestMojo();
        doReturn(PATH).when(outputDirectory).getAbsolutePath();
    }

    @Test
    public void testGetStubsEmpty() {
        assertEquals(0, testMojo.getStubs(outputDirectory).size());
    }

    protected static class TestMojo extends AbstractGroovyStubSourcesMojo {
        @Override
        public void execute() {
        }
    }

}
