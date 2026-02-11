package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;


/**
 * Adds Groovy test stubs directory back to Maven's list of test source directories. Normally, you won't need to use this mojo.
 *
 * @author Keegan Witt
 * @since 1.1
 */
@Mojo(name = "addTestStubSources", threadSafe = true)
public class AddTestStubSourcesMojo extends AbstractGroovyStubSourcesMojo {

    /**
     * The location for the compiled test classes.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/groovy-stubs/test")
    protected File testStubsOutputDirectory;

    /**
     * Flag to allow adding test sources to be skipped.
     */
    @Parameter(property = "maven.test.skip", defaultValue = "false")
    protected boolean skipTests;

    /**
     * Executes this mojo.
     */
    @Override
    public void execute() {
        if (!skipTests) {
            getLog().debug("Added test stub directory " + testStubsOutputDirectory.getAbsolutePath() + " to project test sources.");
            project.addTestCompileSourceRoot(testStubsOutputDirectory.getAbsolutePath());
        }
    }

}
