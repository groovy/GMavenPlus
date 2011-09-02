package gmavenplus.mojo;

import java.lang.reflect.InvocationTargetException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * @author Keegan Witt
 *
 * @goal generateStubs
 * @phase generate-sources
 */
public class GenerateStubsMojo extends AbstractGenerateStubsMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        logGroovyVersion("generateStubs");

        try {
            doStubGeneration(getSources(), outputDirectory);
            resetStubModifiedDates(getStubs());
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Unable to get a Groovy class from classpath. Do you have Groovy as a compile dependency in your project?", e);
        } catch (InvocationTargetException e) {
            throw new MojoExecutionException("Unable to call a method on a Groovy class from classpath.", e);
        } catch (InstantiationException e) {
            throw new MojoExecutionException("Unable to instantiate a Groovy class from classpath.", e);
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
        }
    }

}
