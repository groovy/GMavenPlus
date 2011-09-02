package gmavenplus.mojo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import gmavenplus.util.ReflectionUtils;
import org.codehaus.plexus.util.DirectoryScanner;


/**
 * @author Keegan Witt
 * @version $Rev$ $Date$
 */
public abstract class AbstractGenerateStubsMojo extends AbstractGroovyMojo {

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
     * @parameter expression="${project.build.directory}/generated-sources/groovy-stubs/main"
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
     * @parameter expression="${project.build.directory}/generated-sources/groovy-stubs/test"
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

    protected Set<File> getSources() {
        Set<File> sources = new HashSet<File>();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**/*.groovy"};
        ds.setIncludes(includes);
        ds.setBasedir(sourceDirectory);
        ds.setCaseSensitive(true);
        ds.scan();

        String[] files = ds.getIncludedFiles();
        for (int i = 0; i < files.length; i++) {
            sources.add(new File(sourceDirectory, files[i]));
        }

        return sources;
    }

    protected Set<File> getTestSources() {
        Set<File> sources = new HashSet<File>();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**/*.groovy"};
        ds.setIncludes(includes);
        ds.setBasedir(testSourceDirectory);
        ds.setCaseSensitive(true);
        ds.scan();

        String[] files = ds.getIncludedFiles();
        for (int i = 0; i < files.length; i++) {
            sources.add(new File(testSourceDirectory, files[i]));
        }

        return sources;
    }

    protected Set<File> getStubs() {
        Set<File> stubs = new HashSet<File>();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**/*.groovy"};
        ds.setIncludes(includes);
        ds.setBasedir(outputDirectory);
        ds.setCaseSensitive(true);
        ds.scan();

        String[] files = ds.getIncludedFiles();
        for (int i = 0; i < files.length; i++) {
            stubs.add(new File(outputDirectory, files[i]));
        }

        return stubs;
    }

    protected Set<File> getTestStubs() {
        Set<File> stubs = new HashSet<File>();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**/*.groovy"};
        ds.setIncludes(includes);
        ds.setBasedir(testOutputDirectory);
        ds.setCaseSensitive(true);
        ds.scan();

        String[] files = ds.getIncludedFiles();
        for (int i = 0; i < files.length; i++) {
            stubs.add(new File(testOutputDirectory, files[i]));
        }

        return stubs;
    }

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
        getLog().debug("Compiling " + sources.size() + " sources");
        for (Iterator<File> iter = sources.iterator(); iter.hasNext();) {
            URL url = null;
            File next = iter.next();
            try {
                url = next.toURI().toURL();
            } catch (MalformedURLException e) {
                getLog().error("Unable to add source file " + next.getAbsolutePath() + " for stub generation", e);
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
        for (Iterator i = stubs.iterator(); i.hasNext();) {
            File file = (File) i.next();
            file.setLastModified(0L);
        }
    }

}
