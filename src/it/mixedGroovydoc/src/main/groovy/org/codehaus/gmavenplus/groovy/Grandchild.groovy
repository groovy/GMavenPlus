package org.codehaus.gmavenplus.groovy

import org.codehaus.gmavenplus.java.Child


/**
 * Some GroovyDoc.
 */
class Grandchild extends Child {

    /**
     * Some method GroovyDoc.
     *
     * @return the String from the parent
     */
    String yetAnotherMethod() {
        return someMethod()
    }

}
