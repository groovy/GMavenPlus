package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.gmavenplus.model.IncludeClasspath;
import org.codehaus.gmavenplus.model.internal.Version;
import org.codehaus.gmavenplus.util.GroovyCompiler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;


/**
 * The base generate stubs mojo, which all generate stubs mojos extend.
 *
 * @author Keegan Witt
 */
public abstract class AbstractGenerateStubsMojo extends AbstractGroovyStubSourcesMojo {

    /**
     * Groovy 1.8.2 version.
     */
    protected static final Version GROOVY_1_8_2 = new Version(1, 8, 2);

    /**
     * The encoding of source files.
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    protected String sourceEncoding;

    /**
     * The Groovy compiler bytecode compatibility. One of
     * <ul>
     *   <li>1.4 (or 4)</li>
     *   <li>1.5 (or 5)</li>
     *   <li>1.6 (or 6)</li>
     *   <li>1.7 (or 7)</li>
     *   <li>1.8 (or 8)</li>
     *   <li>9 (or 1.9)</li>
     *   <li>10</li>
     *   <li>11</li>
     *   <li>12</li>
     *   <li>13</li>
     *   <li>14</li>
     *   <li>15</li>
     *   <li>16</li>
     *   <li>17</li>
     *   <li>18</li>
     *   <li>19</li>
     *   <li>20</li>
     *   <li>21</li>
     *   <li>22</li>
     *   <li>23</li>
     *   <li>24</li>
     *   <li>25</li>
     * </ul>
     * Using 1.6 (or 6) or 1.7 (or 7) requires Groovy &gt;= 2.1.3.
     * Using 1.8 (or 8) requires Groovy &gt;= 2.3.3.
     * Using 9 (or 1.9) requires Groovy &gt;= 2.5.3, or Groovy &gt;= 2.6.0 alpha 4, or Groovy &gt;= 3.0.0 alpha 2.
     * Using 9 (or 1.9) with invokedynamic requires Groovy &gt;= 2.5.3, or Groovy &gt;= 3.0.0 alpha 2, but not any 2.6 versions.
     * Using 10, 11, or 12 requires Groovy &gt;= 2.5.3, or Groovy &gt;= 3.0.0 alpha 4, but not any 2.6 versions.
     * Using 13 requires Groovy &gt;= 2.5.7, or Groovy &gt;= 3.0.0-beta-1, but not any 2.6 versions.
     * Using 14 requires Groovy &gt;= 3.0.0 beta-2.
     * Using 15 requires Groovy &gt;= 3.0.3.
     * Using 16 requires Groovy &gt;= 3.0.6.
     * Using 17 requires Groovy &gt;= 3.0.8 or Groovy &gt; 4.0.0-alpha-3.
     * Using 18 requires Groovy &gt; 4.0.0-beta-1.
     * Using 19 requires Groovy &gt; 4.0.2.
     * Using 20 requires Groovy &gt; 4.0.6.
     * Using 21 requires Groovy &gt; 4.0.11.
     * Using 22 requires Groovy &gt; 4.0.16 or Groovy &gt; 5.0.0-alpha-3.
     * Using 23 requires Groovy &gt; 4.0.21 or Groovy &gt; 5.0.0-alpha-8.
     * Using 24 requires Groovy &gt; 4.0.24 or Groovy &gt; 5.0.0-alpha-11.
     * Using 25 requires Groovy &gt; 4.0.27 or Groovy &gt; 5.0.0-alpha-13.
     *
     * @since 1.0-beta-3
     */
    @Parameter(property = "maven.compiler.target", defaultValue = "1.8")
    protected String targetBytecode;

    /**
     * Whether to check that the version of Groovy used is able to use the requested <code>targetBytecode</code>.
     *
     * @since 1.9.0
     */
    @Parameter(property = "skipBytecodeCheck", defaultValue = "false")
    protected boolean skipBytecodeCheck;

    /**
     * Whether Groovy compiler should be set to debug.
     */
    @Parameter(defaultValue = "false")
    protected boolean debug;

    /**
     * Whether Groovy compiler should be set to verbose.
     */
    @Parameter(defaultValue = "false")
    protected boolean verbose;

    /**
     * Groovy compiler warning level. Should be one of:
     * <dl>
     *   <dt>0</dt>
     *     <dd>None</dd>
     *   <dt>1</dt>
     *     <dd>Likely Errors</dd>
     *   <dt>2</dt>
     *     <dd>Possible Errors</dd>
     *   <dt>3</dt>
     *     <dd>Paranoia</dd>
     * </dl>
     */
    @Parameter(defaultValue = "1")
    protected int warningLevel;

    /**
     * Groovy compiler error tolerance (the number of non-fatal errors (per unit) that should be tolerated before compilation is aborted).
     */
    @Parameter(defaultValue = "0")
    protected int tolerance;

    /**
     * What classpath to include. One of
     * <ul>
     *   <li>PROJECT_ONLY</li>
     *   <li>PROJECT_AND_PLUGIN</li>
     *   <li>PLUGIN_ONLY</li>
     * </ul>
     * Uses the same scope as the required dependency resolution of this mojo. Use only if you know what you're doing.
     *
     * @since 1.8.0
     */
    @Parameter(defaultValue = "PROJECT_ONLY")
    protected IncludeClasspath includeClasspath;

    /**
     * The Maven ToolchainManager.
     */
    @javax.inject.Inject
    protected org.apache.maven.toolchain.ToolchainManager toolchainManager;

    /**
     * The Maven Session.
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected org.apache.maven.execution.MavenSession session;

    /**
     * Whether to execute in a forked process.
     *
     * @since 4.3.0
     */
    @Parameter(property = "fork", defaultValue = "false")
    protected boolean fork;

