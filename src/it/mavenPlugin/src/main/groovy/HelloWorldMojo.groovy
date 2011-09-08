import org.apache.maven.plugin.AbstractMojo

class HelloWorldMojo extends AbstractMojo {
    void execute() {
        getLog().error("Hello world!")
    }

}
