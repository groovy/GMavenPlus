package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.gmavenplus.model.IncludeClasspath;

import java.io.File;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.codehaus.gmavenplus.model.GroovyCompileConfiguration;
import org.codehaus.gmavenplus.util.ForkedGroovyCompiler;
import org.codehaus.gmavenplus.util.GroovyCompiler;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * The base compile mojo, which all compile mojos extend.
 *
 * @author Keegan Witt
 */
public abstract class AbstractCompileMojo extends AbstractGroovySourcesMojo {

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
     * Whether to support invokeDynamic (requires Java 7 or greater and Groovy indy 2.0.0-beta-3 or greater).
     * Has no effect for Groovy 4, as it is always enabled.
     */
    @Parameter(defaultValue = "false")
    protected boolean invokeDynamic;

    /**
     * Whether to enable Groovy's parallel parsing. Requires Groovy 3.0.5.
     * Is enabled by default for Groovy 4.0.0-alpha-1 or newer.
     *
     * @since 1.11.0
     */
    @Parameter
    protected Boolean parallelParsing = null;

    /**
     * A <a href="http://groovy-lang.org/dsls.html#compilation-customizers">script</a> for tweaking the configuration options
     * (requires Groovy 2.1.0-beta-1 or greater). Note that its encoding must match your source encoding.
     */
    @Parameter
    protected File configScript;

    /**
     * Generate metadata for reflection on method parameter names using the functionality provided by JEP 118
     * (requires Java 8 or greater and Groovy 2.5.0-alpha-1 or greater).
     */
    @Parameter(defaultValue = "false")
    protected boolean parameters;

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
     * Whether the bytecode version has preview features enabled (JEP 12).
     * Requires Groovy &gt;= 3.0.0-beta-1 or Groovy &gt;= 2.5.7, but not any 2.6 versions and Java &gt;= 12.
     *
     * @since 1.7.1
     */
    @Parameter(defaultValue = "false")
    protected boolean previewFeatures;

    /**
     * The ToolchainManager.
     */
    @Component
    protected ToolchainManager toolchainManager;

    /**
     * Performs compilation of compile mojos.
     *
     * @param sources                the sources to compile
     * @param classpath              the classpath to use for compilation
     * @param compileOutputDirectory the directory to write the compiled class files to
     * @throws ClassNotFoundException    when a class needed for compilation cannot be found
     * @throws InstantiationException    when a class needed for compilation cannot be instantiated
     * @throws IllegalAccessException    when a method needed for compilation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for compilation cannot be completed
     * @throws MalformedURLException     when a classpath element provides a malformed URL
     */
    @SuppressWarnings({"rawtypes"})
    protected synchronized void doCompile(final Set<File> sources, final List classpath, final File compileOutputDirectory)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, MalformedURLException {
        if (sources == null || sources.isEmpty()) {
            getLog().info("No sources specified for compilation. Skipping.");
            return;
        }

        GroovyCompileConfiguration configuration = new GroovyCompileConfiguration(sources, classpath, compileOutputDirectory);
        configuration.setIncludeClasspath(includeClasspath);
        configuration.setSkipBytecodeCheck(skipBytecodeCheck);
        configuration.setDebug(debug);
        configuration.setVerbose(verbose);
        configuration.setWarningLevel(warningLevel);
        configuration.setTolerance(tolerance);
        configuration.setInvokeDynamic(invokeDynamic);
        configuration.setParallelParsing(parallelParsing);
        configuration.setConfigScript(configScript);
        configuration.setParameters(parameters);
        configuration.setPreviewFeatures(previewFeatures);
        configuration.setSourceEncoding(sourceEncoding);
        configuration.setTargetBytecode(targetBytecode);

        Toolchain toolchain = toolchainManager.getToolchainFromBuildContext("jdk", session);
        if (toolchain != null) {
            getLog().info("Toolchain in gmavenplus-plugin: " + toolchain);
            performForkedCompilation(configuration, toolchain.findTool("java"));
        } else {
            getLog().info("Performing in-process compilation");
            performInProcessCompilation(configuration, classpath);
        }
    }

    protected void performInProcessCompilation(GroovyCompileConfiguration configuration, List<?> classpath) throws MalformedURLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        setupClassWrangler(classpath, includeClasspath);
        logPluginClasspath();
        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());

        if (!groovyVersionSupportsAction()) {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support compilation. The minimum version of Groovy required is " + minGroovyVersion + ". Skipping compiling.");
            return;
        }

        GroovyCompiler compiler = new GroovyCompiler(classWrangler, getLog());
        compiler.compile(configuration);
    }

    protected void performForkedCompilation(GroovyCompileConfiguration configuration, String javaExecutable) {
        if (javaExecutable == null) {
            getLog().warn("Unable to find 'java' executable for toolchain. Falling back to in-process compilation.");
            try {
                performInProcessCompilation(configuration, configuration.getClasspath());
            } catch (Exception e) {
                throw new RuntimeException("Compilation failed", e);
            }
            return;
        }

        try {
            File configFile = File.createTempFile("gmavenplus-compile-config", ".ser");
            configFile.deleteOnExit();
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(configFile.toPath()))) {
                oos.writeObject(configuration);
            }

            List<String> command = new ArrayList<>();
            command.add(javaExecutable);
            command.add("-cp");
            command.add(buildForkClasspath());
            command.add(ForkedGroovyCompiler.class.getName());
            command.add(configFile.getAbsolutePath());

            getLog().info("Forking compilation using " + javaExecutable);
            getLog().debug("Command: " + command);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Groovy compilation failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Unable to fork compilation", e);
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

}
