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


public class TheTest {

    @Test
    public void testSomeMethod() {
        SomeClass someClass = new SomeClass();
        Assert.assertEquals("Hello, world.", someClass.someMethod());
    }

    @Test
    public void testSomeMethod2() {
        SomeOtherClass someOtherClass = new SomeOtherClass();
        Assert.assertEquals("Hello, world.", someOtherClass.someMethod());
    }

    @Test
    public void testAnnotation() throws Exception {
        Class getClass = Class.forName("org.codehaus.gmavenplus.Get");
        Assert.assertNotNull(getClass);
    }

}
