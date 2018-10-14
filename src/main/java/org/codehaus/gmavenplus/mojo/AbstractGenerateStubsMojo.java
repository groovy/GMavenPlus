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
import org.codehaus.gmavenplus.util.ClassWrangler;
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
     * For some reason, the JavaStubCompilationUnit is silently not creating my
     * stubs (although it does create the target directory) when I use other
     * versions.
     */

    /**
     * Groovy 2.9.0 beta-1 version.
     */
    protected static final Version GROOVY_1_9_0_BETA1 = new Version(1, 9, 0, "beta-1");

    /**
     * Groovy 1.9.0 beta-3 version.
     */
    protected static final Version GROOVY_1_9_0_BETA3 = new Version(1, 9, 0, "beta-3");

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
     * The file extensions of Groovy source files.
     * @since 1.0-beta-2
     */
    @Parameter
    protected Set<String> scriptExtensions;

    /**
     * The Groovy compiler bytecode compatibility.  One of
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
     * </ul>
     * Using 1.6 or 1.7 requires Groovy >= 2.1.3.
     * Using 1.8 requires Groovy >= 2.3.3.
     * Using 9 requires Groovy >= 2.5.3, or Groovy >= 2.6.0 alpha 4, or Groovy >= 3.0.0 alpha 2.
     * Using 9 with invokedynamic requires Groovy >= 2.5.3, or Groovy >= 3.0.0 alpha 2, but not any 2.6 versions.
     * Using 10, 11, or 12 requires Groovy >= 2.5.3, or Groovy >= 3.0.0 alpha 4, but not any 2.6 versions.
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
     */
    @Parameter(defaultValue = "1")
    protected int warningLevel;

    /**
     * Groovy compiler error tolerance (the number of non-fatal errors
     * (per unit) that should be tolerated before compilation is aborted).
     */
    @Parameter(defaultValue = "0")
    protected int tolerance;

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
    protected synchronized void doStubGeneration(final Set<File> stubSources, final List classpath, final File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        if (stubSources == null || stubSources.isEmpty()) {
            getLog().info("No sources specified for stub generation.  Skipping.");
            return;
        }

        classWrangler = new ClassWrangler(classpath, getLog());

        logPluginClasspath();
        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());

        if (!groovyVersionSupportsAction()) {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support stub generation.  The minimum version of Groovy required is " + minGroovyVersion + ".  Skipping stub generation.");
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
    protected Object setupCompilerConfiguration(final File outputDirectory, final Class compilerConfigurationClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object compilerConfiguration = invokeConstructor(findConstructor(compilerConfigurationClass));
        invokeMethod(findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, debug);
        invokeMethod(findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, verbose);
        invokeMethod(findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, warningLevel);
        invokeMethod(findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, tolerance);
        invokeMethod(findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, targetBytecode);
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
                DotGroovyFile dotGroovyFile = new DotGroovyFile(stubSource);
                dotGroovyFile.setScriptExtensions(scriptExtensions);
                invokeMethod(addSource, javaStubCompilationUnit, dotGroovyFile);
            }
        }
    }

    protected boolean supportsSettingExtensions() {
        return groovyAtLeast(GROOVY_1_8_3) && (groovyOlderThan(GROOVY_1_9_0_BETA1) || groovyNewerThan(GROOVY_1_9_0_BETA3));
    }

    protected void logGeneratedStubs(File outputDirectory) {
        Set<File> stubs = getStubs(outputDirectory);
        getLog().info("Generated " + stubs.size() + " stub" + (stubs.size() > 1 || stubs.size() == 0 ? "s" : "") + ".");
    }

    /**
     * This is a fix for http://jira.codehaus.org/browse/MGROOVY-187
     * It modifies the dates of the created stubs to 1/1/1970, ensuring that
     * the Java compiler will not overwrite perfectly good compiled Groovy
     * just because it has a newer source stub.  Basically, this prevents the
     * stubs from causing a side effect with the Java compiler, but still
     * allows stubs to work with JavaDoc.
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
