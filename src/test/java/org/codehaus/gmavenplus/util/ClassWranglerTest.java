/*
 * Copyright (C) 2014 the original author or authors.
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

import org.apache.maven.plugin.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for the ClassWrangler class.
 *
 * @author Keegan Witt
 */
public class ClassWranglerTest {

    @Test
    public void testIsGroovyIndy() {
        String groovyVersion = "2.3.0-indy";
        ClassWrangler classWrangler = new ClassWrangler(Mockito.mock(ClassLoader.class), Mockito.mock(Log.class));
        classWrangler.groovyVersion = groovyVersion;
        Assert.assertTrue(classWrangler.isGroovyIndy());
        Assert.assertEquals(groovyVersion, classWrangler.getGroovyVersionString());
    }

    @Test
    public void testIsGroovyNotIndy() {
        String groovyVersion = "2.3.0";
        ClassWrangler classWrangler = new ClassWrangler(Mockito.mock(ClassLoader.class), Mockito.mock(Log.class));
        classWrangler.groovyVersion = groovyVersion;
        Assert.assertFalse(classWrangler.isGroovyIndy());
        Assert.assertEquals(groovyVersion, classWrangler.getGroovyVersionString());
    }

    @Test
    public void testGetGroovyJar() throws Exception {
        ClassWrangler classWrangler = Mockito.spy(new ClassWrangler(Mockito.mock(ClassLoader.class), Mockito.mock(Log.class)));
        Mockito.doReturn("some/path/groovy-all-1.5.0.jar").when(classWrangler).getJarPath();
        Assert.assertEquals("groovy-all-1.5.0.jar", classWrangler.getGroovyJar());
    }

    @Test
    public void testGetGroovyVersionString() throws Exception {
        ClassWrangler classWrangler = Mockito.spy(new ClassWrangler(Mockito.mock(ClassLoader.class), Mockito.mock(Log.class)));
        Mockito.doReturn("some/path/groovy-all-1.5.0.jar").when(classWrangler).getJarPath();
        Assert.assertEquals("1.5.0", classWrangler.getGroovyVersionString());
    }

    @Test
    public void testIsGroovyIndyTrue() throws Exception {
        ClassWrangler classWrangler = Mockito.spy(new ClassWrangler(Mockito.mock(ClassLoader.class), Mockito.mock(Log.class)));
        Mockito.doReturn("some/path/groovy-all-1.5.0-indy.jar").when(classWrangler).getJarPath();
        Assert.assertTrue(classWrangler.isGroovyIndy());
    }

    @Test
    public void testIsGroovyIndyFalse() throws Exception {
        ClassWrangler classWrangler = Mockito.spy(new ClassWrangler(Mockito.mock(ClassLoader.class), Mockito.mock(Log.class)));
        Mockito.doReturn("some/path/groovy-all-1.5.0.jar").when(classWrangler).getJarPath();
        Assert.assertFalse(classWrangler.isGroovyIndy());
    }

    @Test
    public void testGetGroovyVersionWithIndy() throws Exception {
        ClassWrangler classWrangler = Mockito.spy(new ClassWrangler(Mockito.mock(ClassLoader.class), Mockito.mock(Log.class)));
        Mockito.doReturn("some/path/groovy-all-2.4.0-indy.jar").when(classWrangler).getJarPath();
        Assert.assertEquals("2.4.0", classWrangler.getGroovyVersion().toString());
    }

    @Test
    public void testGetGroovyVersionWithGrooid() throws Exception {
        ClassWrangler classWrangler = Mockito.spy(new ClassWrangler(Mockito.mock(ClassLoader.class), Mockito.mock(Log.class)));
        Mockito.doReturn("some/path/groovy-all-2.4.0-grooid.jar").when(classWrangler).getJarPath();
        Assert.assertEquals("2.4.0", classWrangler.getGroovyVersion().toString());
    }
}
