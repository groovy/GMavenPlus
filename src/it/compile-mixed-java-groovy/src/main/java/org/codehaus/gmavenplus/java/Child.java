package org.codehaus.gmavenplus.java;

import org.codehaus.gmavenplus.groovy.Parent;


public class Child extends Parent {

    public String someOtherMethod() {
        return someMethod();
    }

}
