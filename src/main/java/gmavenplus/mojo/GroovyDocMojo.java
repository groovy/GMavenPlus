package gmavenplus.mojo;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;


/**
 * @author Keegan Witt
 *
 * @goal groovydoc
 */
public class GroovyDocMojo extends AbstractGroovyMojo {

    /**
     * Location of the Groovy source files
     *
     * @parameter expression="${project.basedir}/src/main/groovy"
     * @readonly
     * @required
     */
    protected File sourceDirectory;

    /**
     * Location for the compiled classes
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @readonly
     * @required
     */
    protected File outputDirectory;

    /**
     * Location of the Groovy test source files
     *
     * @parameter expression="${project.basedir}/src/test/groovy"
     * @readonly
     * @required
     */
    protected File testSourceDirectory;

    /**
     * Location for the compiled test classes
     *
     * @parameter expression="${project.build.testOutputDirectory}"
     * @readonly
     * @required
     */
    protected File testOutputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        logGroovyVersion("groovydoc");

        // TODO: implement
        //org.codehaus.groovy.tools.groovydoc.FileOutputTool
        //org.codehaus.groovy.tools.groovydoc.GroovyDocTool
    }

    protected Set<File> getSources() {
        Set<File> sources = new HashSet<File>();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**/*.groovy"};
        ds.setIncludes(includes);
        ds.setBasedir(sourceDirectory);
        ds.setCaseSensitive(true);
        ds.scan();

        String[] files = ds.getIncludedFiles();
        for (int i = 0; i < files.length; i++) {
            sources.add(new File(sourceDirectory, files[i]));
        }

        return sources;
    }

    protected Set<File> getTestSources() {
        Set<File> sources = new HashSet<File>();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**/*.groovy"};
        ds.setIncludes(includes);
        ds.setBasedir(testSourceDirectory);
        ds.setCaseSensitive(true);
        ds.scan();

        String[] files = ds.getIncludedFiles();
        for (int i = 0; i < files.length; i++) {
            sources.add(new File(testSourceDirectory, files[i]));
        }

        return sources;
    }

}