    /**
     * Performs the stub generation on the specified source files.
     *
     * @param stubSources     the sources to perform stub generation on
     * @param classpath       The classpath to use for compilation
     * @param outputDirectory the directory to write the stub files to
     * @throws ClassNotFoundException    when a class needed for stub generation cannot be found
     * @throws InstantiationException    when a class needed for stub generation cannot be instantiated
     * @throws IllegalAccessException    when a method needed for stub generation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for stub generation cannot be completed
     * @throws MalformedURLException     when a classpath element provides a malformed URL
     */
    protected synchronized void doStubGeneration(final Set<File> stubSources, final List<?> classpath, final File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        if (stubSources == null || stubSources.isEmpty()) {
            getLog().info("No sources specified for stub generation. Skipping.");
            return;
        }

        org.codehaus.gmavenplus.model.GroovyStubConfiguration configuration = new org.codehaus.gmavenplus.model.GroovyStubConfiguration(stubSources, classpath, outputDirectory);
        configuration.setIncludeClasspath(includeClasspath);
        configuration.setSkipBytecodeCheck(skipBytecodeCheck);
        configuration.setDebug(debug);
        configuration.setVerbose(verbose);
        configuration.setWarningLevel(warningLevel);
        configuration.setTolerance(tolerance);
        configuration.setSourceEncoding(sourceEncoding);
        configuration.setTargetBytecode(targetBytecode);

        org.apache.maven.toolchain.Toolchain toolchain = toolchainManager.getToolchainFromBuildContext("jdk", session);
        if (toolchain != null) {
            getLog().info("Toolchain in gmavenplus-plugin: " + toolchain);
            performForkedStubGeneration(configuration, toolchain.findTool("java"));
        } else if (fork) {
            String javaExecutable = getJavaExecutable();
            getLog().info("Forking stub generation using " + javaExecutable);
            performForkedStubGeneration(configuration, javaExecutable);
        } else {
            getLog().info("Performing in-process stub generation");
            performInProcessStubGeneration(configuration, classpath);
        }
    }

    protected void performInProcessStubGeneration(org.codehaus.gmavenplus.model.GroovyStubConfiguration configuration, List<?> classpath) throws MalformedURLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        setupClassWrangler(classpath, includeClasspath);
        logPluginClasspath();
        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());

        // Note: GroovyCompiler handles minGroovyVersion check now in generateStubs

        GroovyCompiler compiler = new GroovyCompiler(classWrangler, getLog());
        compiler.generateStubs(configuration);
    }

    protected void performForkedStubGeneration(org.codehaus.gmavenplus.model.GroovyStubConfiguration configuration, String javaExecutable) throws InvocationTargetException {
        try {
            // Write configuration to file
            File configFile = File.createTempFile("groovy-stub-config", ".ser");
            configFile.deleteOnExit();
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(java.nio.file.Files.newOutputStream(configFile.toPath()))) {
                oos.writeObject(configuration);
            }

            // Build classpath for forked process (plugin + dependencies)
            String forkClasspath = buildForkClasspath();

            List<String> command = new java.util.ArrayList<>();
            command.add(javaExecutable);
            command.add("-cp");
            command.add(forkClasspath);
            command.add("org.codehaus.gmavenplus.util.ForkedGroovyCompiler");
            command.add(configFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new InvocationTargetException(new RuntimeException("Forked stub generation failed with exit code " + exitCode));
            }

        } catch (java.io.IOException | InterruptedException e) {
            throw new InvocationTargetException(e);
        }
    }

    protected String buildForkClasspath() {
        StringBuilder cp = new StringBuilder();
        // Add plugin artifact
        cp.append(pluginDescriptor.getPluginArtifact().getFile().getAbsolutePath());

        // Add plugin dependencies
        for (org.apache.maven.artifact.Artifact artifact : pluginDescriptor.getArtifacts()) {
            cp.append(File.pathSeparator);
            cp.append(artifact.getFile().getAbsolutePath());
        }

        // Add maven-plugin-api jar which is 'provided' so not in getArtifacts()
        try {
            Class<?> logClass = org.apache.maven.plugin.logging.Log.class;
            java.security.CodeSource codeSource = logClass.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                String logJar = new File(codeSource.getLocation().toURI()).getAbsolutePath();
                cp.append(File.pathSeparator).append(logJar);
            }
        } catch (Exception e) {
            getLog().warn("Could not find maven-plugin-api jar to add to fork classpath", e);
        }

        return cp.toString();
    }

    /**
     * Logs the stubs that have been generated.
     *
     * @param outputDirectory the output directory for the stubs
     */
    protected void logGeneratedStubs(File outputDirectory) {
        Set<File> stubs = getStubs(outputDirectory);
        getLog().info("Generated " + stubs.size() + " stub" + (stubs.size() != 1 ? "s" : "") + ".");
    }

    /**
     * This is a fix for <a href="http://jira.codehaus.org/browse/MGROOVY-187">...</a>
     * It modifies the dates of the created stubs to 1/1/1970, ensuring that the Java compiler will not overwrite perfectly
     * good compiled Groovy just because it has a newer source stub. Basically, this prevents the stubs from causing a
     * side effect with the Java compiler, but still allows stubs to work with JavaDoc.
     *
     * @param stubs the files on which to reset the modified date
     */
    protected void resetStubModifiedDates(final Set<File> stubs) {
        for (File stub : stubs) {
            boolean success = stub.setLastModified(0L);
            if (!success) {
                getLog().warn("Unable to set modified time on stub " + stub.getAbsolutePath() + ".");
            }
        }
    }




}
