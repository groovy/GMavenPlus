package gmavenplus.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * @author Keegan Witt
 * @version $Rev$ $Date$
 *
 * @goal execute
 */
public class ExecuteMojo extends AbstractGroovyMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        logGroovyVersion("execute");
        // TODO: implement
    }

}
