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
import java.util.*;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;


/**
 * @author Keegan Witt
 */
public abstract class AbstractCompileMojo extends AbstractGroovyMojo {

    /**
     * Groovy source files (relative paths).
     * Default: "${project.basedir}/src/main/groovy/**&#47;*.groovy"
     *
     * @parameter
     */
    protected FileSet[] sources;

    /**
     * Location for the compiled classes
     *
     * @parameter default-value="${project.build.outputDirectory}"
     */
    protected File outputDirectory;

    /**
     * Groovy test source files (relative paths).
     * Default: "${project.basedir}/src/test/groovy/**&#47;*.groovy"
     *
     * @parameter
     */
    protected FileSet[] testSources;

    /**
     * Location for the compiled test classes
     *
     * @parameter default-value="${project.build.testOutputDirectory}"
     */
    protected File testOutputDirectory;

    /**
     * Encoding of source files
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     */
    protected String sourceEncoding;

    /**
     * Groovy compiler bytecode compatibility ("1.4" or "1.5")
     *
     * @parameter default-value="1.5"
     */
    protected String targetBytecode;

    /**
     * Whether Groovy compiler should be set to debug or not
     *
     * @parameter default-value="false"
     */
    protected boolean debug;

    /**
     * Whether Groovy compiler should be set to verbose or not
     *
     * @parameter default-value="false"
     */
    protected boolean verbose;

    /**
     * Groovy compiler warning level, should be one of
     * "0" (None)
     * "1" (Likely Errors)
     * "2" (Possible Errors)
     * "3" (Paranoia)
     *
     * @parameter default-value="0"
     */
    protected int warningLevel;

    /**
     * Groovy compiler error tolerance (the number of non-fatal errors (per unit) that should be tolerated before compilation is aborted)
     *
     * @parameter default-value="0"
     */
    protected int tolerance;

    /**
     * Gets the set of files for the main sources
     *
     * @return
     */
    protected Set<File> getSources() {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        if (sources != null) {
            for (FileSet fileSet : sources) {
                for (String include : Arrays.asList(fileSetManager.getIncludedFiles(fileSet))) {
                    files.add(new File(project.getBasedir().getAbsolutePath() + File.separator + fileSet.getDirectory(), include));
                }
            }
        } else {
            FileSet fileSet = new FileSet();
            String directory = project.getBasedir().getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "groovy";
            fileSet.setDirectory(directory);
            fileSet.setIncludes(Arrays.asList(DEFAULT_SOURCE_PATTERN));
            for (String file : fileSetManager.getIncludedFiles(fileSet)) {
                files.add(new File(directory, file));
            }
        }

        return files;
    }

    /**
     * Gets the set of files for the test sources
     *
     * @return
     */
    protected Set<File> getTestSources() {
        Set<File> files = new HashSet<File>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        if (testSources != null) {
            for (FileSet fileSet : testSources) {
                for (String include : Arrays.asList(fileSetManager.getIncludedFiles(fileSet))) {
                    files.add(new File(project.getBasedir().getAbsolutePath() + File.separator + fileSet.getDirectory(), include));
                }
            }
        } else {
            FileSet fileSet = new FileSet();
            String directory = project.getBasedir().getAbsolutePath() + File.separator + "src" + File.separator + "test" + File.separator + "groovy";
            fileSet.setDirectory(directory);
            fileSet.setIncludes(Arrays.asList(DEFAULT_SOURCE_PATTERN));
            for (String file : fileSetManager.getIncludedFiles(fileSet)) {
                files.add(new File(directory, file));
            }
        }

        return files;
    }

    /**
     * @param sources
     * @param classpath
     * @param outputDirectory
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.net.MalformedURLException
     */
    protected void doCompile(Set<File> sources, List classpath, File outputDirectory)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, MalformedURLException {
        // get classes we need with reflection
        Class compilerConfigurationClass = Class.forName("org.codehaus.groovy.control.CompilerConfiguration");
        Class compilationUnitClass = Class.forName("org.codehaus.groovy.control.CompilationUnit");
        Class groovyClassLoaderClass = Class.forName("groovy.lang.GroovyClassLoader");

        // set up compile options
        Object compilerConfiguration = ReflectionUtils.findConstructor(compilerConfigurationClass).newInstance();
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
        // append project classpath to groovyClassLoader
        if (classpath != null) {
            getLog().debug("Classpath: ");
            for (Object classpathElement : classpath) {
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyClassLoaderClass, "addURL", URL.class), groovyClassLoader, new File((String) classpathElement).toURI().toURL());
                getLog().debug("    " + classpathElement);
            }
        }
        Object transformLoader = ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class).newInstance(getClass().getClassLoader());
        Object compilationUnit = ReflectionUtils.findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass, groovyClassLoaderClass).newInstance(compilerConfiguration, null, groovyClassLoader, transformLoader);
        getLog().debug("Compiling " + sources.size() + " sources.");
        for (File source : sources) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "addSource", File.class), compilationUnit, source);
        }

        // compile the classes
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "compile"), compilationUnit);

        // log compiled classes
        List classes = (List) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilationUnitClass, "getClasses"), compilationUnit);
        getLog().debug("Compiled " + String.valueOf(classes.size()) + " classes.");
    }

    /**
     * @return
     * @throws DependencyResolutionRequiredException
     */
    protected abstract List getProjectClasspathElements() throws DependencyResolutionRequiredException;

}
