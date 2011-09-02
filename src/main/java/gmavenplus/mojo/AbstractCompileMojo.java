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

package gmavenplus.mojo;

import gmavenplus.util.ReflectionUtils;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.codehaus.plexus.util.DirectoryScanner;


/**
 * @author Keegan Witt
 */
public abstract class AbstractCompileMojo extends AbstractGroovyMojo {

    // TODO: use org.apache.maven.shared.model.fileset.FileSet to have includes and excludes instead of directory & filename conventions?

    /**
     * Location of the Groovy source files
     *
     * @parameter expression="${project.basedir}/src/main/groovy"
     * @readonly
     * @required
     */
    protected File sourceDirectory;

    /**
     * Location for the compiled classes
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @readonly
     * @required
     */
    protected File outputDirectory;

    /**
     * Location of the Groovy test source files
     *
     * @parameter expression="${project.basedir}/src/test/groovy"
     * @readonly
     * @required
     */
    protected File testSourceDirectory;

    /**
     * Location for the compiled test classes
     *
     * @parameter expression="${project.build.testOutputDirectory}"
     * @readonly
     * @required
     */
    protected File testOutputDirectory;

    /**
     * Encoding of source files
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     * @required
     */
    protected String sourceEncoding;

    /**
     * Groovy compiler bytecode compatibility ("1.4" or "1.5")
     *
     * @parameter default-value="1.5"
     * @required
     */
    protected String targetBytecode;

    /**
     * Whether Groovy compiler should be set to debug or not
     *
     * @parameter default-value="false"
     * @required
     */
    protected boolean debug;

    /**
     * Whether Groovy compiler should be set to verbose or not
     *
     * @parameter default-value="false"
     * @required
     */
    protected boolean verbose;

    /**
     * Groovy compiler warning level, should be one of below values
     *
     * * NONE 0
     * * LIKELY_ERRORS 1
     * * POSSIBLE_ERRORS 2
     * * PARANOIA 3
     *
     * @parameter default-value="0"
     * @required
     */
    protected int warningLevel;

    /**
     * Groovy compiler error tolerance (the number of non-fatal errors (per unit) that should be tolerated before compilation is aborted)
     *
     * @parameter default-value="0"
     * @required
     */
    protected int tolerance;

    /**
     * @return
     */
    protected Set<File> getSources() {
        Set<File> sources = new HashSet<File>();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**/*.groovy"};
        ds.setIncludes(includes);
        ds.setBasedir(sourceDirectory);
        ds.setCaseSensitive(true);
        ds.scan();

        String[] files = ds.getIncludedFiles();
        for (String file : files) {
            sources.add(new File(sourceDirectory, file));
        }

        return sources;
    }

    /**
     * @return
     */
    protected Set<File> getTestSources() {
        Set<File> sources = new HashSet<File>();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**/*.groovy"};
        ds.setIncludes(includes);
        ds.setBasedir(testSourceDirectory);
        ds.setCaseSensitive(true);
        ds.scan();

        String[] files = ds.getIncludedFiles();
        for (String file : files) {
            sources.add(new File(testSourceDirectory, file));
        }

        return sources;
    }

    /**
     * @param sources
     * @param outputDirectory
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected void doCompile(Set<File> sources, File outputDirectory) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        // get classes we need with reflection
        Class compilerConfigurationClass = Class.forName("org.codehaus.groovy.control.CompilerConfiguration");
        Class compilationUnitClass = Class.forName("org.codehaus.groovy.control.CompilationUnit");
        Class groovyClassLoaderClass = Class.forName("groovy.lang.GroovyClassLoader");

        // set up compile options
        Object compilerConfiguration = ReflectionUtils.findConstructor(
                compilerConfigurationClass).newInstance();
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, debug);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, verbose);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, warningLevel);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, tolerance);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, targetBytecode);
        if (sourceEncoding != null) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, sourceEncoding);
        }
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetDirectory", String.class), compilerConfiguration, outputDirectory.getAbsolutePath());
        ClassLoader parent = ClassLoader.getSystemClassLoader();
        Object groovyClassLoader = ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass).newInstance(parent, compilerConfiguration);
        Object transformLoader = ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class).newInstance(getClass().getClassLoader());
        Object compilationUnit = ReflectionUtils.findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass, groovyClassLoaderClass).newInstance(compilerConfiguration, null, groovyClassLoader, transformLoader);
        getLog().debug("Compiling " + sources.size() + " sources");
        for (File source : sources) {
            URL url = null;
            try {
                url = source.toURI().toURL();
            } catch (MalformedURLException e) {
                getLog().error("Unable to add source file " + source.getAbsolutePath() + " for compiling", e);
            }
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "addSource", URL.class), compilationUnit, url);
        }

        // compile the classes
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "compile"), compilationUnit);

        // log compiled classes
        List classes = (List) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "getClasses"), compilationUnit);
        if (getLog().isDebugEnabled()) {
            getLog().debug("Compiled " + String.valueOf(classes.size()) + " classes: ");
            for (Object aClass : classes) {
                getLog().debug("    " + ((Class) aClass).getName());
            }
        }
    }

}
