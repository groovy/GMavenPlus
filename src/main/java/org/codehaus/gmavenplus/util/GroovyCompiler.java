package org.codehaus.gmavenplus.util;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.gmavenplus.model.GroovyCompileConfiguration;
import org.codehaus.gmavenplus.model.GroovyDocConfiguration;
import org.codehaus.gmavenplus.model.GroovyStubConfiguration;
import org.codehaus.gmavenplus.model.Link;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.gmavenplus.groovyworkarounds.GroovyDocTemplateInfo;
import java.util.ArrayList;
import java.util.Properties;
import org.codehaus.gmavenplus.model.internal.Version;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.codehaus.gmavenplus.util.ReflectionUtils.*;

/**
 * Handles the actual compilation logic, separated from the Mojo to allow forked execution.
 *
 * @author Keegan Witt
 */
public class GroovyCompiler {

    /**
     * Groovy 5.0.0-beta-1 version.
     */
    protected static final Version GROOVY_5_0_0_BETA_1 = new Version(5, 0, 0, "beta-1");

    /**
     * Groovy 5.0.0-alpha-13 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA13 = new Version(5, 0, 0, "alpha-13");

    /**
     * Groovy 5.0.0-alpha-11 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA11 = new Version(5, 0, 0, "alpha-11");

    /**
     * Groovy 5.0.0-alpha-8 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA8 = new Version(5, 0, 0, "alpha-8");

    /**
     * Groovy 5.0.0-alpha-3 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA3 = new Version(5, 0, 0, "alpha-3");

    /**
     * Groovy 5.0.0-alpha-1 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA1 = new Version(5, 0, 0, "alpha-1");

    /**
     * Groovy 4.0.27 version.
     */
    protected static final Version GROOVY_4_0_27 = new Version(4, 0, 27);

    /**
     * Groovy 4.0.24 version.
     */
    protected static final Version GROOVY_4_0_24 = new Version(4, 0, 24);

    /**
     * Groovy 4.0.21 version.
     */
    protected static final Version GROOVY_4_0_21 = new Version(4, 0, 21);

    /**
     * Groovy 4.0.11 version.
     */
    protected static final Version GROOVY_4_0_16 = new Version(4, 0, 16);

    /**
     * Groovy 4.0.11 version.
     */
    protected static final Version GROOVY_4_0_11 = new Version(4, 0, 11);

    /**
     * Groovy 4.0.6 version.
     */
    protected static final Version GROOVY_4_0_6 = new Version(4, 0, 6);

    /**
     * Groovy 4.0.2 version.
     */
    protected static final Version GROOVY_4_0_2 = new Version(4, 0, 2);

    /**
     * Groovy 4.0.0 beta-1 version.
     */
    protected static final Version GROOVY_4_0_0_BETA1 = new Version(4, 0, 0, "beta-1");

    /**
     * Groovy 4.0.0 alpha-3 version.
     */
    protected static final Version GROOVY_4_0_0_ALPHA3 = new Version(4, 0, 0, "alpha-3");

    /**
     * Groovy 4.0.0 alpha-1 version.
     */
    protected static final Version GROOVY_4_0_0_ALPHA1 = new Version(4, 0, 0, "alpha-1");

    /**
     * Groovy 3.0.8 version.
     */
    protected static final Version GROOVY_3_0_8 = new Version(3, 0, 8);

    /**
     * Groovy 3.0.6 version.
     */
    protected static final Version GROOVY_3_0_6 = new Version(3, 0, 6);

    /**
     * Groovy 3.0.5 version.
     */
    protected static final Version GROOVY_3_0_5 = new Version(3, 0, 5);

    /**
     * Groovy 3.0.3 version.
     */
    protected static final Version GROOVY_3_0_3 = new Version(3, 0, 3);

    /**
     * Groovy 3.0.0 beta-2 version.
     */
    protected static final Version GROOVY_3_0_0_BETA2 = new Version(3, 0, 0, "beta-2");

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
     * Groovy 1.8.2 version.
     */
    protected static final Version GROOVY_1_8_2 = new Version(1, 8, 2);

    /**
     * Groovy 1.8.3 version.
     */
    protected static final Version GROOVY_1_8_3 = new Version(1, 8, 3);

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
     * Groovy 1.9.0 beta-1 version.
     */
    protected static final Version GROOVY_1_9_0_BETA1 = new Version(1, 9, 0, "beta-1");

    /**
     * Groovy 1.9.0 beta-3 version.
     */
    protected static final Version GROOVY_1_9_0_BETA3 = new Version(1, 9, 0, "beta-3");

