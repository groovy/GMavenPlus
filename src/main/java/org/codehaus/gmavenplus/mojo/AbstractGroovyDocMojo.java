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
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.gmavenplus.groovyworkarounds.GroovyDocTemplateInfo;
import org.codehaus.gmavenplus.model.IncludeClasspath;
import org.codehaus.gmavenplus.model.Scopes;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.FileUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.codehaus.gmavenplus.util.ReflectionUtils.*;


/**
 * The base GroovyDoc mojo, which all GroovyDoc mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractGroovyDocMojo extends AbstractGroovySourcesMojo {

    /**
     * Groovy 1.6.0 RC-2 version.
     */
    protected static final Version GROOVY_1_6_0_RC2 = new Version(1, 6, 0, "RC-2");

    /**
     * Groovy 1.6.0 RC-1 version.
     */
    protected static final Version GROOVY_1_6_0_RC1 = new Version(1, 6, 0, "RC-1");

    /**
     * Groovy 1.5.8 version.
     */
    protected static final Version GROOVY_1_5_8 = new Version(1, 5, 8);

    /**
     * Groovy 1.5.2 version.
     */
    protected static final Version GROOVY_1_5_2 = new Version(1, 5, 2);

    /**
     * The window title.
     */
    @Parameter(defaultValue = "Groovy Documentation")
    protected String windowTitle;

    /**
     * The page title.
     */
    @Parameter(defaultValue = "Groovy Documentation")
    protected String docTitle;

    /**
     * The page footer.
     */
    @Parameter(defaultValue = "Groovy Documentation")
    protected String footer;

    /**
     * The page header.
     */
    @Parameter(defaultValue = "Groovy Documentation")
    protected String header;

    /**
     * Whether to display the author in the generated GroovyDoc.
     */
    @Parameter(defaultValue = "true")
    protected boolean displayAuthor;

    /**
     * The HTML file to be used for overview documentation.
     */
    @Parameter
    protected File overviewFile;

    /**
     * The stylesheet file (absolute path) to copy to output directory (will overwrite default stylesheet.css).
     */
    @Parameter
    protected File stylesheetFile;

    /**
     * The encoding of stylesheetFile.
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    protected String stylesheetEncoding;

    /**
     * The scope to generate GroovyDoc for. Should be one of:
     * <ul>
     *   <li>"public"</li>
     *   <li>"protected"</li>
     *   <li>"package"</li>
     *   <li>"private"</li>
     * </ul>
     */
    @Parameter(defaultValue = "private")
    protected String scope;

    /**
     * Links to include in the generated GroovyDoc (key is link href, value is comma-separated packages to use that link).
     *
     * @since 1.0-beta-2
     */
    @Parameter
    protected List<Link> links;

    /**
     * Flag to allow GroovyDoc generation to be skipped.
     *
     * @since 1.6
     */
    @Parameter(property = "skipGroovydoc", defaultValue = "false")
    protected boolean skipGroovyDoc;

    /**
     * What classpath to include. One of
     * <ul>
     *   <li>PROJECT_ONLY</li>
     *   <li>PROJECT_AND_PLUGIN</li>
     *   <li>PLUGIN_ONLY</li>
     * </ul>
     * Uses the same scope as the required dependency resolution of this mojo. Use only if you know what you're doing.
     *
     * @since 1.8.0
     */
    @Parameter(defaultValue = "PROJECT_ONLY")
    protected IncludeClasspath includeClasspath;

    /**
     * Generates the GroovyDoc for the specified sources.
     *
     * @param sourceDirectories The source directories to generate GroovyDoc for
     * @param classpath         The classpath to use for compilation
     * @param outputDirectory   The directory to save the generated GroovyDoc in
     * @throws ClassNotFoundException    when a class needed for GroovyDoc generation cannot be found
     * @throws InstantiationException    when a class needed for GroovyDoc generation cannot be instantiated
     * @throws IllegalAccessException    when a method needed for GroovyDoc generation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for GroovyDoc generation cannot be completed
     * @throws MalformedURLException     when a classpath element provides a malformed URL
     */
    protected synchronized void doGroovyDocGeneration(final FileSet[] sourceDirectories, final List<?> classpath, final File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        if (skipGroovyDoc) {
            getLog().info("Skipping generation of GroovyDoc because ${skipGroovydoc} was set to true.");
            return;
        }

        if (sourceDirectories == null || sourceDirectories.length == 0) {
            getLog().info("No source directories specified for GroovyDoc generation. Skipping.");
            return;
        }

        setupClassWrangler(classpath, includeClasspath);

        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());
        logPluginClasspath();

        if (!groovyVersionSupportsAction()) {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support GroovyDoc. The minimum version of Groovy required is " + minGroovyVersion + ". Skipping GroovyDoc generation.");
            return;
        }
        if (groovyIs(GROOVY_1_6_0_RC1) || groovyIs(GROOVY_1_5_8)) {
            // Groovy 1.5.8 and 1.6-RC-1 are blacklisted because of their dependency on org.apache.tools.ant.types.Path in GroovyDocTool constructor
            getLog().warn("Groovy " + GROOVY_1_5_8 + " and " + GROOVY_1_6_0_RC1 + " are blacklisted from the supported GroovyDoc versions because of their dependency on Ant. Skipping GroovyDoc generation.");
            return;
        }

        // get classes we need with reflection
        Class<?> groovyDocToolClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.GroovyDocTool");
        Class<?> outputToolClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.OutputTool");
        Class<?> fileOutputToolClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.FileOutputTool");
        Class<?> resourceManagerClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.ResourceManager");
        Class<?> classpathResourceManagerClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager");

        // set up GroovyDoc options
        Properties docProperties = setupProperties();
        Object fileOutputTool = invokeConstructor(findConstructor(fileOutputToolClass));
        Object classpathResourceManager = invokeConstructor(findConstructor(classpathResourceManagerClass));
        FileSetManager fileSetManager = new FileSetManager(getLog());
        List<String> sourceDirectoriesStrings = new ArrayList<>();
        for (FileSet sourceDirectory : sourceDirectories) {
            sourceDirectoriesStrings.add(sourceDirectory.getDirectory());
        }
        GroovyDocTemplateInfo groovyDocTemplateInfo = new GroovyDocTemplateInfo(classWrangler.getGroovyVersion());
        List<?> groovyDocLinks = setupLinks();
        if (groovyOlderThan(GROOVY_1_6_0_RC2)) {
            getLog().warn("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support GroovyDoc documentation properties (docTitle, footer, header, displayAuthor, overviewFile, and scope). You need Groovy 1.6-RC-2 or newer to support this. Ignoring properties.");
        }

        // prevent Java stubs (which lack Javadoc) from overwriting GroovyDoc by removing Java sources
        List<String> groovyDocSources = setupGroovyDocSources(sourceDirectories, fileSetManager);

        // instantiate GroovyDocTool
        Object groovyDocTool = createGroovyDocTool(groovyDocToolClass, resourceManagerClass, docProperties, classpathResourceManager, sourceDirectoriesStrings, groovyDocTemplateInfo, groovyDocLinks);

        // generate GroovyDoc
        generateGroovyDoc(outputDirectory, groovyDocToolClass, outputToolClass, fileOutputTool, groovyDocSources, groovyDocTool);

        // overwrite stylesheet.css with provided stylesheet (if configured)
        if (stylesheetFile != null) {
            copyStylesheet(outputDirectory);
        }
    }

    /**
     * Sets up the documentation properties.
     *
     * @return the documentation properties
     */
    protected Properties setupProperties() {
        Properties properties = new Properties();
        properties.setProperty("windowTitle", windowTitle);
        properties.setProperty("docTitle", docTitle);
        properties.setProperty("footer", footer);
        properties.setProperty("header", header);
        properties.setProperty("author", Boolean.toString(displayAuthor));
        properties.setProperty("overviewFile", overviewFile != null ? overviewFile.getAbsolutePath() : "");
        try {
            Scopes scopeVal = Scopes.valueOf(scope.toUpperCase());
            if (scopeVal.equals(Scopes.PUBLIC)) {
                properties.setProperty("publicScope", "true");
            } else if (scopeVal.equals(Scopes.PROTECTED)) {
                properties.setProperty("protectedScope", "true");
            } else if (scopeVal.equals(Scopes.PACKAGE)) {
                properties.setProperty("packageScope", "true");
            } else if (scopeVal.equals(Scopes.PRIVATE)) {
                properties.setProperty("privateScope", "true");
            }
        } catch (IllegalArgumentException e) {
            getLog().warn("Scope (" + scope + ") was not recognized. Skipping argument.");
        }

        return properties;
    }

    /**
     * Sets up the GroovyDoc links.
     *
     * @return the GroovyDoc links
     * @throws ClassNotFoundException    when a class needed for setting up GroovyDoc links cannot be found
     * @throws InstantiationException    when a class needed for setting up GroovyDoc links cannot be instantiated
     * @throws IllegalAccessException    when a method needed for setting up GroovyDoc links cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up GroovyDoc links cannot be completed
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected List<?> setupLinks() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        List linksList = new ArrayList();
        if (links != null && links.size() > 0) {
            Class<?> linkArgumentClass = null;
            if (groovyAtLeast(GROOVY_1_6_0_RC2)) {
                linkArgumentClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.LinkArgument");
            } else if (groovyAtLeast(GROOVY_1_5_2)) {
                linkArgumentClass = classWrangler.getClass("org.codehaus.groovy.ant.Groovydoc$LinkArgument");
            }
            if (linkArgumentClass != null) {
                Method setHref = findMethod(linkArgumentClass, "setHref", String.class);
                Method setPackages = findMethod(linkArgumentClass, "setPackages", String.class);
                for (Link link : links) {
                    Object linkArgument = invokeConstructor(findConstructor(linkArgumentClass));
                    invokeMethod(setHref, linkArgument, link.getHref());
                    invokeMethod(setPackages, linkArgument, link.getPackages());
                    linksList.add(linkArgument);
                }
            } else {
                getLog().warn("Requested to use GroovyDoc links, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be 1.5.2 or newer). Ignoring links parameter.");
            }
        }

        return linksList;
    }

    /**
     * Instantiates a new GroovyDocTool.
     *
     * @param groovyDocToolClass       the GroovyDocTool class
     * @param resourceManagerClass     the ResourceManager lass
     * @param docProperties            the documentation properties
     * @param classpathResourceManager the ClasspathResourceManager for the GroovyDocTool
     * @param sourceDirectories        the source directories for the GroovyDocTool
     * @param groovyDocTemplateInfo    the GroovyDocTemplateInfo for the GroovyDocTool
     * @param groovyDocLinks           the GroovyDoc links
     * @return the GroovyDocTool to use in GroovyDoc generation
     * @throws InstantiationException    when a class needed for setting up GroovyDoc tool cannot be instantiated
     * @throws IllegalAccessException    when a method needed for setting up GroovyDoc tool cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up GroovyDoc tool cannot be completed
     */
    protected Object createGroovyDocTool(final Class<?> groovyDocToolClass, final Class<?> resourceManagerClass, final Properties docProperties, final Object classpathResourceManager, final List<String> sourceDirectories, final GroovyDocTemplateInfo groovyDocTemplateInfo, final List<?> groovyDocLinks) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object groovyDocTool;
        if (groovyAtLeast(GROOVY_1_6_0_RC2)) {
            groovyDocTool = invokeConstructor(findConstructor(groovyDocToolClass, resourceManagerClass, String[].class, String[].class, String[].class, String[].class, List.class, Properties.class),
                    classpathResourceManager,
                    sourceDirectories.toArray(new String[0]),
                    groovyDocTemplateInfo.defaultDocTemplates(),
                    groovyDocTemplateInfo.defaultPackageTemplates(),
                    groovyDocTemplateInfo.defaultClassTemplates(),
                    groovyDocLinks,
                    docProperties
            );
        } else if (groovyAtLeast(GROOVY_1_5_2)) {
            groovyDocTool = invokeConstructor(findConstructor(groovyDocToolClass, resourceManagerClass, String.class, String[].class, String[].class, String[].class, List.class),
                    classpathResourceManager,
                    sourceDirectories.get(0),
                    groovyDocTemplateInfo.defaultDocTemplates(),
                    groovyDocTemplateInfo.defaultPackageTemplates(),
                    groovyDocTemplateInfo.defaultClassTemplates(),
                    groovyDocLinks
            );
            if (sourceDirectories.size() > 1) {
                getLog().warn("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support more than one GroovyDoc source directory (must be 1.6-RC-2 or newer). Only using first source directory (" + sourceDirectories.get(0) + ").");
            }
        } else {
            groovyDocTool = invokeConstructor(findConstructor(groovyDocToolClass, resourceManagerClass, String.class, String[].class, String[].class, String[].class),
                    classpathResourceManager,
                    sourceDirectories.get(0),
                    groovyDocTemplateInfo.defaultDocTemplates(),
                    groovyDocTemplateInfo.defaultPackageTemplates(),
                    groovyDocTemplateInfo.defaultClassTemplates()
            );
            if (sourceDirectories.size() > 1) {
                getLog().warn("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support more than one GroovyDoc source directory (must be 1.6-RC-2 or newer). Only using first source directory (" + sourceDirectories.get(0) + ").");
            }
        }

        return groovyDocTool;
    }

    /**
     * Gets the Groovy sources without the Java sources (since the Java sources don't have Javadoc).
     *
     * @param sourceDirectories the source directories to get the Groovy sources from
     * @param fileSetManager    the FileSetmanager to use to get the included files
     * @return the groovy sources
     */
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

    /**
     * Performs the GroovyDoc generation.
     *
     * @param outputDirectory    the directory to output the GroovyDoc to
     * @param groovyDocToolClass the GroovyDocTool class
     * @param outputToolClass    the OutputTool class
     * @param fileOutputTool     the FileOutputTool to use for GroovyDoc generation
     * @param groovyDocSources   the sources to
     * @param groovyDocTool      the GroovyDocTool to use for GroovyDoc generation
     * @throws IllegalAccessException    when a method needed for GroovyDoc generation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for GroovyDoc generation cannot be completed
     */
    protected void generateGroovyDoc(final File outputDirectory, final Class<?> groovyDocToolClass, final Class<?> outputToolClass, final Object fileOutputTool, final List<String> groovyDocSources, final Object groovyDocTool) throws InvocationTargetException, IllegalAccessException {
        getLog().debug("Adding sources to generate GroovyDoc for:");
        if (getLog().isDebugEnabled()) {
            for (String groovyDocSource : groovyDocSources) {
                getLog().debug("    " + groovyDocSource);
            }
        }
        if (groovyAtLeast(GROOVY_1_6_0_RC2)) {
            invokeMethod(findMethod(groovyDocToolClass, "add", List.class), groovyDocTool, groovyDocSources);
        } else {
            Method add = findMethod(groovyDocToolClass, "add", String.class);
            for (String groovyDocSource : groovyDocSources) {
                invokeMethod(add, groovyDocTool, groovyDocSource);
            }
        }
        invokeMethod(findMethod(groovyDocToolClass, "renderToOutput", outputToolClass, String.class), groovyDocTool, fileOutputTool, outputDirectory.getAbsolutePath());
    }

    /**
     * Copies the stylesheet to the specified output directory.
     *
     * @param outputDirectory The output directory to copy the stylesheet to
     */
    protected void copyStylesheet(final File outputDirectory) {
        getLog().info("Using stylesheet from " + stylesheetFile.getAbsolutePath() + ".");
        try {
            BufferedReader bufferedReader = null;
            BufferedWriter bufferedWriter = null;
            try {
                if (stylesheetEncoding != null) {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(stylesheetFile), stylesheetEncoding));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(stylesheetFile)));
                }
                StringBuilder css = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    css.append(line).append("\n");
                }
                File outfile = new File(outputDirectory, "stylesheet.css");
                if (stylesheetEncoding != null) {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), stylesheetEncoding));
                } else {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile)));
                }
                bufferedWriter.write(css.toString());
            } finally {
                FileUtils.closeQuietly(bufferedReader);
                FileUtils.closeQuietly(bufferedWriter);
            }
        } catch (IOException e) {
            getLog().warn("Unable to copy specified stylesheet (" + stylesheetFile.getAbsolutePath() + ").");
        }
    }

}
