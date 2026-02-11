package org.codehaus.gmavenplus


class SomeClass {
    @Lazy
    def list = ["dog", "cat"]

    def someMethod() {
        return list.toString()
    }

}
