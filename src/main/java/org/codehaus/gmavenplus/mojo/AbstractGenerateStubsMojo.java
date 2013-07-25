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
import org.codehaus.gmavenplus.groovyworkarounds.DotGroovyFile;
import org.codehaus.gmavenplus.util.ReflectionUtils;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;


/**
 * The base generate stubs mojo, which all generate stubs mojos extend.
 *
 * @author Keegan Witt
 */
public abstract class AbstractGenerateStubsMojo extends AbstractGroovyMojo {
    // TODO: support Groovy 1.5.0 - 1.6.9?
    /*
     * For some reason, the JavaStubCompilationUnit is silently not creating my
     * stubs (although it does create the target directory) when I use older
     * versions.
     */

    /**
     * The minimum version of Groovy that this mojo supports.
     */
    protected static final Version MIN_GROOVY_VERSION = new Version(1, 7, 0);

    /**
     * The Groovy source files (relative paths).
     * Default: "${project.basedir}/src/main/groovy/&#42;&#42;/&#42;.groovy"
     *
     * @parameter
     */
    protected FileSet[] sources;

    /**
     * The location for the compiled classes.
     *
     * @parameter default-value="${project.build.directory}/generated-sources/groovy-stubs/main"
     */
    protected File stubsOutputDirectory;

    /**
     * The Groovy test source files (relative paths).
     * Default: "${project.basedir}/src/test/groovy/&#42;&#42;/&#42;.groovy"
     *
     * @parameter
     */
    protected FileSet[] testSources;

    /**
     * The location for the compiled test classes.
     *
     * @parameter default-value="${project.build.directory}/generated-sources/groovy-stubs/test"
     */
    protected File testStubsOutputDirectory;

    /**
     * The encoding of source files.
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     */
    protected String sourceEncoding;

    /**
     * The file extensions of Groovy source files.
     *
     * @parameter
     */
    protected Set<String> scriptExtensions;

    // if plugin only runs on 1.5, then can assume 1.5
    /**
     * The Groovy compiler bytecode compatibility ("1.4" or "1.5").
     *
     * @parameter default-value="1.5"
     */
//    protected String targetBytecode;

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
     * <ul>
     *   <li>"0" (None)</li>
     *   <li>"1" (Likely Errors)</li>
     *   <li>"2" (Possible Errors)</li>
     *   <li>"3" (Paranoia)</li>
     * </ul>
     *
     * @parameter default-value="0"@
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
     * Gets the set of files for the main sources.
     *
     * @return The set of files for the main sources.
     */
    protected Set<File> getSources() {
        return getFiles(sources, "main");
    }

    /**
     * Gets the set of files of the test sources.
     *
     * @return The set of files of the test sources.
     */
    protected Set<File> getTestSources() {
        return getFiles(testSources, "test");
    }

    /**
     * Gets the set of included files from the specified source files or source directory (if sources are null).
     *
     * @param fromSources The sources to get the included files from
     * @param defaultSourceDirectory The source directory to fall back on if sources are null
     *
     * @return The included files from the specified sources.
     */
    protected Set<File> getFiles(final FileSet[] fromSources, final String defaultSourceDirectory) {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        if (fromSources != null) {
            for (FileSet fileSet : fromSources) {
                for (String include : Arrays.asList(fileSetManager.getIncludedFiles(fileSet))) {
                    files.add(new File(project.getBasedir().getAbsolutePath() + File.separator + fileSet.getDirectory(), include));
                }
            }
        } else {
            FileSet fileSet = new FileSet();
            String directory = project.getBasedir().getAbsolutePath() + File.separator + "src" + File.separator + defaultSourceDirectory + File.separator + "groovy";
            fileSet.setDirectory(directory);
            fileSet.setIncludes(Arrays.asList(DEFAULT_SOURCE_PATTERN));
            for (String file : fileSetManager.getIncludedFiles(fileSet)) {
                files.add(new File(directory, file));
            }
        }

        return files;
    }

    /**
     * Gets the set of files of the main stubs.
     *
     * @return The set of files of the main stubs
     */
    protected Set<File> getStubs() {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        FileSet fileSet = new FileSet();
        fileSet.setDirectory(stubsOutputDirectory.getAbsolutePath());
        fileSet.setIncludes(Arrays.asList(DEFAULT_STUB_PATTERN));
        for (String file : fileSetManager.getIncludedFiles(fileSet)) {
            files.add(new File(stubsOutputDirectory, file));
        }

        return files;
    }

