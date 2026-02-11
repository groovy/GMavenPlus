package org.codehaus.gmavenplus.mojo;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.File;
import java.io.IOException;


/**
 * Create a GroovyDoc jar for the main sources. Note by default this will also invoke the <i>groovydoc</i> goal
 * (unless <code>invokeGroovyDoc</code> is <code>false</code>).
 *
 * @author Keegan Witt
 * @since 1.7.1
 */
@Mojo(name = "groovydoc-jar", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class GroovyDocJarMojo extends GroovyDocMojo {

    /**
     * Specifies the filename that will be used for the generated jar file. Please note that <code>-groovydoc</code>
     * will be appended to the file name.
     */
    @Parameter(property = "project.build.finalName")
    protected String finalName;

    /**
     * Specifies the directory where the generated jar file will be put.
     */
    @Parameter(property = "project.build.directory")
    protected String jarOutputDirectory;

    /**
     * The Jar archiver.
     */
    @Component(role = Archiver.class, hint = "jar")
    protected JarArchiver jarArchiver;

    /**
     * The archive configuration to use.
     * See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver Reference</a>.
     */
    @Parameter
    protected final MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * Specifies whether to attach the generated artifact to the project helper.
     */
    @Parameter(property = "attach", defaultValue = "true")
    protected boolean attach;

    /**
     * Used for attaching the artifact in the project.
     */
    @Component
    private MavenProjectHelper projectHelper;

    /**
     * Path to the default MANIFEST file to use. It will be used if
     * <code>useDefaultManifestFile</code> is set to <code>true</code>.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}/META-INF/MANIFEST.MF", required = true, readonly = true)
    protected File defaultManifestFile;

    /**
     * Set this to <code>true</code> to enable the use of the <code>defaultManifestFile</code>.
     */
    @Parameter(defaultValue = "false")
    private boolean useDefaultManifestFile;

    /**
     * The classifier for the GroovyDoc jar.
     */
    @Parameter(defaultValue = "groovydoc")
    private String classifier;

    /**
     * The artifact type for the GroovyDoc jar.
     *
     * @since 1.10.0
     */
    @Parameter(defaultValue = "javadoc")
    private String artifactType;

    /**
     * Whether to invoke the <code>groovydoc</code> goal before creating jar.
     */
    @Parameter(defaultValue = "true")
    protected boolean invokeGroovyDoc;

    /**
     * Executes this mojo.
     */
    @Override
    public void execute() throws MojoExecutionException {
        if (invokeGroovyDoc) {
            // invoke the GroovyDoc mojo
            super.execute();
        }

        try {
            File outputFile = generateArchive(groovyDocOutputDirectory, finalName + "-" + classifier + ".jar");

            if (attach) {
                projectHelper.attachArtifact(project, artifactType, classifier, outputFile);
            } else {
                getLog().info("Not adding GroovyDoc jar to attached artifacts list.");
            }
        } catch (ArchiverException e) {
            throw new MojoExecutionException("ArchiverException: Error while creating archive", e);
        } catch (IOException e) {
            throw new MojoExecutionException("IOException: Error while creating archive", e);
        } catch (RuntimeException e) {
            throw new MojoExecutionException("RuntimeException: Error while creating archive", e);
        }
    }

    /**
     * Method that creates the jar file
     *
     * @param groovydocFiles the directory where the generated jar file will be put
     * @param jarFileName    the filename of the generated jar file
     * @return a File object that contains the generated jar file
     * @throws ArchiverException When an issue occurs preventing Maven Archiver from creating the jar file
     * @throws IOException       When an IO issue occurs preventing Maven Archiver from creating the jar file
     */
    protected File generateArchive(File groovydocFiles, String jarFileName) throws ArchiverException, IOException {
        File groovydocJar = new File(jarOutputDirectory, jarFileName);

        if (groovydocJar.exists()) {
            if (!groovydocJar.delete()) {
                getLog().warn("Unable to delete " + groovydocJar.getAbsolutePath());
            }
        }

        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(jarArchiver);
        archiver.setOutputFile(groovydocJar);

        if (!groovydocFiles.exists()) {
            getLog().warn("JAR will be empty - no content was marked for inclusion!");
        } else {
            archiver.getArchiver().addDirectory(groovydocFiles);
        }

        if (useDefaultManifestFile && defaultManifestFile.exists() && archive.getManifestFile() == null) {
            getLog().info("Adding existing MANIFEST to archive. Found under: " + defaultManifestFile.getPath());
            archive.setManifestFile(defaultManifestFile);
        }

        try {
            archiver.createArchive(session, project, archive);
        } catch (ManifestException e) {
            throw new ArchiverException("ManifestException: " + e.getMessage(), e);
        } catch (DependencyResolutionRequiredException e) {
            throw new ArchiverException("DependencyResolutionRequiredException: " + e.getMessage(), e);
        }

        return groovydocJar;
    }

}
