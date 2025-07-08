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

import groovy.lang.GroovySystem;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;


public class SomeClassTest {

    @Test
    public void testSomeMethod() throws NoSuchMethodException {
        Method method = SomeClass.class.getDeclaredMethod("someMethod", String.class, String.class);
        List<Parameter> parameterNames = Arrays.asList(method.getParameters());

        Assert.assertEquals(2, parameterNames.size());

        if (GroovySystem.getVersion().startsWith("1.5")
                || GroovySystem.getVersion().startsWith("1.6")
                || GroovySystem.getVersion().startsWith("1.7")
                || GroovySystem.getVersion().startsWith("1.8")
                || GroovySystem.getVersion().startsWith("1.9")
                || GroovySystem.getVersion().startsWith("2.0")
                || GroovySystem.getVersion().startsWith("2.1")
                || GroovySystem.getVersion().startsWith("2.2")
                || GroovySystem.getVersion().startsWith("2.3")
                || GroovySystem.getVersion().startsWith("2.4")) {
            Assert.assertEquals("arg0", parameterNames.get(0).getName());
            Assert.assertEquals("arg1", parameterNames.get(1).getName());
        } else {
            Assert.assertEquals("param1", parameterNames.get(0).getName());
            Assert.assertEquals("param2", parameterNames.get(1).getName());
        }
    }

}
