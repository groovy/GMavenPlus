package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;


/**
 * This mojo adds Groovy sources to the project's sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
@Mojo(name = "addSources", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class AddSourcesMojo extends AbstractGroovySourcesMojo {

    /**
     * The Groovy source files (relative paths).
     * Default: "${project.basedir}/src/main/groovy/&#42;&#42;/&#42;.groovy"
     */
    @Parameter
    protected FileSet[] sources;

    /**
     * Executes this mojo.
     */
    @Override
    public void execute() {
        for (FileSet source : getFilesets(sources, false)) {
            addSourcePath(source.getDirectory());
        }
    }

    /**
     * Adds the specified source path to the project's main compile sources.
     *
     * @param path The source path to add to the project's main compile sources
     */
    protected void addSourcePath(final String path) {
        if (!project.getCompileSourceRoots().contains(path)) {
            getLog().debug("Added source directory: " + path);
            project.addCompileSourceRoot(path);
        }
    }

}
