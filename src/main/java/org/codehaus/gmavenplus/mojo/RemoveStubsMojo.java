package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;


/**
 * This mojo removes Groovy stubs from the project's sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-3
 */
@Mojo(name = "removeStubs", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class RemoveStubsMojo extends AbstractGroovyStubSourcesMojo {

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
        try {
            removeSourceRoot(project, SourceRootScope.MAIN, stubsOutputDirectory);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to remove Groovy stub source root", e);
        }
    }

}