    /**
     * Groovy 1.6.0 version.
     */
    protected static final Version GROOVY_1_6_0 = new Version(1, 6, 0);

    /**
     * Groovy 1.6.0 RC-2 version.
     */
    protected static final Version GROOVY_1_6_0_RC2 = new Version(1, 6, 0, "RC-2");

    /**
     * Groovy 1.5.2 version.
     */
    protected static final Version GROOVY_1_5_2 = new Version(1, 5, 2);

    /**
     * Java 1.7 version.
     */
    protected static final Version JAVA_1_7 = new Version(1, 7);

    /**
     * Java 1.8 version.
     */
    protected static final Version JAVA_1_8 = new Version(1, 8);

    /**
     * Java 1.8 version.
     */
    protected static final Version JAVA_12 = new Version(12);

    private final ClassWrangler classWrangler;
    private final Log log;

    public GroovyCompiler(ClassWrangler classWrangler, Log log) {
        this.classWrangler = classWrangler;
        this.log = log;
    }

    public void compile(GroovyCompileConfiguration configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (configuration.getSources() == null || configuration.getSources().isEmpty()) {
            log.info("No sources specified for compilation. Skipping.");
            return;
        }

        if (!configuration.isSkipBytecodeCheck()) {
            verifyGroovyVersionSupportsTargetBytecode(configuration.getTargetBytecode());
        }

        // get classes we need with reflection
        Class<?> compilerConfigurationClass = classWrangler.getClass("org.codehaus.groovy.control.CompilerConfiguration");
        Class<?> compilationUnitClass = classWrangler.getClass("org.codehaus.groovy.control.CompilationUnit");
        Class<?> groovyClassLoaderClass = classWrangler.getClass("groovy.lang.GroovyClassLoader");

        // setup compile options
        Object compilerConfiguration = setupCompilerConfiguration(configuration, compilerConfigurationClass);
        Object groovyClassLoader = invokeConstructor(findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass), classWrangler.getClassLoader(), compilerConfiguration);
        Object transformLoader = invokeConstructor(findConstructor(groovyClassLoaderClass, ClassLoader.class), classWrangler.getClassLoader());

        // add Groovy sources
        Object compilationUnit = setupCompilationUnit(configuration.getSources(), compilerConfigurationClass, compilationUnitClass, groovyClassLoaderClass, compilerConfiguration, groovyClassLoader, transformLoader);

        // compile the classes
        invokeMethod(findMethod(compilationUnitClass, "compile"), compilationUnit);

