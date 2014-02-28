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

package org.codehaus.gmavenplus;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class TheTest {

    @Test
    public void testClassesExist() {
        File generatedGroovydoc1 = new File("target/gapidocs/org/codehaus/gmavenplus/groovy/Child.html");
        Assert.assertTrue(generatedGroovydoc1 + " does not exist.", generatedGroovydoc1.exists());
        Assert.assertTrue(generatedGroovydoc1 + " is empty.", generatedGroovydoc1.length() > 0);
        File generatedGroovydoc2 = new File("target/gapidocs/org/codehaus/gmavenplus/java/Parent.html");
        Assert.assertTrue(generatedGroovydoc2 + " does not exist.", generatedGroovydoc2.exists());
        Assert.assertTrue(generatedGroovydoc2 + " is empty.", generatedGroovydoc2.length() > 0);
        File generatedGroovydoc3 = new File("target/gapidocs/org/codehaus/gmavenplus/java/GrandChild.html");
        Assert.assertTrue(generatedGroovydoc3 + " does not exist.", generatedGroovydoc3.exists());
        Assert.assertTrue(generatedGroovydoc3 + " is empty.", generatedGroovydoc3.length() > 0);
    }

    @Test
    public void testOverviewSummaryExists() {
        File generatedGroovydoc = new File("target/gapidocs/overview-summary.html");
        Assert.assertTrue(generatedGroovydoc + " does not exist.", generatedGroovydoc.exists());
        Assert.assertTrue(generatedGroovydoc + " is empty.", generatedGroovydoc.length() > 0);
    }

}
