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

import org.codehaus.gmavenplus.groovyworkarounds.DotGroovyFile;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The base generate stubs mojo, which all generate stubs mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractGenerateStubsMojo extends AbstractGroovyStubSourcesMojo {
    // TODO: support Groovy 1.5.0 - 1.8.1?
    /*
     * For some reason, the JavaStubCompilationUnit is silently not creating my
     * stubs (although it does create the target directory) when I use other
     * versions.
     */

    /**
     * The encoding of source files.
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     */
    protected String sourceEncoding;

    /**
     * The file extensions of Groovy source files.
     * @since 1.0-beta-2
     *
     * @parameter
     */
    protected Set<String> scriptExtensions;

    /**
     * The Groovy compiler bytecode compatibility.  One of
     * <ul>
     *   <li>1.4</li>
     *   <li>1.5</li>
     *   <li>1.6</li>
     *   <li>1.7</li>
     *   <li>1.8</li>
     * </ul>
     * Note that prior to Groovy 2.1.3, only 1.4 and 1.5 were supported.
     * If an invalid selection is made, Groovy will default to VM determined
     * version.
     * @since 1.0-beta-3
     *
     * @parameter default-value="1.5"
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
     * (the number of non-fatal errors (per unit) that should be tolerated before compilation is aborted).
     *
     * @parameter default-value="0"
     */
    protected int tolerance;

    /**
     * Performs the stub generation on the specified source files.
     *
     * @param stubSources the sources to perform stub generation on
     * @param classpath The classpath to use for compilation
     * @param outputDirectory the directory to write the stub files to
     * @throws ClassNotFoundException When a class needed for stub generation cannot be found
     * @throws InstantiationException When a class needed for stub generation cannot be instantiated
     * @throws IllegalAccessException When a method needed for stub generation cannot be accessed
     * @throws InvocationTargetException When a reflection invocation needed for stub generation cannot be completed
     * @throws MalformedURLException When a classpath element provides a malformed URL
     */
    protected synchronized void doStubGeneration(final Set<File> stubSources, final List classpath, final File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        if (stubSources == null || stubSources.isEmpty()) {
            getLog().info("No sources specified for stub generation.  Skipping.");
            return;
        }

        // create an isolated ClassLoader with all the appropriate project dependencies in it
        ClassLoader isolatedClassLoader = createNewClassLoader(classpath);

        // get classes we need with reflection
        Class compilerConfigurationClass = Class.forName("org.codehaus.groovy.control.CompilerConfiguration", true, isolatedClassLoader);
        Class javaStubCompilationUnitClass = Class.forName("org.codehaus.groovy.tools.javac.JavaStubCompilationUnit", true, isolatedClassLoader);
        Class groovyClassLoaderClass = Class.forName("groovy.lang.GroovyClassLoader", true, isolatedClassLoader);

        // set up compile options
        Object compilerConfiguration = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(compilerConfigurationClass));
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, debug);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, verbose);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, warningLevel);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, tolerance);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, targetBytecode);
        if (sourceEncoding != null) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, sourceEncoding);
        }
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("stubDir", outputDirectory);
        options.put("keepStubs", Boolean.TRUE);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setJointCompilationOptions", Map.class), compilerConfiguration, options);

        // create groovyClassLoader isolated classloader (with project classpath)
        Object groovyClassLoader = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass), isolatedClassLoader, compilerConfiguration);
        Object javaStubCompilationUnit = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(javaStubCompilationUnitClass, compilerConfigurationClass, groovyClassLoaderClass, File.class), compilerConfiguration, groovyClassLoader, outputDirectory);

        // add Groovy sources
        getLog().debug("Adding Groovy to generate stubs for:");
        for (File source : stubSources) {
            if (getLog().isDebugEnabled()) {
                getLog().debug("    " + source);
            }
            if (getGroovyVersion().compareTo(new Version(1, 8, 3)) >= 0 && (getGroovyVersion().compareTo(new Version(1, 9, 0, "beta-1")) < 0 || getGroovyVersion().compareTo(new Version(1, 9, 0, "beta-3")) > 0)) {
                Set<String> extensions;
                if (scriptExtensions != null && !scriptExtensions.isEmpty()) {
                    extensions = scriptExtensions;
                } else {
                    extensions = DotGroovyFile.defaultScriptExtensions();
                }
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setScriptExtensions", Set.class), compilerConfiguration, extensions);
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(javaStubCompilationUnitClass, "addSource", File.class), javaStubCompilationUnit, source);
            } else {
                DotGroovyFile dotGroovyFile = new DotGroovyFile(source);
                Set<String> extensions;
                if (scriptExtensions != null && !scriptExtensions.isEmpty()) {
                    extensions = scriptExtensions;
                } else {
                    extensions = DotGroovyFile.defaultScriptExtensions();
                }
                dotGroovyFile.setScriptExtensions(extensions);
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(javaStubCompilationUnitClass, "addSource", File.class), javaStubCompilationUnit, dotGroovyFile);
            }
        }

        // generate the stubs
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(javaStubCompilationUnitClass, "compile"), javaStubCompilationUnit);

        // log generated stubs
        getLog().info("Generated " + getStubs().size() + " stub" + (getStubs().size() > 1 || getStubs().size() == 0 ? "s" : "") + ".");
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