        // log compiled classes
        List<?> classes = (List<?>) invokeMethod(findMethod(compilationUnitClass, "getClasses"), compilationUnit);
        log.info("Compiled " + classes.size() + " file" + (classes.size() != 1 ? "s" : "") + ".");
    }

    public void generateGroovyDoc(GroovyDocConfiguration configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (configuration.getSourceDirectories() == null || configuration.getSourceDirectories().length == 0) {
            log.info("No source directories specified for GroovyDoc generation. Skipping.");
            return;
        }

        // Note: minGroovyVersion check is usually done by caller (Mojo) but strict check is good.
        // GroovyDoc supports 1.5.2+ generally (LinkArgument) or 1.6+ for properties?
        // AbstractGroovyDocMojo checks groovyVersionSupportsAction() which uses mojo defined min version.
        // We can skip strict min version check here and rely on feature detection or caller.

        // get classes we need with reflection
        Class<?> groovyDocToolClass = classWrangler.getClass(configuration.getGroovyDocToolClass() == null ? "org.codehaus.groovy.tools.groovydoc.GroovyDocTool" : configuration.getGroovyDocToolClass());
        Class<?> outputToolClass = classWrangler.getClass(configuration.getOutputToolClass() == null ? "org.codehaus.groovy.tools.groovydoc.OutputTool" : configuration.getOutputToolClass());
        Class<?> fileOutputToolClass = classWrangler.getClass(configuration.getFileOutputToolClass() == null ? "org.codehaus.groovy.tools.groovydoc.FileOutputTool" : configuration.getFileOutputToolClass());
        Class<?> resourceManagerClass = classWrangler.getClass(configuration.getResourceManagerClass() == null ? "org.codehaus.groovy.tools.groovydoc.ResourceManager" : configuration.getResourceManagerClass());
        Class<?> classpathResourceManagerClass = classWrangler.getClass(configuration.getClasspathResourceManagerClass() == null ? "org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager" : configuration.getClasspathResourceManagerClass());

        // set up GroovyDoc options
        if (configuration.isAttachGroovyDocAnnotation()) {
            if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_3_0_0_ALPHA4)) {
                System.setProperty("runtimeGroovydoc", "true");
            } else {
                log.warn("Requested to enable attaching GroovyDoc annotation, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be " + GROOVY_3_0_0_ALPHA4 + " or newer). Ignoring enableGroovyDocAnnotation parameter.");
            }
        }

        Object fileOutputTool = invokeConstructor(findConstructor(fileOutputToolClass));
        Object classpathResourceManager = invokeConstructor(findConstructor(classpathResourceManagerClass));
        FileSetManager fileSetManager = new FileSetManager();
        List<String> sourceDirectoriesStrings = new ArrayList<>();
        for (FileSet sourceDirectory : configuration.getSourceDirectories()) {
            sourceDirectoriesStrings.add(sourceDirectory.getDirectory());
        }
        GroovyDocTemplateInfo groovyDocTemplateInfo = new GroovyDocTemplateInfo(classWrangler.getGroovyVersion());

        List<?> groovyDocLinks = setupLinks(configuration);

        if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_1_6_0_RC2) && configuration.getDocProperties() != null && !configuration.getDocProperties().isEmpty()) {
            log.warn("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support GroovyDoc documentation properties (docTitle, footer, header, displayAuthor, overviewFile, and scope). You need Groovy 1.6-RC-2 or newer to support this. Ignoring properties.");
        }

        // prevent Java stubs from overwriting GroovyDoc
        List<String> groovyDocSources = setupGroovyDocSources(configuration.getSourceDirectories(), fileSetManager);

        // instantiate GroovyDocTool
        Object groovyDocTool = createGroovyDocTool(groovyDocToolClass, resourceManagerClass, configuration.getDocProperties(), classpathResourceManager, sourceDirectoriesStrings, groovyDocTemplateInfo, groovyDocLinks, configuration);

        // generate GroovyDoc
        performGroovyDocGeneration(configuration.getOutputDirectory(), groovyDocToolClass, outputToolClass, fileOutputTool, groovyDocSources, groovyDocTool);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected List<?> setupLinks(GroovyDocConfiguration configuration) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        List linksList = new ArrayList();
        if (configuration.getLinks() != null && !configuration.getLinks().isEmpty()) {
            Class<?> linkArgumentClass = null;
            if (configuration.getLinkArgumentClass() == null) {
                if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_1_6_0_RC2)) {
                    linkArgumentClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.LinkArgument");
                } else if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_1_5_2)) {
                    linkArgumentClass = classWrangler.getClass("org.codehaus.groovy.ant.Groovydoc$LinkArgument");
                }
            } else {
                linkArgumentClass = classWrangler.getClass(configuration.getLinkArgumentClass());
            }
            if (linkArgumentClass != null) {
                Method setHref = findMethod(linkArgumentClass, "setHref", String.class);
                Method setPackages = findMethod(linkArgumentClass, "setPackages", String.class);
                for (Link link : configuration.getLinks()) {
                    Object linkArgument = invokeConstructor(findConstructor(linkArgumentClass));
                    invokeMethod(setHref, linkArgument, link.getHref());
                    invokeMethod(setPackages, linkArgument, link.getPackages());
                    linksList.add(linkArgument);
                }
            } else {
                log.warn("Requested to use GroovyDoc links, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be 1.5.2 or newer). Ignoring links parameter.");
            }
        }
        return linksList;
    }

    protected Object createGroovyDocTool(final Class<?> groovyDocToolClass, final Class<?> resourceManagerClass, final Properties docProperties, final Object classpathResourceManager, final List<String> sourceDirectories, final GroovyDocTemplateInfo groovyDocTemplateInfo, final List<?> groovyDocLinks, GroovyDocConfiguration configuration) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object groovyDocTool;

        String[] defaultDocTemplates = configuration.getDefaultDocTemplates();
        String[] defaultPackageTemplates = configuration.getDefaultPackageTemplates();
        String[] defaultClassTemplates = configuration.getDefaultClassTemplates();

        if ((ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_4_0_27) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA1)) || ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_5_0_0_BETA_1)) {
             groovyDocTool = invokeConstructor(findConstructor(groovyDocToolClass, resourceManagerClass, String[].class, String[].class, String[].class, String[].class, List.class, String.class, Properties.class),
                    classpathResourceManager,
                    sourceDirectories.toArray(new String[0]),
                    defaultDocTemplates == null ? groovyDocTemplateInfo.defaultDocTemplates() : defaultDocTemplates,
                    defaultPackageTemplates == null ? groovyDocTemplateInfo.defaultPackageTemplates() : defaultPackageTemplates,
                    defaultClassTemplates == null ? groovyDocTemplateInfo.defaultClassTemplates() : defaultClassTemplates,
                    groovyDocLinks,
                    configuration.getLanguageLevel(),
                    docProperties
            );
        } else if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_1_6_0_RC2)) {
            groovyDocTool = invokeConstructor(findConstructor(groovyDocToolClass, resourceManagerClass, String[].class, String[].class, String[].class, String[].class, List.class, Properties.class),
                    classpathResourceManager,
                    sourceDirectories.toArray(new String[0]),
                    defaultDocTemplates == null ? groovyDocTemplateInfo.defaultDocTemplates() : defaultDocTemplates,
                    defaultPackageTemplates == null ? groovyDocTemplateInfo.defaultPackageTemplates() : defaultPackageTemplates,
                    defaultClassTemplates == null ? groovyDocTemplateInfo.defaultClassTemplates() : defaultClassTemplates,
                    groovyDocLinks,
                    docProperties
            );
        } else if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_1_5_2)) {
            groovyDocTool = invokeConstructor(findConstructor(groovyDocToolClass, resourceManagerClass, String.class, String[].class, String[].class, String[].class, List.class),
                    classpathResourceManager,
                    sourceDirectories.get(0),
                    defaultDocTemplates == null ? groovyDocTemplateInfo.defaultDocTemplates() : defaultDocTemplates,
                    defaultPackageTemplates == null ? groovyDocTemplateInfo.defaultPackageTemplates() : defaultPackageTemplates,
                    defaultClassTemplates == null ? groovyDocTemplateInfo.defaultClassTemplates() : defaultClassTemplates,
                    groovyDocLinks
            );
            if (sourceDirectories.size() > 1) {
                log.warn("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support more than one GroovyDoc source directory (must be 1.6-RC-2 or newer). Only using first source directory (" + sourceDirectories.get(0) + ").");
            }
        } else {
            groovyDocTool = invokeConstructor(findConstructor(groovyDocToolClass, resourceManagerClass, String.class, String[].class, String[].class, String[].class),
                    classpathResourceManager,
                    sourceDirectories.get(0),
                    defaultDocTemplates == null ? groovyDocTemplateInfo.defaultDocTemplates() : defaultDocTemplates,
                    defaultPackageTemplates == null ? groovyDocTemplateInfo.defaultPackageTemplates() : defaultPackageTemplates,
                    defaultClassTemplates == null ? groovyDocTemplateInfo.defaultClassTemplates() : defaultClassTemplates
            );
             if (sourceDirectories.size() > 1) {
                log.warn("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support more than one GroovyDoc source directory (must be 1.6-RC-2 or newer). Only using first source directory (" + sourceDirectories.get(0) + ").");
            }
        }
        return groovyDocTool;
    }

    protected List<String> setupGroovyDocSources(final FileSet[] sourceDirectories, final FileSetManager fileSetManager) {
        List<String> javaSources = new ArrayList<>();
        List<String> groovySources = new ArrayList<>();
        List<String> possibleGroovyStubs = new ArrayList<>();
        for (FileSet sourceDirectory : sourceDirectories) {
            String[] sources = fileSetManager.getIncludedFiles(sourceDirectory);
            for (String source : sources) {
                if (source.endsWith(".java") && !javaSources.contains(source)) {
                    javaSources.add(source);
                } else if (!groovySources.contains(source)) {
                    groovySources.add(source);
                    possibleGroovyStubs.add(source.replaceFirst("\\." + FileUtils.getFileExtension(source), ".java"));
                }
            }
        }
        javaSources.removeAll(possibleGroovyStubs);
        List<String> groovyDocSources = new ArrayList<>();
        groovyDocSources.addAll(javaSources);
        groovyDocSources.addAll(groovySources);

        return groovyDocSources;
    }

    protected void performGroovyDocGeneration(final File outputDirectory, final Class<?> groovyDocToolClass, final Class<?> outputToolClass, final Object fileOutputTool, final List<String> groovyDocSources, final Object groovyDocTool) throws InvocationTargetException, IllegalAccessException {
        log.debug("Adding sources to generate GroovyDoc for:");
        if (log.isDebugEnabled()) {
            for (String groovyDocSource : groovyDocSources) {
                log.debug("    " + groovyDocSource);
            }
        }
        if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_1_6_0_RC2)) {
            invokeMethod(findMethod(groovyDocToolClass, "add", List.class), groovyDocTool, groovyDocSources);
        } else {
            Method add = findMethod(groovyDocToolClass, "add", String.class);
            for (String groovyDocSource : groovyDocSources) {
                invokeMethod(add, groovyDocTool, groovyDocSource);
            }
        }
        invokeMethod(findMethod(groovyDocToolClass, "renderToOutput", outputToolClass, String.class), groovyDocTool, fileOutputTool, outputDirectory.getAbsolutePath());
    }

    public void generateStubs(GroovyStubConfiguration configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {

        if (configuration.getStubSources() == null || configuration.getStubSources().isEmpty()) {
            log.info("No sources specified for stub generation. Skipping.");
            return;
        }

        if (!supportsStubGeneration()) {
            log.error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support stub generation. The minimum version of Groovy required is 1.8.2. Skipping stub generation.");
            return;
        }

        if (!configuration.isSkipBytecodeCheck()) {
            verifyGroovyVersionSupportsTargetBytecode(configuration.getTargetBytecode());
        }

        // get classes we need with reflection
        Class<?> compilerConfigurationClass = classWrangler.getClass("org.codehaus.groovy.control.CompilerConfiguration");
        Class<?> javaStubCompilationUnitClass = classWrangler.getClass("org.codehaus.groovy.tools.javac.JavaStubCompilationUnit");
        Class<?> groovyClassLoaderClass = classWrangler.getClass("groovy.lang.GroovyClassLoader");

        // setup stub generation options
        Object compilerConfiguration = setupStubCompilerConfiguration(configuration, compilerConfigurationClass);
        Object groovyClassLoader = invokeConstructor(findConstructor(groovyClassLoaderClass, ClassLoader.class, compilerConfigurationClass), classWrangler.getClassLoader(), compilerConfiguration);
        Object javaStubCompilationUnit = invokeConstructor(findConstructor(javaStubCompilationUnitClass, compilerConfigurationClass, groovyClassLoaderClass, File.class), compilerConfiguration, groovyClassLoader, configuration.getOutputDirectory());

        // add Groovy sources
        addGroovySources(configuration.getStubSources(), compilerConfigurationClass, javaStubCompilationUnitClass, compilerConfiguration, javaStubCompilationUnit);

        // generate the stubs
        invokeMethod(findMethod(javaStubCompilationUnitClass, "compile"), javaStubCompilationUnit);
    }

    protected Object setupStubCompilerConfiguration(final GroovyStubConfiguration configuration, final Class<?> compilerConfigurationClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object compilerConfiguration = invokeConstructor(findConstructor(compilerConfigurationClass));
        invokeMethod(findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, configuration.isDebug());
        invokeMethod(findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, configuration.isVerbose());
        invokeMethod(findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, configuration.getWarningLevel());
        invokeMethod(findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, configuration.getTolerance());
        invokeMethod(findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, translateJavacTargetToTargetBytecode(configuration.getTargetBytecode()));
        if (configuration.getSourceEncoding() != null) {
            invokeMethod(findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, configuration.getSourceEncoding());
        }
        Map<String, Object> options = new HashMap<>();
        options.put("stubDir", configuration.getOutputDirectory());
        options.put("keepStubs", Boolean.TRUE);
        invokeMethod(findMethod(compilerConfigurationClass, "setJointCompilationOptions", Map.class), compilerConfiguration, options);

        return compilerConfiguration;
    }

    protected void addGroovySources(final Set<File> stubSources, final Class<?> compilerConfigurationClass, final Class<?> javaStubCompilationUnitClass, final Object compilerConfiguration, final Object javaStubCompilationUnit) throws InvocationTargetException, IllegalAccessException {
        // Since we don't have FileUtils here easily without dependencies, we can just use simple extension check.
        // Wait, FileUtils is in util/FileUtils.java in this project.
        // I should check if I can assume it is available. It is in the same package or similar.
        // It's org.codehaus.gmavenplus.util.FileUtils.
        // I will use it.

        Set<String> scriptExtensions = new java.util.HashSet<>();
        for (File stubSource : stubSources) {
            scriptExtensions.add(FileUtils.getFileExtension(stubSource));
        }
        log.debug("Detected Groovy file extensions: " + scriptExtensions + ".");
        if (supportsSettingExtensions()) {
            invokeMethod(findMethod(compilerConfigurationClass, "setScriptExtensions", Set.class), compilerConfiguration, scriptExtensions);
        }
        log.debug("Adding Groovy to generate stubs for:");
        Method addSource = findMethod(javaStubCompilationUnitClass, "addSource", File.class);
        for (File stubSource : stubSources) {
            log.debug("    " + stubSource);
            if (supportsSettingExtensions()) {
                invokeMethod(addSource, javaStubCompilationUnit, stubSource);
            } else {
                // DotGroovyFile is in groovyworkarounds package.
                // org.codehaus.gmavenplus.groovyworkarounds.DotGroovyFile
                // I need to import it or use reflection? It is compiled code in this project, so I can import it.
                org.codehaus.gmavenplus.groovyworkarounds.DotGroovyFile dotGroovyFile = new org.codehaus.gmavenplus.groovyworkarounds.DotGroovyFile(stubSource).setScriptExtensions(scriptExtensions);
                invokeMethod(addSource, javaStubCompilationUnit, dotGroovyFile);
            }
        }
    }

    protected boolean supportsStubGeneration() {
        return ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_1_8_2);
    }

    protected boolean supportsSettingExtensions() {
        return ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_1_8_3) && (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_1_9_0_BETA1) || ClassWrangler.groovyNewerThan(classWrangler.getGroovyVersion(), GROOVY_1_9_0_BETA3));
    }

    protected Object setupCompilationUnit(final Set<File> sources, final Class<?> compilerConfigurationClass, final Class<?> compilationUnitClass, final Class<?> groovyClassLoaderClass, final Object compilerConfiguration, final Object groovyClassLoader, final Object transformLoader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object compilationUnit;
        if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_1_6_0)) {
            compilationUnit = invokeConstructor(findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass, groovyClassLoaderClass), compilerConfiguration, null, groovyClassLoader, transformLoader);
        } else {
            compilationUnit = invokeConstructor(findConstructor(compilationUnitClass, compilerConfigurationClass, CodeSource.class, groovyClassLoaderClass), compilerConfiguration, null, groovyClassLoader);
        }
        log.debug("Adding Groovy to compile:");
        Method addSourceMethod = findMethod(compilationUnitClass, "addSource", File.class);
        for (File source : sources) {
            log.debug("    " + source);
            invokeMethod(addSourceMethod, compilationUnit, source);
        }

        return compilationUnit;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Object setupCompilerConfiguration(final GroovyCompileConfiguration configuration, final Class<?> compilerConfigurationClass) throws InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Object compilerConfiguration = invokeConstructor(findConstructor(compilerConfigurationClass));
        if (configuration.getConfigScript() != null) {
            if (!configuration.getConfigScript().exists()) {
                log.warn("Configuration script file (" + configuration.getConfigScript().getAbsolutePath() + ") doesn't exist. Ignoring configScript parameter.");
            } else if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_2_1_0_BETA1)) {
                log.warn("Requested to use configScript, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be " + GROOVY_2_1_0_BETA1 + " or newer). Ignoring configScript parameter.");
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
                Object shell = invokeConstructor(findConstructor(groovyShellClass, ClassLoader.class, bindingClass, compilerConfigurationClass), classWrangler.getClassLoader(), binding, shellCompilerConfiguration);
                log.debug("Using configuration script " + configuration.getConfigScript() + " for compilation.");
                invokeMethod(findMethod(groovyShellClass, "evaluate", File.class), shell, configuration.getConfigScript());
            }
        }
        invokeMethod(findMethod(compilerConfigurationClass, "setDebug", boolean.class), compilerConfiguration, configuration.isDebug());
        invokeMethod(findMethod(compilerConfigurationClass, "setVerbose", boolean.class), compilerConfiguration, configuration.isVerbose());
        invokeMethod(findMethod(compilerConfigurationClass, "setWarningLevel", int.class), compilerConfiguration, configuration.getWarningLevel());
        invokeMethod(findMethod(compilerConfigurationClass, "setTolerance", int.class), compilerConfiguration, configuration.getTolerance());
        invokeMethod(findMethod(compilerConfigurationClass, "setTargetBytecode", String.class), compilerConfiguration, translateJavacTargetToTargetBytecode(configuration.getTargetBytecode()));
        if (configuration.isPreviewFeatures()) {
            if (isJavaSupportPreviewFeatures()) {
                if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_2_5_7) || (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_2_6_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_3_0_0_BETA1))) {
                    log.warn("Requested to use preview features, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be " + GROOVY_2_5_7 + "/" + GROOVY_3_0_0_BETA1 + " or newer. No 2.6 version is supported. Ignoring previewFeatures parameter.");
                } else {
                    invokeMethod(findMethod(compilerConfigurationClass, "setPreviewFeatures", boolean.class), compilerConfiguration, configuration.isPreviewFeatures());
                }
            } else {
                log.warn("Requested to use to use preview features, but your Java version (" + getJavaVersionString() + ") doesn't support it. Ignoring previewFeatures parameter.");
            }
        }
        if (configuration.getSourceEncoding() != null) {
            invokeMethod(findMethod(compilerConfigurationClass, "setSourceEncoding", String.class), compilerConfiguration, configuration.getSourceEncoding());
        }
        invokeMethod(findMethod(compilerConfigurationClass, "setTargetDirectory", String.class), compilerConfiguration, configuration.getCompileOutputDirectory().getAbsolutePath());
        if (configuration.isInvokeDynamic() || ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_4_0_0_ALPHA1)) {
            if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_2_0_0_BETA3)) {
                if (classWrangler.isGroovyIndy()) {
                    if (isJavaSupportIndy()) {
                        Map<String, Boolean> optimizationOptions = (Map<String, Boolean>) invokeMethod(findMethod(compilerConfigurationClass, "getOptimizationOptions"), compilerConfiguration);
                        optimizationOptions.put("indy", true);
                        optimizationOptions.put("int", false);
                        log.info("invokedynamic enabled.");
                    } else {
                        log.warn("Requested to use to use invokedynamic, but your Java version (" + getJavaVersionString() + ") doesn't support it. Ignoring invokeDynamic parameter.");
                    }
                } else {
                    log.warn("Requested to use invokedynamic, but your Groovy version doesn't support it (must use have indy classifier). Ignoring invokeDynamic parameter.");
                }
            } else {
                log.warn("Requested to use invokeDynamic, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be " + GROOVY_2_0_0_BETA3 + " or newer). Ignoring invokeDynamic parameter.");
            }
        }
        if (configuration.isParameters()) {
            if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_2_5_0_ALPHA1)) {
                if (isJavaSupportParameters()) {
                    invokeMethod(findMethod(compilerConfigurationClass, "setParameters", boolean.class), compilerConfiguration, configuration.isParameters());
                } else {
                    log.warn("Requested to use to use parameters, but your Java version (" + getJavaVersionString() + ") doesn't support it. Ignoring parameters parameter.");
                }
            } else {
                log.warn("Requested to use parameters, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be " + GROOVY_2_5_0_ALPHA1 + " or newer). Ignoring parameters parameter.");
            }
        }
        if (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_3_0_5)) {
            if ((configuration.getParallelParsing() == null && ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_4_0_0_ALPHA1)) || (configuration.getParallelParsing() != null && configuration.getParallelParsing())) {
                Map<String, Boolean> optimizationOptions = (Map<String, Boolean>) invokeMethod(findMethod(compilerConfigurationClass, "getOptimizationOptions"), compilerConfiguration);
                optimizationOptions.put("parallelParse", true);
                log.info("Parallel parsing enabled.");
            } else {
                log.info("Parallel parsing disabled.");
            }
        }

        return compilerConfiguration;
    }

    protected void verifyGroovyVersionSupportsTargetBytecode(String targetBytecode) {
        if ("1.5".equals(targetBytecode) || "5".equals(targetBytecode) || "1.6".equals(targetBytecode) || "6".equals(targetBytecode) || "1.7".equals(targetBytecode) || "7".equals(targetBytecode) || "1.8".equals(targetBytecode) || "8".equals(targetBytecode) || "1.9".equals(targetBytecode) || "9".equals(targetBytecode) || "10".equals(targetBytecode)) {
            if (ClassWrangler.groovyNewerThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA1)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " isn't accepted by Groovy " + GROOVY_5_0_0_ALPHA1 + " or newer.");
            }
        }

        if ("25".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_4_0_27)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_27 + " or newer.");
            }
            if (ClassWrangler.groovyNewerThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA13)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_5_0_0_ALPHA13 + " or newer.");
            }
        } else if ("24".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_4_0_24)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_24 + " or newer.");
            }
            if (ClassWrangler.groovyNewerThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA11)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_5_0_0_ALPHA11 + " or newer.");
            }
        } else if ("23".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_4_0_21)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_21 + " or newer.");
            }
            if (ClassWrangler.groovyNewerThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA8)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_5_0_0_ALPHA8 + " or newer.");
            }
        } else if ("22".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_4_0_16)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_16 + " or newer.");
            }
            if (ClassWrangler.groovyNewerThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_5_0_0_ALPHA3)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_5_0_0_ALPHA3 + " or newer.");
            }
        } else if ("21".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_4_0_11)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_11 + " or newer.");
            }
        } else if ("20".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_4_0_6)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_6 + " or newer.");
            }
        } else if ("19".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_4_0_2)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_2 + " or newer.");
            }
        } else if ("18".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_4_0_0_BETA1)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_4_0_0_BETA1 + " or newer.");
            }
        } else if ("17".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_3_0_8) || (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_4_0_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_4_0_0_ALPHA3))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_3_0_8 + "/" + GROOVY_4_0_0_ALPHA3 + " or newer.");
            }
        } else if ("16".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_3_0_6)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_3_0_6 + " or newer.");
            }
        } else if ("15".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_3_0_3)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_3_0_3 + " or newer.");
            }
        } else if ("14".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_3_0_0_BETA2)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_3_0_0_BETA2 + " or newer.");
            }
        } else if ("13".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_2_5_7) || (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_2_6_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_3_0_0_BETA1))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_5_7 + "/" + GROOVY_3_0_0_BETA1 + " or newer. No 2.6 version is supported.");
            }
        } else if ("12".equals(targetBytecode) || "11".equals(targetBytecode) || "10".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_2_5_3) || (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_2_6_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_3_0_0_ALPHA4))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_5_3 + "/" + GROOVY_3_0_0_ALPHA4 + " or newer. No 2.6 version is supported.");
            }
        } else if ("9".equals(targetBytecode) || "1.9".equals(targetBytecode)) {
            if (!classWrangler.isGroovyIndy() && (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_2_5_3)
                    || (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_2_6_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_2_6_0_ALPHA4))
                    || (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_3_0_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_3_0_0_ALPHA2)))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_5_3 + "/" + GROOVY_2_6_0_ALPHA4 + "/" + GROOVY_3_0_0_ALPHA2 + " or newer.");
            } else if (classWrangler.isGroovyIndy() && (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_2_5_3) || (ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), GROOVY_2_6_0_ALPHA1) && ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_3_0_0_ALPHA4)))) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_5_3 + "/" + GROOVY_3_0_0_ALPHA4 + " or newer. No 2.6 version is supported.");
            }
        } else if ("8".equals(targetBytecode) || "1.8".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_2_3_3)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_3_3 + " or newer.");
            }
        } else if ("7".equals(targetBytecode) || "1.7".equals(targetBytecode) || "6".equals(targetBytecode) || "1.6".equals(targetBytecode)) {
            if (ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), GROOVY_2_1_3)) {
                throw new IllegalArgumentException("Target bytecode " + targetBytecode + " requires Groovy " + GROOVY_2_1_3 + " or newer.");
            }
        } else if (!"5".equals(targetBytecode) && !"1.5".equals(targetBytecode) && !"4".equals(targetBytecode) && !"1.4".equals(targetBytecode)) {
            throw new IllegalArgumentException("Unrecognized target bytecode: '" + targetBytecode + "'. This check can be skipped with 'skipBytecodeCheck', but this may result in a different target bytecode being used.");
        }
    }

    public static String translateJavacTargetToTargetBytecode(String targetBytecode) {
        Map<String, String> javacTargetToTargetBytecode = new HashMap<>();
        javacTargetToTargetBytecode.put("5", "1.5");
        javacTargetToTargetBytecode.put("6", "1.6");
        javacTargetToTargetBytecode.put("7", "1.7");
        javacTargetToTargetBytecode.put("8", "1.8");
        javacTargetToTargetBytecode.put("1.9", "9");
        return javacTargetToTargetBytecode.getOrDefault(targetBytecode, targetBytecode);
    }

    protected boolean isJavaSupportIndy() {
        return getJavaVersion().compareTo(JAVA_1_7, false) >= 0;
    }

    protected boolean isJavaSupportPreviewFeatures() {
        return getJavaVersion().compareTo(JAVA_12, false) >= 0;
    }

    protected boolean isJavaSupportParameters() {
        return getJavaVersion().compareTo(JAVA_1_8, false) >= 0;
    }

    protected Version getJavaVersion() {
        return Version.parseFromString(getJavaVersionString());
    }

    protected String getJavaVersionString() {
        return System.getProperty("java.version");
    }

}
