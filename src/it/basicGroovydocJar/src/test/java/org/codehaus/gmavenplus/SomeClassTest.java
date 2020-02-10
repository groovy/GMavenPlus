/*
 * Copyright (C) 2019 the original author or authors.
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

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class SomeClassTest {

    @Test
    public void testGroovyDocExists() {
        boolean foundGroovydocDir = false;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile("target/gmavenplus-plugin-it-basicGroovyDocJar-testing-groovydoc.jar");
            Enumeration<? extends ZipEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                String entryName = entry.getName();
                if ("org/codehaus/gmavenplus/SomeClass.html".equals(entryName)) {
                    foundGroovydocDir = true;
                    break;
                }
            }
        } catch (IOException ioe) {
            System.err.println("Error opening zip file" + ioe);
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing zip file" + ioe);
            }
        }
        Assert.assertTrue(foundGroovydocDir);
    }

}
