package org.codehaus.gmavenplus;

import org.junit.Test;


public class HelloWorldMojoTest {
    private HelloWorldMojo mojo = new HelloWorldMojo();

    @Test
    public void testExecute() {
        // not really testing anything here, other than it doesn't unexpectedly blow up
        mojo.execute();
    }

}