    /**
     * Gets the set of files of the test stubs.
     *
     * @return The set of files of the test stubs
     */
    protected Set<File> getTestStubs() {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        FileSet fileSet = new FileSet();
        fileSet.setDirectory(testStubsOutputDirectory.getAbsolutePath());
        fileSet.setIncludes(Arrays.asList(DEFAULT_STUB_PATTERN));
        for (String file : fileSetManager.getIncludedFiles(fileSet)) {
            files.add(new File(testStubsOutputDirectory, file));
        }

        return files;
    }

    /**
     * Performs the stub generation on the specified source files.
     *
     * @param stubSources the sources to perform stub generation on
     * @param classpath The classpath to use for compilation
     * @param mavenBuildOutputDirectory Maven's build output directory
     * @param outputDirectory the directory to write the stub files to
     * @throws ClassNotFoundException When a class needed for stub generation cannot be found
     * @throws InstantiationException When a class needed for stub generation cannot be instantiated
     * @throws IllegalAccessException When a method needed for stub generation cannot be accessed
     * @throws InvocationTargetException When a reflection invocation needed for stub generation cannot be completed
     * @throws java.net.MalformedURLException When a classpath element provides a malformed URL
     */
    protected synchronized void doStubGeneration(final Set<File> stubSources, final List classpath, final String mavenBuildOutputDirectory, final File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        // get classes we need with reflection
        Class<?> compilerConfigurationClass = Class.forName("org.codehaus.groovy.control.CompilerConfiguration");
        Class<?> javaStubCompilationUnitClass = Class.forName("org.codehaus.groovy.tools.javac.JavaStubCompilationUnit");
        Class<?> groovyClassLoaderClass = Class.forName("groovy.lang.GroovyClassLoader");
        Class<?> phasesClass = Class.forName("org.codehaus.groovy.control.Phases");

        // set up compile options
        Object compilerConfiguration = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(compilerConfigurationClass));
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, debug);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, verbose);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, warningLevel);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, tolerance);
        if (Version.parseFromString(getGroovyVersion()).compareTo(new Version(1, 5, 0)) >= 0) {
            // if plugin only runs on 1.5, then can assume 1.5
//            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, targetBytecode);
            //
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, "1.5");
        }
        if (sourceEncoding != null) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, sourceEncoding);
        }
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetDirectory", String.class), compilerConfiguration, outputDirectory.getAbsolutePath());

        // append project classpath to groovyClassLoader
        ClassLoader parent = ClassLoader.getSystemClassLoader();
        Object groovyClassLoader = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass), parent, compilerConfiguration);
        Object javaStubCompilationUnit = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(javaStubCompilationUnitClass, compilerConfigurationClass, groovyClassLoaderClass, File.class), compilerConfiguration, groovyClassLoader, outputDirectory);
        getLog().debug("Classpath: ");
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyClassLoaderClass, "addClasspath", String.class), groovyClassLoader, mavenBuildOutputDirectory);
        getLog().debug("    " + mavenBuildOutputDirectory);
        if (classpath != null) {
            for (Object classpathElement : classpath) {
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyClassLoaderClass, "addURL", URL.class), groovyClassLoader, new File((String) classpathElement).toURI().toURL());
                getLog().debug("    " + classpathElement);
            }
        }

        // add Groovy sources
        getLog().debug("Adding Groovy to generate stubs for:");
        for (File source : stubSources) {
            getLog().debug("    " + source);
            if (Version.parseFromString(getGroovyVersion()).compareTo(new Version(1, 8, 3)) >= 0) {
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
        Object convPhase = ReflectionUtils.getStaticField(ReflectionUtils.findField(phasesClass, "CONVERSION", int.class));
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(javaStubCompilationUnitClass, "compile", int.class), javaStubCompilationUnit, convPhase);

        // log generated stubs
        getLog().debug("Generated " + stubSources.size() + "stubs.");
    }

    /**
     * Determines whether this mojo can be run with the version of Groovy supplied.
     * Must be >= 1.7.0 because not all the classes needed were available and
     * functioning correctly in previous versions.
     *
     * @return <code>true</code> only if the version of Groovy supports this mojo.
     */
    protected boolean groovyVersionSupportsAction() {
        return Version.parseFromString(getGroovyVersion()).compareTo(MIN_GROOVY_VERSION) >= 0;
    }

    /**
     * This is a fix for fix for http://jira.codehaus.org/browse/MGROOVY-187
     * It modifies the dates of the created stubs to 1970, ensuring that the Java
     * compiler will not come along and overwrite perfectly good compiled Groovy
     * just because it has a newer source stub.  Basically, this prevents the
     * stubs from causing a side effect with the Java compiler, but still allows
     * the stubs to work with JavaDoc.  Ideally, the code for this should be
     * added to the code that creates the stubs.
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
