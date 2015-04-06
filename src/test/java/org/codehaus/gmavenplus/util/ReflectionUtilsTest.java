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


/**
 * Unit tests for the ReflectionUtils class.
 *
 * @author Keegan Witt
 */
public class ReflectionUtilsTest {

    @Test
    public void testHappyPaths() throws Exception {
        String expectedString = "some string";
        Object test1 = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(TestClass.class));
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(TestClass.class, "setStringField", String.class), test1, expectedString);
        Assert.assertEquals(expectedString, ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(TestClass.class, "getStringField"), test1));
        Assert.assertEquals(TestClass.HELLO_WORLD, ReflectionUtils.invokeStaticMethod(ReflectionUtils.findMethod(TestClass.class, "helloWorld")));
        Assert.assertEquals(TestClass.ENUM.VALUE, ReflectionUtils.getEnumValue(TestClass.ENUM.class, "VALUE"));
        Assert.assertEquals(TestClass.HELLO_WORLD, ReflectionUtils.getStaticField(ReflectionUtils.findField(TestClass.class, "HELLO_WORLD", null)));
        Object test2 = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(TestClass.class, String.class), expectedString );
        Assert.assertEquals(expectedString, ReflectionUtils.getField(ReflectionUtils.findField(TestClass.class, "stringField", String.class), test2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindConstructorClassNull() {
        ReflectionUtils.findConstructor(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindConstructorNotFound() {
        ReflectionUtils.findConstructor(TestClass.class, TestClass.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindFieldClassNull() {
        ReflectionUtils.findField(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindFieldNameAndTypeNull() {
        ReflectionUtils.findField(TestClass.class, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindFieldNotFound() {
        ReflectionUtils.findField(TestClass.class, "nonExistentField", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindMethodClassNull() {
        ReflectionUtils.findMethod(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindMethodNameNull() {
        ReflectionUtils.findMethod(TestClass.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindMethodNotFound() {
        ReflectionUtils.findMethod(TestClass.class, "nonExistentMethod");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEnumConstantNonEnumClass() {
        ReflectionUtils.getEnumValue(TestClass.class, "VALUE");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEnumConstantValueNotFound() {
        ReflectionUtils.getEnumValue(TestClass.ENUM.class, "NON_EXISTENT_VALUE");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetStaticFieldNotStatic() throws Exception {
        ReflectionUtils.getStaticField(ReflectionUtils.findField(TestClass.class, "stringField", String.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvokeConstructorNull() throws Exception {
        ReflectionUtils.invokeConstructor(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvokeMethodMethodNull() throws Exception {
        ReflectionUtils.invokeMethod(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvokeMethodObjectNull() throws Exception {
        ReflectionUtils.invokeMethod(TestClass.class.getMethod("getStringField"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvokeStaticMethodMethodNull() throws Exception {
        ReflectionUtils.invokeStaticMethod(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvokeStaticMethodMethodNotStatic() throws Exception {
        ReflectionUtils.invokeStaticMethod(TestClass.class.getMethod("getStringField"));
    }

    @Test
    public void testConstructor() throws Exception {
        ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(ReflectionUtils.class));
    }

    public static class TestClass {
        public static final String HELLO_WORLD = "Hello world!";
        public String stringField;

        public TestClass() { }

        public TestClass(String newStringField) {
            stringField = newStringField;
        }

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String newStringField) {
            stringField = newStringField;
        }

        public static String helloWorld() {
            return HELLO_WORLD;
        }

        protected static enum ENUM {
            VALUE
        }
    }

}
