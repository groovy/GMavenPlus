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

import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ReflectionUtils;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;


/**
 * The base compile mojo, which all compile mojos extend.
 *
 * @author Keegan Witt
 */
public abstract class AbstractCompileMojo extends AbstractGroovySourcesMojo {

    /**
     * The minimum version of Groovy that this mojo supports.
     */
    protected static final Version MIN_GROOVY_VERSION = new Version(1, 5, 0);

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
     * Allow setting whether to support invokeDynamic (requires Java 7 or greater).
     *
     * @parameter property="invokeDynamic" default-value="false"
     */
    protected boolean invokeDynamic;

    /**
     * Performs compilation of compile mojos.
     *
     * @param sourcesToCompile The sources to compile
     * @param classpath The classpath to use for compilation
     * @param compileOutputDirectory The directory to write the compiled class files to
     * @throws ClassNotFoundException When a class needed for compilation cannot be found
     * @throws InstantiationException When a class needed for compilation cannot be instantiated
     * @throws IllegalAccessException When a method needed for compilation cannot be accessed
     * @throws InvocationTargetException When a reflection invocation needed for compilation cannot be completed
     * @throws java.net.MalformedURLException When a classpath element provides a malformed URL
     */
    @SuppressWarnings("unchecked")
    protected synchronized void doCompile(final Set<File> sourcesToCompile, final List classpath, final File compileOutputDirectory)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, MalformedURLException {
        if (sourcesToCompile == null || sourcesToCompile.isEmpty()) {
            getLog().info("No sources specified for compilation.  Skipping.");
            return;
        }

        // get classes we need with reflection
        Class<?> compilerConfigurationClass = Class.forName("org.codehaus.groovy.control.CompilerConfiguration");
        Class<?> compilationUnitClass = Class.forName("org.codehaus.groovy.control.CompilationUnit");
        Class<?> groovyClassLoaderClass = Class.forName("groovy.lang.GroovyClassLoader");

        // set up compile options
        Object compilerConfiguration = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(compilerConfigurationClass));
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, debug);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, verbose);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, warningLevel);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, tolerance);
        if (getGroovyVersion().compareTo(new Version(2, 1, 3)) >= 0) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, targetBytecode);
        } else {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, "1.5");
        }
        if (sourceEncoding != null) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, sourceEncoding);
        }
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetDirectory", String.class), compilerConfiguration, compileOutputDirectory.getAbsolutePath());
        if (getGroovyVersion().compareTo(new Version(2, 0, 0, "beta-3")) >= 0 && invokeDynamic) {
            if (isGroovyIndy()) {
                Map<java.lang.String, java.lang.Boolean> optimizationOptions = (Map<String, Boolean>) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "getOptimizationOptions"), compilerConfiguration);
                optimizationOptions.put("indy", true);
                optimizationOptions.put("int", false);
            } else {
                getLog().warn("Requested to use InvokeDynamic option but the version of Groovy on the project classpath doesn't support it.  Ignoring invokeDynamic option.");
            }
        }

        // append project classpath to groovyClassLoader and transformLoader
        Object groovyClassLoader = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass), compilationUnitClass.getClassLoader(), compilerConfiguration);
        Object transformLoader = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class), compilationUnitClass.getClassLoader());
        getLog().debug("Classpath: ");
        if (classpath != null) {
            for (Object classpathElement : classpath) {
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyClassLoaderClass, "addURL", URL.class), groovyClassLoader, new File((String) classpathElement).toURI().toURL());
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyClassLoaderClass, "addURL", URL.class), transformLoader, new File((String) classpathElement).toURI().toURL());
                getLog().debug("    " + classpathElement);
            }
        }

        // add Groovy sources
        Object compilationUnit;
        if (getGroovyVersion().compareTo(new Version(1, 6, 0)) >= 0) {
            compilationUnit = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass, groovyClassLoaderClass), compilerConfiguration, null, groovyClassLoader, transformLoader);
        } else {
            compilationUnit = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass), compilerConfiguration, null, groovyClassLoader);
        }
        getLog().debug("Adding Groovy to compile:");
        for (File source : sourcesToCompile) {
            getLog().debug("    " + source);
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "addSource", File.class), compilationUnit, source);
        }

        // compile the classes
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "compile"), compilationUnit);

        // log compiled classes
        List classes = (List) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "getClasses"), compilationUnit);
        getLog().info("Compiled " + classes.size() + " file" + (classes.size() > 1 || classes.size() == 0 ? "s" : "") + ".");
    }

    /**
     * Determines whether this mojo can be run with the version of Groovy supplied.
     * Must be >= 1.5.0 because not all the classes needed were available and
     * functioning correctly in previous versions.
     *
     * @return <code>true</code> only if the version of Groovy supports this mojo.
     */
    protected boolean groovyVersionSupportsAction() {
        return getGroovyVersion() != null && getGroovyVersion().compareTo(MIN_GROOVY_VERSION) >= 0;
    }

}
