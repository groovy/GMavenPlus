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

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.codehaus.gmavenplus.util.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


/**
 * Unit tests for the ExecuteMojo class.
 *
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class ExecuteMojoTest {
    private ExecuteMojo executeMojo;

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Before
    public void setup() {
        executeMojo = new ExecuteMojo();
        executeMojo.mojoExecution = Mockito.mock(MojoExecution.class);
        MojoDescriptor mockMojoDescriptor = Mockito.mock(MojoDescriptor.class);
        Mockito.doReturn(mockMojoDescriptor).when(executeMojo.mojoExecution).getMojoDescriptor();
        Mockito.doReturn("execute").when(mockMojoDescriptor).getGoal();
    }

    @Test
    public void testScriptString() throws Exception {
        File file = tmpDir.newFile();
        String line = "hello world";
        executeMojo.scripts = new String[] {"new File('" + file.getAbsolutePath().replaceAll("\\\\", "/") + "').withWriter { w -> w << '" + line +"' }"};

        executeMojo.execute();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String actualLine = reader.readLine();
        FileUtils.closeQuietly(reader);

        Assert.assertEquals(line, actualLine);
    }

    @Test
    public void testScriptPath() throws Exception {
        executeMojo.sourceEncoding = "UTF-8";
        File file = new File("target/testFile.txt");
        String line = "Hello world!";
        executeMojo.scripts = new String[] {new File("src/test/resources/testScript.groovy").toURI().toURL().toString()};

        String actualLine;
        try {
            executeMojo.execute();
        } finally {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            actualLine = reader.readLine();
            FileUtils.closeQuietly(reader);
            file.delete();
        }

        Assert.assertEquals(line, actualLine);
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        executeMojo.classWrangler = Mockito.mock(ClassWrangler.class);
        Mockito.doReturn(Version.parseFromString("1.5.0")).when(executeMojo.classWrangler).getGroovyVersion();
        Assert.assertTrue(executeMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        executeMojo.classWrangler = Mockito.mock(ClassWrangler.class);
        Mockito.doReturn(Version.parseFromString("1.0")).when(executeMojo.classWrangler).getGroovyVersion();
        Assert.assertFalse(executeMojo.groovyVersionSupportsAction());
    }

}
