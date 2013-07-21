/*
 * Copyright 2013 Keegan Witt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, softwaredistributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus.mojo;

import com.google.common.io.LineReader;
import org.apache.maven.model.Dependency;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
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
    @Spy
    private ExecuteMojo executeMojo;

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Before
    public void setup() {
        Dependency groovyDependency = Mockito.mock(Dependency.class);
        Mockito.doReturn(groovyDependency).when(executeMojo).getGroovyDependency();
        Mockito.doReturn("SOME_GROOVY_VERSION").when(executeMojo).getGroovyVersion();
    }

    @Test
    public void testScriptString() throws Exception {
        File file = tmpDir.newFile();
        String line = "hello world";
        executeMojo.scripts = new String[] { "new File('" + file.getAbsolutePath().replaceAll("\\\\", "/") + "').withWriter { w -> w << '" + line +"' }" };

        executeMojo.execute();

        LineReader lineReader = new LineReader(new BufferedReader(new FileReader(file)));
        String actualLine = lineReader.readLine();
        Assert.assertEquals(line, actualLine);
    }

    @Test
    public void testScriptPath() throws Exception {
        executeMojo.sourceEncoding = "UTF-8";
        File file = new File("target/testFile.txt");
        String line = "Hello world!";
        executeMojo.scripts = new String[] { new File("src/test/resources/testScript.groovy").toURI().toURL().toString() };

        String actualLine;
        try {
            executeMojo.execute();
        } finally {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            LineReader lineReader = new LineReader(reader);
            actualLine = lineReader.readLine();
            reader.close();
            file.delete();
        }

        Assert.assertEquals(line, actualLine);
    }

}
