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
import org.codehaus.gmavenplus.groovyworkarounds.DotGroovyFile;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.FileUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.*;

import static org.codehaus.gmavenplus.util.ReflectionUtils.*;


/**
 * The base generate stubs mojo, which all generate stubs mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractGenerateStubsMojo extends AbstractGroovyStubSourcesMojo {
    /*
     * TODO: support Groovy 1.5.0 - 1.8.1?
     * For some reason, the JavaStubCompilationUnit is silently not creating my stubs
     * (although it does create the target directory) when I use other versions.
     */

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
     * Groovy 2.3.3 version.
     */
    protected static final Version GROOVY_2_3_3 = new Version(2, 3, 3);

    /**
     * Groovy 2.1.3 version.
     */
    protected static final Version GROOVY_2_1_3 = new Version(2, 1, 3);

    /**
     * Groovy 2.9.0 beta-1 version.
     */
    protected static final Version GROOVY_1_9_0_BETA1 = new Version(1, 9, 0, "beta-1");

    /**
     * Groovy 1.9.0 beta-3 version.
     */
    protected static final Version GROOVY_1_9_0_BETA3 = new Version(1, 9, 0, "beta-3");

    /**
     * Groovy 1.8.2 version.
     */
    protected static final Version GROOVY_1_8_2 = new Version(1, 8, 2);

    /**
     * Groovy 1.8.3 version.
     */
    protected static final Version GROOVY_1_8_3 = new Version(1, 8, 3);

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
     *
     * @since 1.0-beta-3
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
     * Performs the stub generation on the specified source files.
     *
     * @param stubSources the sources to perform stub generation on
     * @param classpath The classpath to use for compilation
     * @param outputDirectory the directory to write the stub files to
     * @throws ClassNotFoundException when a class needed for stub generation cannot be found
     * @throws InstantiationException when a class needed for stub generation cannot be instantiated
     * @throws IllegalAccessException when a method needed for stub generation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for stub generation cannot be completed
     * @throws MalformedURLException when a classpath element provides a malformed URL
     */
    protected synchronized void doStubGeneration(final Set<File> stubSources, final List<?> classpath, final File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        if (stubSources == null || stubSources.isEmpty()) {
            getLog().info("No sources specified for stub generation. Skipping.");
            return;
        }

        setupClassWrangler(classpath, useSharedClassLoader);

        logPluginClasspath();
        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());

        if (groovyVersionSupportsAction()) {
            verifyGroovyVersionSupportsTargetBytecode();
        } else {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support stub generation. The minimum version of Groovy required is " + minGroovyVersion + ". Skipping stub generation.");
            return;
        }

        // get classes we need with reflection
        Class<?> compilerConfigurationClass = classWrangler.getClass("org.codehaus.groovy.control.CompilerConfiguration");
        Class<?> javaStubCompilationUnitClass = classWrangler.getClass("org.codehaus.groovy.tools.javac.JavaStubCompilationUnit");
        Class<?> groovyClassLoaderClass = classWrangler.getClass("groovy.lang.GroovyClassLoader");

        // setup stub generation options
        Object compilerConfiguration = setupCompilerConfiguration(outputDirectory, compilerConfigurationClass);
        Object groovyClassLoader = invokeConstructor(findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass), classWrangler.getClassLoader(), compilerConfiguration);
        Object javaStubCompilationUnit = invokeConstructor(findConstructor(javaStubCompilationUnitClass, compilerConfigurationClass, groovyClassLoaderClass, File.class), compilerConfiguration, groovyClassLoader, outputDirectory);

        // add Groovy sources
        addGroovySources(stubSources, compilerConfigurationClass, javaStubCompilationUnitClass, compilerConfiguration, javaStubCompilationUnit);

        // generate the stubs
        invokeMethod(findMethod(javaStubCompilationUnitClass, "compile"), javaStubCompilationUnit);
    }

    /**
     * Sets up the CompilerConfiguration to use for stub generation.
     *
     * @param outputDirectory the directory to write the stub files to
     * @param compilerConfigurationClass the CompilerConfiguration class
     * @return the CompilerConfiguration to use for stub generation
     * @throws InstantiationException when a class needed for stub generation cannot be instantiated
     * @throws IllegalAccessException when a method needed for stub generation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for stub generation cannot be completed
     */
    protected Object setupCompilerConfiguration(final File outputDirectory, final Class<?> compilerConfigurationClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object compilerConfiguration = invokeConstructor(findConstructor(compilerConfigurationClass));
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
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("stubDir", outputDirectory);
        options.put("keepStubs", Boolean.TRUE);
        invokeMethod(findMethod(compilerConfigurationClass, "setJointCompilationOptions", Map.class), compilerConfiguration, options);

        return compilerConfiguration;
    }

    /**
     * Adds the Groovy sources to the CompilationUnit.
     *
     * @param stubSources the sources to perform stub generation on
     * @param compilerConfigurationClass the CompilerConfiguration class
     * @param javaStubCompilationUnitClass the JavaStubCompilationUnit class
     * @param compilerConfiguration the CompilerConfiguration to use for stub generation
     * @param javaStubCompilationUnit the JavaStubCompilationUnit to use for stub generation
     * @throws IllegalAccessException when a method needed for stub generation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for stub generation cannot be completed
     */
    protected void addGroovySources(final Set<File> stubSources, final Class<?> compilerConfigurationClass, final Class<?> javaStubCompilationUnitClass, final Object compilerConfiguration, final Object javaStubCompilationUnit) throws InvocationTargetException, IllegalAccessException {
        Set<String> scriptExtensions = new HashSet<String>();
        for (File stubSource : stubSources) {
            scriptExtensions.add(FileUtils.getFileExtension(stubSource));
        }
        getLog().debug("Detected Groovy file extensions: "+ scriptExtensions + ".");
        if (supportsSettingExtensions()) {
            invokeMethod(findMethod(compilerConfigurationClass, "setScriptExtensions", Set.class), compilerConfiguration, scriptExtensions);
        }
        getLog().debug("Adding Groovy to generate stubs for:");
        Method addSource = findMethod(javaStubCompilationUnitClass, "addSource", File.class);
        for (File stubSource : stubSources) {
            getLog().debug("    " + stubSource);
            if (supportsSettingExtensions()) {
                invokeMethod(addSource, javaStubCompilationUnit, stubSource);
            } else {
                DotGroovyFile dotGroovyFile = new DotGroovyFile(stubSource).setScriptExtensions(scriptExtensions);
                invokeMethod(addSource, javaStubCompilationUnit, dotGroovyFile);
            }
        }
    }

    /**
     * Determines whether the version of Groovy supports stub generation.
     *
     * @return <code>true</code> if the version of Groovy supports stub generation, <code>false</code> otherwise
     */
    protected boolean supportsSettingExtensions() {
        return groovyAtLeast(GROOVY_1_8_3) && (groovyOlderThan(GROOVY_1_9_0_BETA1) || groovyNewerThan(GROOVY_1_9_0_BETA3));
    }

    /**
     * Logs the stubs that have been generated.
     *
     * @param outputDirectory the output directory for the stubs
     */
    protected void logGeneratedStubs(File outputDirectory) {
        Set<File> stubs = getStubs(outputDirectory);
        getLog().info("Generated " + stubs.size() + " stub" + (stubs.size() > 1 || stubs.size() == 0 ? "s" : "") + ".");
    }

    /**
     * This is a fix for http://jira.codehaus.org/browse/MGROOVY-187
     * It modifies the dates of the created stubs to 1/1/1970, ensuring that the Java compiler will not overwrite perfectly
     * good compiled Groovy just because it has a newer source stub. Basically, this prevents the stubs from causing a
     * side-effect with the Java compiler, but still allows stubs to work with JavaDoc.
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
