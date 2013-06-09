package org.codehaus.gmavenplus

import org.junit.Test


class HelloWorldMojoTest {
    HelloWorldMojo mojo = new HelloWorldMojo()

    @Test
    void testExecute() {
        // not really testing anything here, other than it doesn't unexpectedly blow up
        mojo.execute()
    }

}
