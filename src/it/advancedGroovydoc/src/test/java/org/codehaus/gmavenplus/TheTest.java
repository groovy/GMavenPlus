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

package org.codehaus.gmavenplus;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class TheTest {

    @Test
    public void testSomeClassExists() {
        File generatedGroovydoc = new File("target/gapidocs/org/codehaus/gmavenplus/SomeClass.html");
        Assert.assertTrue(generatedGroovydoc + " does not exist.", generatedGroovydoc.exists());
        Assert.assertTrue(generatedGroovydoc + " is empty.", generatedGroovydoc.length() > 0);
    }

    @Test
    public void testSomeOtherClassExists() {
        File generatedGroovydoc = new File("target/gapidocs/org/codehaus/gmavenplus/SomeOtherClass.html");
        Assert.assertTrue(generatedGroovydoc + " does not exist.", generatedGroovydoc.exists());
        Assert.assertTrue(generatedGroovydoc + " is empty.", generatedGroovydoc.length() > 0);
    }

}
