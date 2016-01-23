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
        File generatedGroovyDoc1 = new File("target/gapidocs/org/codehaus/gmavenplus/groovy/Child.html");
        Assert.assertTrue(generatedGroovyDoc1 + " does not exist.", generatedGroovyDoc1.exists());
        Assert.assertTrue(generatedGroovyDoc1 + " is empty.", generatedGroovyDoc1.length() > 0);
        File generatedGroovyDoc2 = new File("target/gapidocs/org/codehaus/gmavenplus/java/Parent.html");
        Assert.assertTrue(generatedGroovyDoc2 + " does not exist.", generatedGroovyDoc2.exists());
        Assert.assertTrue(generatedGroovyDoc2 + " is empty.", generatedGroovyDoc2.length() > 0);
        File generatedGroovyDoc3 = new File("target/gapidocs/org/codehaus/gmavenplus/java/Grandchild.html");
        Assert.assertTrue(generatedGroovyDoc3 + " does not exist.", generatedGroovyDoc3.exists());
        Assert.assertTrue(generatedGroovyDoc3 + " is empty.", generatedGroovyDoc3.length() > 0);
    }

    @Test
    public void testOverviewSummaryExists() {
        File generatedGroovyDoc = new File("target/gapidocs/overview-summary.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
    }

}
