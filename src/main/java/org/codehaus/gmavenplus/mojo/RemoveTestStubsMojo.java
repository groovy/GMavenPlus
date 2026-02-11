package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;


/**
 * This mojo removes Groovy test stubs from the project's sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-3
 */
@Mojo(name = "removeTestStubs", defaultPhase = LifecyclePhase.TEST_COMPILE, threadSafe = true)
public class RemoveTestStubsMojo extends AbstractGroovyStubSourcesMojo {

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
            try {
                project.getTestCompileSourceRoots().remove(testStubsOutputDirectory.getAbsolutePath());
            } catch (UnsupportedOperationException e) {
                try {
                    removeSourceRoot(project, "test", testStubsOutputDirectory);
                } catch (Throwable e2) {
                    e.addSuppressed(e2);
                    throw e;
                }
            }
        }
    }

}
