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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.codehaus.gmavenplus.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.CodeSource;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The base compile mojo, which all compile mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractCompileMojo extends AbstractGroovySourcesMojo {
    /** Groovy 2.1.0 beta-3 version. */
    protected static final Version GROOVY_2_1_0_BETA3 = new Version(2, 1, 0, "beta-3");

    /** Groovy 2.1.0 beta-1 version. */
    protected static final Version GROOVY_2_1_0_BETA1 = new Version(2, 1, 0, "beta-1");

    /** Groovy 1.6.0 version. */
    protected static final Version GROOVY_1_6_0 = new Version(1, 6, 0);

    /**
     * The location for the compiled classes.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     */
    protected File outputDirectory;

    /**
     * The location for the compiled test classes.
     *
     * @parameter default-value="${project.build.testOutputDirectory}"
     */
    protected File testOutputDirectory;

    /**
     * The encoding of source files.
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     */
    protected String sourceEncoding;

    /**
     * The Groovy compiler bytecode compatibility.  One of
     * <ul>
     *   <li>1.4</li>
     *   <li>1.5</li>
     *   <li>1.6</li>
     *   <li>1.7</li>
     *   <li>1.8</li>
     * </ul>
     * Using 1.6 or 1.7 requires Groovy >= 2.1.3, and using 1.8 requires Groovy >= 2.3.3.
     * If an invalid selection is made, Groovy will default to VM determined
     * version (1.4 or 1.5).
     *
     * @parameter property="maven.compiler.target" default-value="1.5"
     */
    protected String targetBytecode;

    /**
     * Whether Groovy compiler should be set to debug.
     *
     * @parameter default-value="false"
     */
    protected boolean debug;

    /**
     * Whether Groovy compiler should be set to verbose.
     *
     * @parameter default-value="false"
     */
    protected boolean verbose;

    /**
     * Groovy compiler warning level.  Should be one of:
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
     *
     * @parameter default-value="1"
     */
    protected int warningLevel;

    /**
     * Groovy compiler error tolerance
     * (the number of non-fatal errors (per unit) that should be tolerated
     * before compilation is aborted).
     *
     * @parameter default-value="0"
     */
    protected int tolerance;

    /**
     * Whether to support invokeDynamic (requires Java 7 or greater and Groovy
     * indy 2.0.0-beta-3 or greater).
     *
     * @parameter property="invokeDynamic" default-value="false"
     */
    protected boolean invokeDynamic;

    /**
     * A <a href="http://groovy.codehaus.org/Advanced+compiler+configuration">script</a>
     * for tweaking the configuration options (requires Groovy 2.1.0-beta-1
     * or greater).  Note that its encoding must match your source encoding.
     *
     * @parameter property="configScript"
     */
    protected File configScript;

    /**
     * Performs compilation of compile mojos.
     *
     * @param sources the sources to compile
     * @param classpath the classpath to use for compilation
     * @param compileOutputDirectory the directory to write the compiled class files to
     * @throws ClassNotFoundException when a class needed for compilation cannot be found
     * @throws InstantiationException when a class needed for compilation cannot be instantiated
     * @throws IllegalAccessException when a method needed for compilation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for compilation cannot be completed
     * @throws MalformedURLException when a classpath element provides a malformed URL
     */
    @SuppressWarnings("unchecked")
    protected synchronized void doCompile(final Set<File> sources, final List classpath, final File compileOutputDirectory)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, MalformedURLException {
        classWrangler = new ClassWrangler(classpath, getLog());

        if (sources == null || sources.isEmpty()) {
            getLog().info("No sources specified for compilation.  Skipping.");
            return;
        }
        if (groovyVersionSupportsAction()) {
            classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());
            logPluginClasspath();
            if (getLog().isDebugEnabled()) {
                try {
                    getLog().debug("Project compile classpath:\n" + project.getCompileClasspathElements());
                } catch (DependencyResolutionRequiredException e) {
                    getLog().warn("Unable to log project compile classpath", e);
                }
            }
        } else {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersion() + ") doesn't support compilation.  The minimum version of Groovy required is " + minGroovyVersion + ".  Skipping compiling.");
            return;
        }

        // get classes we need with reflection
        Class compilerConfigurationClass = classWrangler.getClass("org.codehaus.groovy.control.CompilerConfiguration");
        Class compilationUnitClass = classWrangler.getClass("org.codehaus.groovy.control.CompilationUnit");
        Class groovyClassLoaderClass = classWrangler.getClass("groovy.lang.GroovyClassLoader");

        // setup compile options
        Object compilerConfiguration = setupCompilerConfiguration(compileOutputDirectory, compilerConfigurationClass);
        Object groovyClassLoader = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass), classWrangler.getClassLoader(), compilerConfiguration);
        Object transformLoader = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class), classWrangler.getClassLoader());

        // add Groovy sources
        Object compilationUnit = setupCompilationUnit(sources, compilerConfigurationClass, compilationUnitClass, groovyClassLoaderClass, compilerConfiguration, groovyClassLoader, transformLoader);

        // compile the classes
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "compile"), compilationUnit);

        // log compiled classes
        List classes = (List) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "getClasses"), compilationUnit);
        getLog().info("Compiled " + classes.size() + " file" + (classes.size() > 1 || classes.size() == 0 ? "s" : "") + ".");
    }

    /**
     * Sets up the CompilationUnit to use for compilation.
     *
     * @param sources the sources to compile
     * @param compilerConfigurationClass the CompilerConfiguration class
     * @param compilationUnitClass the CompilationUnit class
     * @param groovyClassLoaderClass the GroovyClassLoader class
     * @param compilerConfiguration the CompilerConfiguration
     * @param groovyClassLoader the GroovyClassLoader
     * @param transformLoader the GroovyClassLoader to use for transformation
     * @return the CompilationUnit
     * @throws InstantiationException when a class needed for setting up compilation unit cannot be instantiated
     * @throws IllegalAccessException when a method needed for setting up compilation unit cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up compilation unit cannot be completed
     */
    protected Object setupCompilationUnit(final Set<File> sources, final Class compilerConfigurationClass, final Class compilationUnitClass, final Class groovyClassLoaderClass, final Object compilerConfiguration, final Object groovyClassLoader, final Object transformLoader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object compilationUnit;
        if (classWrangler.getGroovyVersion().compareTo(GROOVY_1_6_0) >= 0) {
            compilationUnit = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass, groovyClassLoaderClass), compilerConfiguration, null, groovyClassLoader, transformLoader);
        } else {
            compilationUnit = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass), compilerConfiguration, null, groovyClassLoader);
        }
        getLog().debug("Adding Groovy to compile:");
        for (File source : sources) {
            if (getLog().isDebugEnabled()) {
                getLog().debug("    " + source);
            }
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "addSource", File.class), compilationUnit, source);
        }

        return compilationUnit;
    }

    /**
     * Sets up the CompilationConfiguration to use for compilation.
     *
     * @param compileOutputDirectory the directory to write the compiled classes to
     * @param compilerConfigurationClass the CompilerConfiguration class
     * @return the CompilerConfiguration
     * @throws ClassNotFoundException when a class needed for setting up CompilerConfiguration cannot be found
     * @throws InstantiationException when a class needed for setting up CompilerConfiguration cannot be instantiated
     * @throws IllegalAccessException when a method needed for setting up CompilerConfiguration cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up CompilerConfiguration cannot be completed
     */
    @SuppressWarnings("unchecked")
    protected Object setupCompilerConfiguration(final File compileOutputDirectory, final Class compilerConfigurationClass) throws InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Object compilerConfiguration = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(compilerConfigurationClass));
        if (configScript != null) {
            if (classWrangler.getGroovyVersion().compareTo(GROOVY_2_1_0_BETA1) >= 0) {
                Class bindingClass = classWrangler.getClass("groovy.lang.Binding");
                Class importCustomizerClass = classWrangler.getClass("org.codehaus.groovy.control.customizers.ImportCustomizer");
                Class groovyShellClass = classWrangler.getClass("groovy.lang.GroovyShell");

                Object binding = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(bindingClass));
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(bindingClass, "setVariable", String.class, Object.class), binding, "configuration", compilerConfiguration);
                Object shellCompilerConfiguration = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(compilerConfigurationClass));
                Object importCustomizer = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(importCustomizerClass));
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(importCustomizerClass, "addStaticStar", String.class), importCustomizer, "org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder");
                List compilationCustomizers = (List) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "getCompilationCustomizers"), shellCompilerConfiguration);
                compilationCustomizers.add(importCustomizer);
                Object shell = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyShellClass, bindingClass, compilerConfigurationClass), binding, shellCompilerConfiguration);
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyShellClass, "evaluate", File.class), shell , configScript);
            } else {
                getLog().warn("Requested to use configScript, but your Groovy version doesn't support it (must be 2.1.0-beta-1 or newer).  Ignoring configScript parameter.");
            }
        }
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, debug);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, verbose);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, warningLevel);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, tolerance);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, targetBytecode);
        if (sourceEncoding != null) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, sourceEncoding);
        }
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetDirectory", String.class), compilerConfiguration, compileOutputDirectory.getAbsolutePath());
        if (invokeDynamic) {
            if (classWrangler.getGroovyVersion().compareTo(GROOVY_2_1_0_BETA3) >= 0) {
                if (classWrangler.isGroovyIndy()) {
                    if (isJavaSupportIndy()) {
                        Map<String, Boolean> optimizationOptions = (Map<String, Boolean>) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "getOptimizationOptions"), compilerConfiguration);
                        optimizationOptions.put("indy", true);
                        optimizationOptions.put("int", false);
                    } else {
                        getLog().warn("Tried to use to use an indy version of Groovy, but your Java version (" + getJavaVersionString() + ") doesn't support it.  Ignoring invokeDynamic parameter.");
                    }
                } else {
                    getLog().warn("Requested to use InvokeDynamic, but the version of Groovy on the project classpath doesn't support it (must use have indy classifier).  Ignoring invokeDynamic parameter.");
                }
            } else {
                getLog().warn("Requested to use invokeDynamic, but your Groovy version doesn't support it (must be 2.0.0-beta-3 or newer).  Ignoring invokeDynamic parameter.");
            }
        }

        return compilerConfiguration;
    }

}
