package org.codehaus.gmavenplus

import groovy.transform.Canonical
import groovy.transform.CompileStatic


@Canonical
@CompileStatic
class SomeClass {

    String someMethod() {
        return "Hello, world."
    }

}
