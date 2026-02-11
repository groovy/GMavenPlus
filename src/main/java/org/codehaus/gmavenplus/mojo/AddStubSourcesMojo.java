package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;


/**
 * Adds Groovy stubs directory back to Maven's list of source directories. Normally, you won't need to use this mojo.
 *
 * @author Keegan Witt
 * @since 1.1
 */
@Mojo(name = "addStubSources", threadSafe = true)
public class AddStubSourcesMojo extends AbstractGroovyStubSourcesMojo {

    /**
     * The location for the compiled classes.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/groovy-stubs/main")
    protected File stubsOutputDirectory;

    /**
     * Executes this mojo.
     */
    @Override
    public void execute() {
        getLog().debug("Added stub directory " + stubsOutputDirectory.getAbsolutePath() + " to project sources.");
        project.addCompileSourceRoot(stubsOutputDirectory.getAbsolutePath());
    }

}
