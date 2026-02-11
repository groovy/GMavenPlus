package org.codehaus.gmavenplus

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo


@Mojo(name = "helloworld", defaultPhase = LifecyclePhase.INSTALL)
class HelloWorldMojo extends AbstractMojo {

    void execute() {
        getLog().error("Hello world!")
    }

}
