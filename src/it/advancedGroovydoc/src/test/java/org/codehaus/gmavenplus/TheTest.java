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

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class TheTest {

    @Test
    public void testSomeClassExistsAndContainsJavaLinks() throws Exception {
        File generatedGroovyDoc = new File("target/gapidocs/org/codehaus/gmavenplus/SomeClass.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
        Assert.assertTrue(generatedGroovyDoc + " does not contain Java 5 links.", readFileToString(generatedGroovyDoc)
                .contains("http://docs.oracle.com/javase/5/docs/api/"));
    }

    @Test
    public void testSomeOtherClassExistsAndContainsJavaLinks() throws Exception {
        File generatedGroovyDoc = new File("target/gapidocs/org/codehaus/gmavenplus/SomeOtherClass.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
        Assert.assertTrue(generatedGroovyDoc + " does not contain Java 5 links.", readFileToString(generatedGroovyDoc)
                .contains("http://docs.oracle.com/javase/5/docs/api/"));
    }

    @Test
    public void testOverviewSummaryExistsAndContainsStylesheetLink() throws Exception {
        File generatedGroovyDoc = new File("target/gapidocs/overview-summary.html");
        Assert.assertTrue(generatedGroovyDoc + " does not exist.", generatedGroovyDoc.exists());
        Assert.assertTrue(generatedGroovyDoc + " is empty.", generatedGroovyDoc.length() > 0);
        Assert.assertTrue(generatedGroovyDoc + " does not contain stylesheet link.", readFileToString(generatedGroovyDoc)
                .contains("<link rel =\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\" title=\"Style\">"));
    }

    private String readFileToString(File file) throws IOException {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            return scanner.useDelimiter("\\A").next();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
