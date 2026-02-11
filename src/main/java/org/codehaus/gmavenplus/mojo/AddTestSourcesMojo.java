package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;


/**
 * This mojo adds Groovy test sources to the project's test sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-3
 */
@Mojo(name = "addTestSources", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class AddTestSourcesMojo extends AbstractGroovySourcesMojo {

    /**
     * The Groovy test source files (relative paths).
     * Default: "${project.basedir}/src/test/groovy/&#42;&#42;/&#42;.groovy"
     */
    @Parameter
    protected FileSet[] testSources;

    /**
     * Executes this mojo.
     */
    @Override
    public void execute() {
        for (FileSet testSource : getTestFilesets(testSources, false)) {
            addTestSourcePath(testSource.getDirectory());
        }
    }

    /**
     * Adds the specified test source path to the project's test compile sources.
     *
     * @param path The test source path to add to the project's test compile sources
     */
    protected void addTestSourcePath(final String path) {
        if (!project.getTestCompileSourceRoots().contains(path)) {
            getLog().debug("Added test source directory: " + path);
            project.addTestCompileSourceRoot(path);
        }
    }

}
