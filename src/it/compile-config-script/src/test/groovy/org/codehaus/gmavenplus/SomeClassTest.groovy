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
