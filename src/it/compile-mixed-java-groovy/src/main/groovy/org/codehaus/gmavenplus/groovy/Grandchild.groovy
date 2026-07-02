package org.codehaus.gmavenplus.groovy

import org.codehaus.gmavenplus.java.Child


class Grandchild extends Child {

    String yetAnotherMethod() {
        return someMethod()
    }

}
