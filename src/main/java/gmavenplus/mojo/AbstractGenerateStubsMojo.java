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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;


/**
 * @author Keegan Witt
 */
public abstract class AbstractGenerateStubsMojo extends AbstractGroovyMojo {

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
     * @parameter default-value="${project.build.directory}/generated-sources/groovy-stubs/main"
     */
    protected File stubsOutputDirectory;

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
     * @parameter default-value="${project.build.directory}/generated-sources/groovy-stubs/test"
     */
    protected File testStubsOutputDirectory;

    /**
     * Encoding of source files
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     */
    protected String sourceEncoding;

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
     * Gets the set of files for the main stubs
     *
     * @return
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
     * Gets the set of files for the test stubs
     *
     * @return
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
     * @param sources
     * @param outputDirectory
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected void doStubGeneration(Set<File> sources, File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // get classes we need with reflection
        Class compilerConfigurationClass = Class.forName("org.codehaus.groovy.control.CompilerConfiguration");
        Class javaStubCompilationUnitClass = Class.forName("org.codehaus.groovy.tools.javac.JavaStubCompilationUnit");
        Class groovyClassLoaderClass = Class.forName("groovy.lang.GroovyClassLoader");

        // set up compile options
        Object compilerConfiguration = ReflectionUtils.findConstructor(compilerConfigurationClass).newInstance();
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, debug);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, verbose);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, warningLevel);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, tolerance);
        if (sourceEncoding != null) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, sourceEncoding);
        }
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(compilerConfigurationClass, "setTargetDirectory", String.class), compilerConfiguration, outputDirectory.getAbsolutePath());
        ClassLoader parent = ClassLoader.getSystemClassLoader();
        Object groovyClassLoader = ReflectionUtils.findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass).newInstance(parent, compilerConfiguration);
        Object javaStubCompilationUnit = ReflectionUtils.findConstructor(javaStubCompilationUnitClass, compilerConfigurationClass, groovyClassLoaderClass, File.class).newInstance(compilerConfiguration, groovyClassLoader, outputDirectory);
        getLog().debug("Generating stubs for " + sources.size() + " sources.");
        for (File source : sources) {
            URL url = null;
            try {
                url = source.toURI().toURL();
            } catch (MalformedURLException e) {
                getLog().error("Unable to add source file " + source.getAbsolutePath() + " for stub generation.", e);
            }
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(javaStubCompilationUnitClass, "addSource", URL.class), javaStubCompilationUnit, url);
        }

        // compile the classes
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(javaStubCompilationUnitClass, "compile"), javaStubCompilationUnit);

        // log compiled classes
        Integer stubCount = (Integer) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(javaStubCompilationUnitClass, "getStubCount"), javaStubCompilationUnit);
        if (getLog().isDebugEnabled()) {
            getLog().debug("Generated " + stubCount + " stubs: ");
        }
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
    protected void resetStubModifiedDates(Set<File> stubs ) {
        for (File file : stubs) {
            file.setLastModified(0L);
        }
    }

}
