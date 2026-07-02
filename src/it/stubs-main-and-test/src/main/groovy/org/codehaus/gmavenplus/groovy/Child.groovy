package org.codehaus.gmavenplus.groovy

import org.codehaus.gmavenplus.java.Parent


class Child extends Parent {

    String someOtherMethod() {
        someMethod()
    }

}
