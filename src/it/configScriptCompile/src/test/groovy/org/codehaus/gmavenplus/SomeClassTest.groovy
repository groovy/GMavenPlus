/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class SomeClassTest {
    SomeClass someClass

    @Before
    @CompileStatic(TypeCheckingMode.SKIP)
    void setup() {
        someClass = new SomeClass()
        someClass.metaClass.someMethod = { "Goodbye, world." }
    }

    @Test
    void testSomeMethod() {
        Assert.assertEquals("Hello, world.", someClass.someMethod())
    }

    @Test
    @CompileStatic(TypeCheckingMode.SKIP)
    void testSomeMethodNoCheck() {
        Assert.assertEquals("Goodbye, world.", someClass.someMethod())
    }

}
