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

package org.codehaus.gmavenplus.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;


/**
 * Unit tests for the DotGroovyFile class.
 *
 * @author Keegan Witt
 */
public class DotGroovyFileTest {

    @Test
    public void testNotJava() {
        DotGroovyFile[] dotGroovyFiles = new DotGroovyFile[] {
                new DotGroovyFile("pathname.ext"),
                new DotGroovyFile("parent", "child.ext"),
                new DotGroovyFile(new File("parent"), "child.ext"),
                new DotGroovyFile(new File("filename.ext")),
                new DotGroovyFile(new File("filename.ext").toURI())
        };
        for (DotGroovyFile dotGroovyFile : dotGroovyFiles) {
            Assert.assertTrue(dotGroovyFile.getName() + " doesn't end with .groovy", dotGroovyFile.getName().endsWith(".groovy"));
        }
    }

    @Test
    public void testJava() {
        DotGroovyFile[] dotGroovyFiles = new DotGroovyFile[] {
                new DotGroovyFile("pathname.java"),
                new DotGroovyFile("parent", "child.java"),
                new DotGroovyFile(new File("parent"), "child.java"),
                new DotGroovyFile(new File("filename.java")),
                new DotGroovyFile(new File("filename.java").toURI())
        };
        for (DotGroovyFile dotGroovyFile : dotGroovyFiles) {
            Assert.assertTrue(dotGroovyFile.getName() + " ends with .groovy", !dotGroovyFile.getName().endsWith(".groovy"));
        }
    }
}
