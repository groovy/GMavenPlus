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

import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.gmavenplus.model.Version;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.security.CodeSource;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.codehaus.gmavenplus.util.ReflectionUtils.*;


/**
 * The base compile mojo, which all compile mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractCompileMojo extends AbstractGroovySourcesMojo {

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
     *   <li>1.4</li>
     *   <li>1.5</li>
     *   <li>1.6</li>
     *   <li>1.7</li>
     *   <li>1.8</li>
     *   <li>9</li>
     *   <li>10</li>
     *   <li>11</li>
     *   <li>12</li>
     *   <li>13</li>
     * </ul>
     * Using 1.6 or 1.7 requires Groovy &gt;= 2.1.3.
     * Using 1.8 requires Groovy &gt;= 2.3.3.
     * Using 9 requires Groovy &gt;= 2.5.3, or Groovy &gt;= 2.6.0 alpha 4, or Groovy &gt;= 3.0.0 alpha 2.
     * Using 9 with invokedynamic requires Groovy &gt;= 2.5.3, or Groovy &gt;= 3.0.0 alpha 2, but not any 2.6 versions.
     * Using 10, 11, or 12 requires Groovy &gt;= 2.5.3, or Groovy &gt;= 3.0.0 alpha 4, but not any 2.6 versions.
     * Using 13 requires Groovy &gt;= 2.5.7, or Groovy &gt;= 3.0.0-beta-1, but not any 2.6 versions.
     */
    @Parameter(property = "maven.compiler.target", defaultValue = "1.8")
    protected String targetBytecode;

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
     */
    @Parameter(defaultValue = "false")
    protected boolean invokeDynamic;

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
     * Whether to use a shared classloader that includes both the project classpath and plugin classpath.
     * Use only if you know what you're doing.
     *
     * @since 1.6.3
     */
    @Parameter(defaultValue = "false")
    protected boolean useSharedClassLoader;

    /**
     * Whether the bytecode version has preview features enabled (JEP 12).
     * Requires Groovy &gt;= 3.0.0-beta-1 or Groovy &gt;= 2.5.7, but not any 2.6 versions and Java &gt;= 12.
     *
     * @Since 1.7.1
     */
    @Parameter(defaultValue = "false")
    protected boolean previewFeatures;

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
    @SuppressWarnings({"rawtypes"})
    protected synchronized void doCompile(final Set<File> sources, final List classpath, final File compileOutputDirectory)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, MalformedURLException {
        if (sources == null || sources.isEmpty()) {
            getLog().info("No sources specified for compilation. Skipping.");
            return;
        }

        setupClassWrangler(classpath, useSharedClassLoader);

        logPluginClasspath();
        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());

        if (groovyVersionSupportsAction()) {
            verifyGroovyVersionSupportsTargetBytecode();
        } else {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support compilation. The minimum version of Groovy required is " + minGroovyVersion + ". Skipping compiling.");
            return;
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
     * @param compileOutputDirectory the directory to write the compiled classes to
     * @param compilerConfigurationClass the CompilerConfiguration class
     * @return the CompilerConfiguration
     * @throws ClassNotFoundException when a class needed for setting up CompilerConfiguration cannot be found
     * @throws InstantiationException when a class needed for setting up CompilerConfiguration cannot be instantiated
     * @throws IllegalAccessException when a method needed for setting up CompilerConfiguration cannot be accessed
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
                Object shell = invokeConstructor(findConstructor(groovyShellClass, bindingClass, compilerConfigurationClass), binding, shellCompilerConfiguration);
                getLog().debug("Using configuration script " + configScript + " for compilation.");
                invokeMethod(findMethod(groovyShellClass, "evaluate", File.class), shell, configScript);
            }
        }
        invokeMethod(findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, debug);
        invokeMethod(findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, verbose);
        invokeMethod(findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, warningLevel);
        invokeMethod(findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, tolerance);
        invokeMethod(findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, targetBytecode);
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
        if (invokeDynamic) {
            if (groovyAtLeast(GROOVY_2_0_0_BETA3)) {
                if (classWrangler.isGroovyIndy()) {
                    if (isJavaSupportIndy()) {
                        Map<String, Boolean> optimizationOptions = (Map<String, Boolean>) invokeMethod(findMethod(compilerConfigurationClass, "getOptimizationOptions"), compilerConfiguration);
                        optimizationOptions.put("indy", true);
                        optimizationOptions.put("int", false);
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

        return compilerConfiguration;
    }

    /**
     * Throws an exception if targetBytecode is not supported with this version of Groovy. That is, when Groovy added
     * the option to org.codehaus.groovy.control.CompilerConfiguration and used it in
     * org.codehaus.groovy.classgen.asm.WriterController.
     */
    protected void verifyGroovyVersionSupportsTargetBytecode() {
        if ("13".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_2_5_7) || (groovyAtLeast(GROOVY_2_6_0_ALPHA1) && groovyOlderThan(GROOVY_3_0_0_BETA1))) {
                throw new IllegalArgumentException("Target bytecode 13 requires Groovy " + GROOVY_2_5_7 + "/" + GROOVY_3_0_0_BETA1 + " or newer. No 2.6 version is supported.");
            }
        } else if ("12".equals(targetBytecode) || "11".equals(targetBytecode) || "10".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_2_5_3) || (groovyAtLeast(GROOVY_2_6_0_ALPHA1) && groovyOlderThan(GROOVY_3_0_0_ALPHA4))) {
                throw new IllegalArgumentException("Target bytecode 10, 11, or 12 requires Groovy " + GROOVY_2_5_3 + "/" + GROOVY_3_0_0_ALPHA4 + " or newer. No 2.6 version is supported.");
            }
        } else if ("9".equals(targetBytecode)) {
            if (!isGroovyIndy() && (groovyOlderThan(GROOVY_2_5_3)
                    || (groovyAtLeast(GROOVY_2_6_0_ALPHA1) && groovyOlderThan(GROOVY_2_6_0_ALPHA4))
                    || (groovyAtLeast(GROOVY_3_0_0_ALPHA1) && groovyOlderThan(GROOVY_3_0_0_ALPHA2)))) {
                throw new IllegalArgumentException("Target bytecode 9 requires Groovy " + GROOVY_2_5_3 + "/" + GROOVY_2_6_0_ALPHA4 + "/" + GROOVY_3_0_0_ALPHA2 + " or newer.");
            } else if (isGroovyIndy() && (groovyOlderThan(GROOVY_2_5_3) || (groovyAtLeast(GROOVY_2_6_0_ALPHA1) && groovyOlderThan(GROOVY_3_0_0_ALPHA4)))) {
                throw new IllegalArgumentException("Target bytecode 9 with invokedynamic requires Groovy " + GROOVY_2_5_3 + "/" + GROOVY_3_0_0_ALPHA4 + " or newer. No 2.6 version is supported.");
            }
        } else if ("1.8".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_2_3_3)) {
                throw new IllegalArgumentException("Target bytecode 1.8 requires Groovy " + GROOVY_2_3_3 + " or newer.");
            }
        } else if ("1.7".equals(targetBytecode) || "1.6".equals(targetBytecode)) {
            if (groovyOlderThan(GROOVY_2_1_3)) {
                throw new IllegalArgumentException("Target bytecode 1.6 and 1.7 require Groovy " + GROOVY_2_1_3 + " or newer.");
            }
        } else if (!"1.5".equals(targetBytecode) && !"1.4".equals(targetBytecode)) {
            throw new IllegalArgumentException("Unrecognized target bytecode: '" + targetBytecode + "'.");
        }
    }

}
