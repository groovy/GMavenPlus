package gmavenplus.mojo;

import java.lang.reflect.InvocationTargetException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * @author Keegan Witt
 *
 * @goal testGenerateStubs
 * @phase generate-test-sources
 */
public class GenerateTestStubsMojo extends AbstractGenerateStubsMojo {

    /**
     * Flag to allow test compilation to be skipped
     *
     * @parameter expression="${maven.test.skip}" default-value="false"
     */
    protected boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!skip) {
            logGroovyVersion("generateTestStubs");
            try {
                doStubGeneration(getTestSources(), testOutputDirectory);
                resetStubModifiedDates(getTestStubs());
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath. Do you have Groovy as a compile dependency in your project?", e);
            } catch (InvocationTargetException e) {
                throw new MojoExecutionException("Unable to call a method on a Groovy class from classpath.", e);
            } catch (InstantiationException e) {
                throw new MojoExecutionException("Unable to instantiate a Groovy class from classpath.", e);
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
            }
        } else {
            getLog().info("Skipping generation of test stubs because ${maven.test.skip} was set to true");
        }
    }

}
