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

import groovy.lang.GroovySystem;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the ClassWrangler class.
 *
 * @author Keegan Witt
 */
public class ClassWranglerTest {

    @Test
    public void testGetGroovyJar() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(mock(ClassLoader.class), mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to force GMavenPlus to get version from jar.")).when(classWrangler).getClass(anyString());
        doReturn("some/path/groovy-all-1.5.0.jar").when(classWrangler).getJarPath();
        assertEquals("groovy-all-1.5.0.jar", classWrangler.getGroovyJar());
    }

    @Test
    public void testGetGroovyVersionStringFromGroovySystemThenFromInvokerHelper() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(mock(ClassLoader.class), mock(Log.class)));
        doReturn("some/path/groovy-all-1.5.0.jar").when(classWrangler).getJarPath();
        ArgumentCaptor<String> classArg = ArgumentCaptor.forClass(String.class);
        classWrangler.getGroovyVersionString();
        verify(classWrangler, times(2)).getClass(classArg.capture());
        assertEquals(GroovySystem.class.getCanonicalName(), classArg.getAllValues().get(0));
        assertEquals(InvokerHelper.class.getCanonicalName(), classArg.getAllValues().get(1));
    }

    @Test
    public void testGetGroovyVersionStringFromJar() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(mock(ClassLoader.class), mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to force GMavenPlus to get version from jar.")).when(classWrangler).getClass(anyString());
        doReturn("some/path/groovy-all-1.5.0.jar").when(classWrangler).getJarPath();
        assertEquals("1.5.0", classWrangler.getGroovyVersionString());
    }

    @Test
    public void testGetGroovyVersionWithIndyFromJar() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(mock(ClassLoader.class), mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to force GMavenPlus to get version from jar.")).when(classWrangler).getClass(anyString());
        doReturn("some/path/groovy-all-2.4.0-indy.jar").when(classWrangler).getJarPath();
        assertEquals("2.4.0", classWrangler.getGroovyVersion().toString());
    }

    @Test
    public void testGetGroovyVersionWithGrooidFromJar() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(mock(ClassLoader.class), mock(Log.class)));
        doReturn("some/path/groovy-all-2.4.0-grooid.jar").when(classWrangler).getJarPath();
        assertEquals("2.4.0", classWrangler.getGroovyVersion().toString());
    }

    @Test
    public void testIsGroovyIndyTrue() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(mock(ClassLoader.class), mock(Log.class)));
        doReturn(null).when(classWrangler).getClass(anyString());  // make it appear Groovy is indy
        assertTrue(classWrangler.isGroovyIndy());
    }

    @Test
    public void testIsGroovyIndyFalse() throws Exception {
        ClassWrangler classWrangler = spy(new ClassWrangler(mock(ClassLoader.class), mock(Log.class)));
        doThrow(new ClassNotFoundException("Throwing exception to make it appear Groovy is not indy.")).when(classWrangler).getClass(anyString());
        assertFalse(classWrangler.isGroovyIndy());
    }

}
