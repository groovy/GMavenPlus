/*
 * Copyright (C) 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus.mojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.gmavenplus.model.IncludeClasspath;
import org.codehaus.gmavenplus.model.internal.Version;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.gmavenplus.mojo.GroovycLogger.LogTarget;

import static java.util.stream.Collectors.joining;
import static org.codehaus.gmavenplus.util.ReflectionUtils.findConstructor;
import static org.codehaus.gmavenplus.util.ReflectionUtils.findMethod;
import static org.codehaus.gmavenplus.util.ReflectionUtils.invokeConstructor;
import static org.codehaus.gmavenplus.util.ReflectionUtils.invokeMethod;


/**
 * The base compile mojo, which all compile mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractCompileMojo extends AbstractGroovySourcesMojo {

    /**
     * Groovy 5.0.0-alpha-13 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA13 = new Version(5, 0, 0, "alpha-13");

    /**
     * Groovy 5.0.0-alpha-11 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA11 = new Version(5, 0, 0, "alpha-11");

    /**
     * Groovy 5.0.0-alpha-8 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA8 = new Version(5, 0, 0, "alpha-8");

    /**
     * Groovy 5.0.0-alpha-3 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA3 = new Version(5, 0, 0, "alpha-3");

    /**
     * Groovy 5.0.0-alpha-1 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA1 = new Version(5, 0, 0, "alpha-1");

    /**
     * Groovy 4.0.27 version.
     */
    protected static final Version GROOVY_4_0_27 = new Version(4, 0, 27);

    /**
     * Groovy 4.0.24 version.
     */
    protected static final Version GROOVY_4_0_24 = new Version(4, 0, 24);

    /**
     * Groovy 4.0.21 version.
     */
    protected static final Version GROOVY_4_0_21 = new Version(4, 0, 21);

    /**
     * Groovy 4.0.11 version.
     */
    protected static final Version GROOVY_4_0_16 = new Version(4, 0, 16);

    /**
     * Groovy 4.0.11 version.
     */
    protected static final Version GROOVY_4_0_11 = new Version(4, 0, 11);

    /**
     * Groovy 4.0.6 version.
     */
    protected static final Version GROOVY_4_0_6 = new Version(4, 0, 6);

    /**
     * Groovy 4.0.2 version.
     */
    protected static final Version GROOVY_4_0_2 = new Version(4, 0, 2);

    /**
     * Groovy 4.0.0 beta-1 version.
     */
    protected static final Version GROOVY_4_0_0_BETA1 = new Version(4, 0, 0, "beta-1");

    /**
     * Groovy 4.0.0 alpha-3 version.
     */
    protected static final Version GROOVY_4_0_0_ALPHA3 = new Version(4, 0, 0, "alpha-3");

    /**
     * Groovy 4.0.0 alpha-1 version.
     */
    protected static final Version GROOVY_4_0_0_ALPHA1 = new Version(4, 0, 0, "alpha-1");

    /**
     * Groovy 3.0.8 version.
     */
    protected static final Version GROOVY_3_0_8 = new Version(3, 0, 8);

    /**
     * Groovy 3.0.6 version.
     */
    protected static final Version GROOVY_3_0_6 = new Version(3, 0, 6);

    /**
     * Groovy 3.0.5 version.
     */
    protected static final Version GROOVY_3_0_5 = new Version(3, 0, 5);

    /**
     * Groovy 3.0.3 version.
     */
    protected static final Version GROOVY_3_0_3 = new Version(3, 0, 3);

    /**
     * Groovy 3.0.0 beta-2 version.
     */
    protected static final Version GROOVY_3_0_0_BETA2 = new Version(3, 0, 0, "beta-2");

    /**
     * Groovy 3.0.0 beta-1 version.
     */
    protected static final Version GROOVY_3_0_0_BETA1 = new Version(3, 0, 0, "beta-1");

    /**
     * Groovy 3.0.0 alpha-4 version.
     */
    protected static final Version GROOVY_3_0_0_ALPHA4 = new Version(3, 0, 0, "alpha-4");

    /**
     * Groovy 3.0.0 alpha-2 version.
     */
    protected static final Version GROOVY_3_0_0_ALPHA2 = new Version(3, 0, 0, "alpha-2");

    /**
     * Groovy 3.0.0 alpha-1 version.
     */
    protected static final Version GROOVY_3_0_0_ALPHA1 = new Version(3, 0, 0, "alpha-1");

    /**
     * Groovy 2.6.0 alpha-4 version.
     */
    protected static final Version GROOVY_2_6_0_ALPHA4 = new Version(2, 6, 0, "alpha-4");

    /**
     * Groovy 2.6.0 alpha-1 version.
     */
    protected static final Version GROOVY_2_6_0_ALPHA1 = new Version(2, 6, 0, "alpha-1");

    /**
     * Groovy 2.5.7 version.
     */
    protected static final Version GROOVY_2_5_7 = new Version(2, 5, 7);

    /**
     * Groovy 2.5.3 version.
     */
    protected static final Version GROOVY_2_5_3 = new Version(2, 5, 3);

    /**
     * Groovy 2.5.0 alpha-1 version.
     */
    protected static final Version GROOVY_2_5_0_ALPHA1 = new Version(2, 5, 0, "alpha-1");

    /**
     * Groovy 2.3.3 version.
     */
    protected static final Version GROOVY_2_3_3 = new Version(2, 3, 3);

    /**
     * Groovy 2.1.3 version.
     */
    protected static final Version GROOVY_2_1_3 = new Version(2, 1, 3);

    /**
     * Groovy 2.1.0 beta-1 version.
     */
    protected static final Version GROOVY_2_1_0_BETA1 = new Version(2, 1, 0, "beta-1");

    /**
     * Groovy 2.0.0 beta-3 version.
     */
    protected static final Version GROOVY_2_0_0_BETA3 = new Version(2, 0, 0, "beta-3");

    /**
     * Groovy 1.6.0 version.
     */
    protected static final Version GROOVY_1_6_0 = new Version(1, 6, 0);

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
     * Whether to run the compiler using {@code groovyc} in a separate process.
     * <p>
     * {@code groovyc} will be search in {@code GROOVY_HOME/bin} first and then on the {@code PATH}.
     * If no executable was found, the compilation fails.
     */
    @Parameter(property = "groovy.fork", defaultValue = "false")
    protected boolean fork;

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
     * @throws MojoExecutionException    in case the mojo execution breaks with another reason.
     */
    @SuppressWarnings({"rawtypes"})
    protected synchronized void doCompile(final Set<File> sources, final List classpath, final File compileOutputDirectory)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, MalformedURLException, MojoExecutionException {
        if (sources == null || sources.isEmpty()) {
            getLog().info("No sources specified for compilation. Skipping.");
            return;
        }

        if (this.fork) {
            doCompileProcess(sources, classpath, compileOutputDirectory);
            return;
        }

        doCompileLibrary(sources, classpath, compileOutputDirectory);
    }

    private void doCompileProcess(Set<File> sources, List<?> classpath, File compileOutputDirectory)
        throws MojoExecutionException {
        Path groovyc = getGroovyc();

        List<String> args = new ArrayList<>();
        args.add(groovyc.toAbsolutePath().toString());
        final String delimitedCp = getClassPathString(classpath);
        args.add("--classpath="+delimitedCp);

        if (this.sourceEncoding != null && !this.sourceEncoding.trim().isEmpty()) {
            args.add("--encoding=" + this.sourceEncoding);
        }

        if (this.parameters) {
            args.add("--parameters");
        }

        if (this.previewFeatures) {
            args.add("--enable-preview");
        }

        if (this.targetBytecode != null) {
            args.add("-Jtarget=" + this.targetBytecode);
        }

        if (this.debug) {
            getLog().warn("Option 'debug' is requested but not supported yet with fork=true.");
        }

        if (!this.invokeDynamic) {
            getLog().warn("Option 'invokeDynamic=false' is requested but not supported yet with fork=true.");
        }

        if (!this.skipBytecodeCheck) {
            getLog().warn("Option 'skipBytecodeCheck' is requested but not supported yet with fork=true.");
        }

        if (this.warningLevel != 1) {
            getLog().warn("Option 'warningLevel' is requested but not supported yet with fork=true.");
        }

        if (this.tolerance != 0) {
            getLog().warn("Option 'tolerance' is requested but not supported yet with fork=true.");
        }

        if (this.parallelParsing != null) {
            getLog().warn("Option 'parallelParsing' is requested but not supported yet with fork=true.");
        }

        if (this.includeClasspath != IncludeClasspath.PROJECT_ONLY) {
            getLog().warn("Option 'includeClasspath' is requested but not supported yet with fork=true.");
        }

        // missing:
        // this.configScript (available as --configscript=)
        // this.verbose
        // as well as:
        // --compile-static
        // --type-checked
        // --temp=

        final String compileMessage = String.format(
            Locale.ROOT,
            "Compiling %d source files with groovyc %s to %s",
            sources.size(),
            args.stream().skip(2).collect(Collectors.toList()),
            project.getBasedir().toPath().relativize(compileOutputDirectory.toPath()));
        getLog().info(compileMessage);
        args.addAll(sources.stream().map(File::getAbsolutePath).collect(Collectors.toList()));

        final ProcessBuilder processBuilder = new ProcessBuilder()
            .directory(compileOutputDirectory)
            .command(args)
            .inheritIO()
            ;

        getLog().debug("Running groovyc via: " + args);

        try {
            final Path compileOutputDirectoryPath = compileOutputDirectory.toPath();
            if (!Files.exists(compileOutputDirectoryPath)) {
                Files.createDirectories(compileOutputDirectoryPath);
            }
            if (!Files.isDirectory(compileOutputDirectoryPath)) {
                throw new MojoExecutionException(
                    "Target directory [" + compileOutputDirectoryPath + "] is not a directory.");
            }
            final Process groovycProcess = processBuilder.start();

            final GroovycLogger outputLogger = new GroovycLogger(groovycProcess.getInputStream(),
                getLog(), LogTarget.INFO);
            final GroovycLogger errorLogger = new GroovycLogger(groovycProcess.getErrorStream(),
                getLog(), LogTarget.ERROR);
            final Thread outputLoggerThread = new Thread(outputLogger);
            outputLoggerThread.start();
            final Thread errorLoggerThread = new Thread(errorLogger);
            errorLoggerThread.start();

            final int groovycRc = groovycProcess.waitFor();
            outputLoggerThread.join();
            errorLoggerThread.join();

            if (groovycRc != 0) {
                throw new MojoExecutionException("Groovy exited with RC=" + groovycRc);
            }
        } catch (final IOException ioException) {
            throw new MojoExecutionException("Error compiling", ioException);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new MojoExecutionException("Compilation interrupted", interruptedException);
        }
    }

    private static String getClassPathString(List<?> classpath) {
        for (Object cp : classpath) {
            if (!(cp instanceof String)) {
                throw new IllegalArgumentException("Classpath element [" + cp + "] is not a string!");
            }
        }

        return classpath.stream()
            .map(obj -> (String) obj)
            .collect(joining(File.pathSeparator));
    }

    private Path getGroovyc() {
        final Optional<Path> groovyHomeGroovyc = getGroovyHomeGroovyc();

        if (groovyHomeGroovyc.isPresent()) {
            return groovyHomeGroovyc.orElseThrow(NoSuchElementException::new);
        }

        final Optional<Path> pathGroovyc = getPathGroovyc();

        return pathGroovyc.orElseThrow(() -> new IllegalStateException("No groovyc found in GROOVY_HOME or PATH!"));
    }

    private Optional<Path> getPathGroovyc() {
        for (String dirname : System.getenv("PATH").split(File.pathSeparator)) {
            final Path groovyc = Paths.get(dirname, "groovyc");
            if (Files.isRegularFile(groovyc) && Files.isExecutable(groovyc)) {
                return Optional.of(groovyc);
            }
        }

        return Optional.empty();
    }

    private Optional<Path> getGroovyHomeGroovyc() {
        final String groovyHome = System.getenv("GROOVY_HOME");

        if (groovyHome == null || groovyHome.trim().isEmpty()) {
            return Optional.empty();
        }

        final Path groovyHomePath = Paths.get(groovyHome);

        if (!Files.isDirectory(groovyHomePath)) {
            return Optional.empty();
        }

        final Path groovyc = groovyHomePath.resolve("bin/groovyc");

        if (Files.exists(groovyc) && Files.isExecutable(groovyc) && Files.isRegularFile(groovyc)) {
            return Optional.of(groovyc);
        }

        return Optional.empty();
    }

    private void doCompileLibrary(Set<File> sources, List classpath, File compileOutputDirectory)
        throws MalformedURLException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        setupClassWrangler(classpath, includeClasspath);

        logPluginClasspath();
        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());

        if (!groovyVersionSupportsAction()) {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support compilation. The minimum version of Groovy required is " + minGroovyVersion + ". Skipping compiling.");
            return;
        }

        if (!skipBytecodeCheck) {
            verifyGroovyVersionSupportsTargetBytecode();
        }

        // get classes we need with reflection
        Class<?> compilerConfigurationClass = classWrangler.getClass("org.codehaus.groovy.control.CompilerConfiguration");
        Class<?> compilationUnitClass = classWrangler.getClass("org.codehaus.groovy.control.CompilationUnit");
        Class<?> groovyClassLoaderClass = classWrangler.getClass("groovy.lang.GroovyClassLoader");

        // setup compile options
        Object compilerConfiguration = setupCompilerConfiguration(compileOutputDirectory, compilerConfigurationClass);
        Object groovyClassLoader = invokeConstructor(findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass), classWrangler.getClassLoader(), compilerConfiguration);
        Object transformLoader = invokeConstructor(findConstructor(groovyClassLoaderClass, ClassLoader.class), classWrangler.getClassLoader());

        // add Groovy sources
        Object compilationUnit = setupCompilationUnit(sources, compilerConfigurationClass, compilationUnitClass, groovyClassLoaderClass, compilerConfiguration, groovyClassLoader, transformLoader);

        // compile the classes
        invokeMethod(findMethod(compilationUnitClass, "compile"), compilationUnit);

        // log compiled classes
        List classes = (List) invokeMethod(findMethod(compilationUnitClass, "getClasses"), compilationUnit);
        getLog().info("Compiled " + classes.size() + " file" + (classes.size() != 1 ? "s" : "") + ".");
    }

    /**
     * Sets up the CompilationUnit to use for compilation.
     *
     * @param sources                    the sources to compile
     * @param compilerConfigurationClass the CompilerConfiguration class
     * @param compilationUnitClass       the CompilationUnit class
     * @param groovyClassLoaderClass     the GroovyClassLoader class
     * @param compilerConfiguration      the CompilerConfiguration
     * @param groovyClassLoader          the GroovyClassLoader
     * @param transformLoader            the GroovyClassLoader to use for transformation
     * @return the CompilationUnit
     * @throws InstantiationException    when a class needed for setting up compilation unit cannot be instantiated
     * @throws IllegalAccessException    when a method needed for setting up compilation unit cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up compilation unit cannot be completed
     */
    protected Object setupCompilationUnit(final Set<File> sources, final Class<?> compilerConfigurationClass, final Class<?> compilationUnitClass, final Class<?> groovyClassLoaderClass, final Object compilerConfiguration, final Object groovyClassLoader, final Object transformLoader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object compilationUnit;
        if (groovyAtLeast(GROOVY_1_6_0)) {
            compilationUnit = invokeConstructor(findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass, groovyClassLoaderClass), compilerConfiguration, null, groovyClassLoader, transformLoader);
        } else {
            compilationUnit = invokeConstructor(findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass), compilerConfiguration, null, groovyClassLoader);
        }
        getLog().debug("Adding Groovy to compile:");
        Method addSourceMethod = findMethod(compilationUnitClass, "addSource", File.class);
        for (File source : sources) {
            getLog().debug("    " + source);
            invokeMethod(addSourceMethod, compilationUnit, source);
        }

        return compilationUnit;
    }

    /**
     * Sets up the CompilationConfiguration to use for compilation.
     *
     * @param compileOutputDirectory     the directory to write the compiled classes to
     * @param compilerConfigurationClass the CompilerConfiguration class
     * @return the CompilerConfiguration
     * @throws ClassNotFoundException    when a class needed for setting up CompilerConfiguration cannot be found
     * @throws InstantiationException    when a class needed for setting up CompilerConfiguration cannot be instantiated
     * @throws IllegalAccessException    when a method needed for setting up CompilerConfiguration cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up CompilerConfiguration cannot be completed
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Object setupCompilerConfiguration(final File compileOutputDirectory, final Class<?> compilerConfigurationClass) throws InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Object compilerConfiguration = invokeConstructor(findConstructor(compilerConfigurationClass));
        if (configScript != null) {
            if (!configScript.exists()) {
                getLog().warn("Configuration script file (" + configScript.getAbsolutePath() + ") doesn't exist. Ignoring configScript parameter.");
            } else if (groovyOlderThan(GROOVY_2_1_0_BETA1)) {
                getLog().warn("Requested to use configScript, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be " + GROOVY_2_1_0_BETA1 + " or newer). Ignoring configScript parameter.");
            } else {
                Class<?> bindingClass = classWrangler.getClass("groovy.lang.Binding");
                Class<?> importCustomizerClass = classWrangler.getClass("org.codehaus.groovy.control.customizers.ImportCustomizer");
                Class<?> groovyShellClass = classWrangler.getClass("groovy.lang.GroovyShell");

                Object binding = invokeConstructor(findConstructor(bindingClass));
                invokeMethod(findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "configuration", compilerConfiguration);
                Object shellCompilerConfiguration = invokeConstructor(findConstructor(compilerConfigurationClass));
                Object importCustomizer = invokeConstructor(findConstructor(importCustomizerClass));
                invokeMethod(findMethod(importCustomizerClass, "addStaticStar", String.class), importCustomizer, "org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder");
                List compilationCustomizers = (List) invokeMethod(findMethod(compilerConfigurationClass, "getCompilationCustomizers"), shellCompilerConfiguration);
                compilationCustomizers.add(importCustomizer);
                Object shell = invokeConstructor(findConstructor(groovyShellClass, ClassLoader.class, bindingClass, compilerConfigurationClass), classWrangler.getClassLoader(), binding, shellCompilerConfiguration);
                getLog().debug("Using configuration script " + configScript + " for compilation.");
                invokeMethod(findMethod(groovyShellClass, "evaluate", File.class), shell, configScript);
            }
        }
        invokeMethod(findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, debug);
        invokeMethod(findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, verbose);
        invokeMethod(findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, warningLevel);
        invokeMethod(findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, tolerance);
        invokeMethod(findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, translateJavacTargetToTargetBytecode(targetBytecode));
        if (previewFeatures) {
            if (isJavaSupportPreviewFeatures()) {
                if (groovyOlderThan(GROOVY_2_5_7) || (groovyAtLeast(GROOVY_2_6_0_ALPHA1) && groovyOlderThan(GROOVY_3_0_0_BETA1))) {
                    getLog().warn("Requested to use preview features, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be " + GROOVY_2_5_7 + "/" + GROOVY_3_0_0_BETA1 + " or newer. No 2.6 version is supported. Ignoring previewFeatures parameter.");
                } else {
                    invokeMethod(findMethod(compilerConfigurationClass, "setPreviewFeatures", boolean.class), compilerConfiguration, previewFeatures);
                }
            } else {
                getLog().warn("Requested to use to use preview features, but your Java version (" + getJavaVersionString() + ") doesn't support it. Ignoring previewFeatures parameter.");
            }
        }
        if (sourceEncoding != null) {
            invokeMethod(findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, sourceEncoding);
        }
        invokeMethod(findMethod(compilerConfigurationClass, "setTargetDirectory", String.class), compilerConfiguration, compileOutputDirectory.getAbsolutePath());
        if (invokeDynamic || groovyAtLeast(GROOVY_4_0_0_ALPHA1)) {
            if (groovyAtLeast(GROOVY_2_0_0_BETA3)) {
                if (classWrangler.isGroovyIndy()) {
                    if (isJavaSupportIndy()) {
                        Map<String, Boolean> optimizationOptions = (Map<String, Boolean>) invokeMethod(findMethod(compilerConfigurationClass, "getOptimizationOptions"), compilerConfiguration);
                        optimizationOptions.put("indy", true);
                        optimizationOptions.put("int", false);
                        getLog().info("invokedynamic enabled.");
                    } else {
                        getLog().warn("Requested to use to use invokedynamic, but your Java version (" + getJavaVersionString() + ") doesn't support it. Ignoring invokeDynamic parameter.");
                    }
                } else {
                    getLog().warn("Requested to use invokedynamic, but your Groovy version doesn't support it (must use have indy classifier). Ignoring invokeDynamic parameter.");
                }
            } else {
                getLog().warn("Requested to use invokeDynamic, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be " + GROOVY_2_0_0_BETA3 + " or newer). Ignoring invokeDynamic parameter.");
            }
        }
        if (parameters) {
            if (groovyAtLeast(GROOVY_2_5_0_ALPHA1)) {
                if (isJavaSupportParameters()) {
                    invokeMethod(findMethod(compilerConfigurationClass, "setParameters", boolean.class), compilerConfiguration, parameters);
                } else {
                    getLog().warn("Requested to use to use parameters, but your Java version (" + getJavaVersionString() + ") doesn't support it. Ignoring parameters parameter.");
                }
            } else {
                getLog().warn("Requested to use parameters, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be " + GROOVY_2_5_0_ALPHA1 + " or newer). Ignoring parameters parameter.");
            }
        }
        if (groovyAtLeast(GROOVY_3_0_5)) {
            if ((parallelParsing == null && groovyAtLeast(GROOVY_4_0_0_ALPHA1)) || (parallelParsing != null && parallelParsing)) {
                Map<String, Boolean> optimizationOptions = (Map<String, Boolean>) invokeMethod(findMethod(compilerConfigurationClass, "getOptimizationOptions"), compilerConfiguration);
                optimizationOptions.put("parallelParse", true);
                getLog().info("Parallel parsing enabled.");
            } else {
                getLog().info("Parallel parsing disabled.");
            }
        }

        return compilerConfiguration;
    }

    /**
     * Throws an exception if targetBytecode is not supported with this version of Groovy. That is, when Groovy added
     * the option to org.codehaus.groovy.control.CompilerConfiguration and used it in
     * org.codehaus.groovy.classgen.asm.WriterController.
     */
    protected void verifyGroovyVersionSupportsTargetBytecode() {
        if ("1.5".equals(targetBytecode) || "5".equals(targetBytecode) || "1.6".equals(targetBytecode) || "6".equals(targetBytecode) || "1.7".equals(targetBytecode) || "7".equals(targetBytecode) || "1.8".equals(targetBytecode) || "8".equals(targetBytecode) || "1.9".equals(targetBytecode) || "9".equals(targetBytecode) || "10".equals(targetBytecode)) {
            if (groovyNewerThan(GROOVY_5_0_0_ALPHA1)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " isn't accepted by Groovy " + GROOVY_5_0_0_ALPHA1 + " or newer.");
            }
        }

        if ("25".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_4_0_27)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_24 + " or newer.");
            }
            if (groovyNewerThan(GROOVY_5_0_0_ALPHA1) && groovyOlderThan(GROOVY_5_0_0_ALPHA13)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_5_0_0_ALPHA13 + " or newer.");
            }
        } else if ("24".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_4_0_24)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_24 + " or newer.");
            }
            if (groovyNewerThan(GROOVY_5_0_0_ALPHA1) && groovyOlderThan(GROOVY_5_0_0_ALPHA11)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_5_0_0_ALPHA11 + " or newer.");
            }
        } else if ("23".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_4_0_21)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_21 + " or newer.");
            }
            if (groovyNewerThan(GROOVY_5_0_0_ALPHA1) && groovyOlderThan(GROOVY_5_0_0_ALPHA8)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_5_0_0_ALPHA8 + " or newer.");
            }
        } else if ("22".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_4_0_16)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_16 + " or newer.");
            }
            if (groovyNewerThan(GROOVY_5_0_0_ALPHA1) && groovyOlderThan(GROOVY_5_0_0_ALPHA3)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_5_0_0_ALPHA3 + " or newer.");
            }
        } else if ("21".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_4_0_11)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_11 + " or newer.");
            }
        } else if ("20".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_4_0_6)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_6 + " or newer.");
            }
        } else if ("19".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_4_0_2)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_2 + " or newer.");
            }
        } else if ("18".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_4_0_0_BETA1)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_0_BETA1 + " or newer.");
            }
        } else if ("17".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_3_0_8) || (groovyAtLeast(GROOVY_4_0_0_ALPHA1) && groovyOlderThan(GROOVY_4_0_0_ALPHA3))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_3_0_8 + "/" + GROOVY_4_0_0_ALPHA3 + " or newer.");
            }
        } else if ("16".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_3_0_6)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_3_0_6 + " or newer.");
            }
        } else if ("15".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_3_0_3)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_3_0_3 + " or newer.");
            }
        } else if ("14".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_3_0_0_BETA2)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_3_0_0_BETA2 + " or newer.");
            }
        } else if ("13".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_2_5_7) || (groovyAtLeast(GROOVY_2_6_0_ALPHA1) && groovyOlderThan(GROOVY_3_0_0_BETA1))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_5_7 + "/" + GROOVY_3_0_0_BETA1 + " or newer. No 2.6 version is supported.");
            }
        } else if ("12".equals(targetBytecode) || "11".equals(targetBytecode) || "10".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_2_5_3) || (groovyAtLeast(GROOVY_2_6_0_ALPHA1) && groovyOlderThan(GROOVY_3_0_0_ALPHA4))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_5_3 + "/" + GROOVY_3_0_0_ALPHA4 + " or newer. No 2.6 version is supported.");
            }
        } else if ("9".equals(targetBytecode) || "1.9".equals(targetBytecode)) {
            if (!isGroovyIndy() && (groovyOlderThan(GROOVY_2_5_3)
                    || (groovyAtLeast(GROOVY_2_6_0_ALPHA1) && groovyOlderThan(GROOVY_2_6_0_ALPHA4))
                    || (groovyAtLeast(GROOVY_3_0_0_ALPHA1) && groovyOlderThan(GROOVY_3_0_0_ALPHA2)))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_5_3 + "/" + GROOVY_2_6_0_ALPHA4 + "/" + GROOVY_3_0_0_ALPHA2 + " or newer.");
            } else if (isGroovyIndy() && (groovyOlderThan(GROOVY_2_5_3) || (groovyAtLeast(GROOVY_2_6_0_ALPHA1) && groovyOlderThan(GROOVY_3_0_0_ALPHA4)))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_5_3 + "/" + GROOVY_3_0_0_ALPHA4 + " or newer. No 2.6 version is supported.");
            }
        } else if ("8".equals(targetBytecode) || "1.8".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_2_3_3)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_3_3 + " or newer.");
            }
        } else if ("7".equals(targetBytecode) || "1.7".equals(targetBytecode) || "6".equals(targetBytecode) || "1.6".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_2_1_3)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_1_3 + " or newer.");
            }
        } else if (!"5".equals(targetBytecode) && !"1.5".equals(targetBytecode) && !"4".equals(targetBytecode) && !"1.4".equals(targetBytecode)) {
            throw new IllegalArgumentException("Unrecognized target bytecode: '" + targetBytecode + "'. This check can be skipped with 'skipBytecodeCheck', but this may result in a different target bytecode being used.");
        }
    }

    protected static String translateJavacTargetToTargetBytecode(String targetBytecode) {
        Map<String, String> javacTargetToTargetBytecode = new HashMap<>();
        javacTargetToTargetBytecode.put("5", "1.5");
        javacTargetToTargetBytecode.put("6", "1.6");
        javacTargetToTargetBytecode.put("7", "1.7");
        javacTargetToTargetBytecode.put("8", "1.8");
        javacTargetToTargetBytecode.put("1.9", "9");
        return javacTargetToTargetBytecode.getOrDefault(targetBytecode, targetBytecode);
    }

}
