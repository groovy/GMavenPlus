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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.gmavenplus.groovyworkarounds.GroovyDocTemplateInfo;
import org.codehaus.gmavenplus.model.Scopes;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.codehaus.gmavenplus.util.FileUtils;
import org.codehaus.gmavenplus.util.ReflectionUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * The base groovydoc mojo, which all groovydoc mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractGroovydocMojo extends AbstractGroovySourcesMojo {

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
     * The location for the generated API docs.
     *
     * @parameter default-value="${project.build.directory}/gapidocs"
     */
    protected File groovydocOutputDirectory;

    /**
     * The location for the generated test API docs.
     *
     * @parameter default-value="${project.build.directory}/testgapidocs"
     */
    protected File testGroovydocOutputDirectory;

    /**
     * The window title.
     *
     * @parameter default-value="Groovy Documentation"
     */
    protected String windowTitle;

    /**
     * The page title.
     *
     * @parameter default-value="Groovy Documentation"
     */
    protected String docTitle;

    /**
     * The page footer.
     *
     * @parameter default-value="Groovy Documentation"
     */
    protected String footer;

    /**
     * The page header.
     *
     * @parameter default-value="Groovy Documentation"
     */
    protected String header;

    /**
     * Whether to display the author in the generated Groovydoc.
     *
     * @parameter default-value="true"
     */
    protected boolean displayAuthor;

    /**
     * The HTML file to be used for overview documentation.
     *
     * @parameter
     */
    protected File overviewFile;

    /**
     * The stylesheet file (absolute path) to copy to output directory (will
     * overwrite default stylesheet.css).
     *
     * @parameter
     */
    protected File stylesheetFile;

    /**
     * The encoding of stylesheetFile.
     *
     * @parameter default-value="${project.build.sourceEncoding}
     */
    protected String stylesheetEncoding;

    /**
     * The scope to generate Groovydoc for. Should be one of:
     * <ul>
     *   <li>"public"</li>
     *   <li>"protected"</li>
     *   <li>"package"</li>
     *   <li>"private"</li>
     * </ul>
     *
     * @parameter default-value="private"
     */
    protected String scope;

    /**
     * Links to include in the generated groovydoc (key is link href, value is
     * comma-separated packages to use that link).
     * @since 1.0-beta-2
     *
     * @parameter
     */
    protected List<Link> links;

    /**
     * Whether to include Java sources in groovydoc generation.
     * @since 1.0-beta-2
     *
     * @parameter default-value="true"
     */
    protected boolean groovydocJavaSources;

    /**
     * Generates the Groovydoc for the specified sources.
     *
     * @param sourceDirectories The source directories to generate groovydoc for
     * @param classpath The classpath to use for compilation
     * @param outputDirectory The directory to save the generated groovydoc in
     * @throws ClassNotFoundException when a class needed for groovydoc generation cannot be found
     * @throws InstantiationException when a class needed for groovydoc generation cannot be instantiated
     * @throws IllegalAccessException when a method needed for groovydoc generation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for groovydoc generation cannot be completed
     * @throws MalformedURLException when a classpath element provides a malformed URL
     */
    @SuppressWarnings("unchecked")
    protected synchronized void doGroovydocGeneration(final FileSet[] sourceDirectories, final List classpath, final File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        classWrangler = new ClassWrangler(classpath, getLog());

        if (sourceDirectories == null || sourceDirectories.length == 0) {
            getLog().info("No source directories specified for Groovydoc generation.  Skipping.");
            return;
        }
        if (groovyVersionSupportsAction()) {
            classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());
            logPluginClasspath();
            if (getLog().isDebugEnabled()) {
                try {
                    getLog().debug("Project compile classpath:\n" + project.getCompileClasspathElements());
                } catch (DependencyResolutionRequiredException e) {
                    getLog().warn("Unable to log project compile classpath", e);
                }
            }
        } else {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support Groovydoc.  The minimum version of Groovy required is " + minGroovyVersion + ".  Skipping Groovydoc generation.");
            return;
        }
        if (classWrangler.getGroovyVersion().compareTo(GROOVY_1_6_0_RC1) == 0 || classWrangler.getGroovyVersion().compareTo(GROOVY_1_5_8) == 0) {
            // Groovy 1.5.8 and 1.6-RC-1 are blacklisted because of their dependency on org.apache.tools.ant.types.Path in GroovyDocTool constructor
            getLog().warn("Groovy " + GROOVY_1_5_8 + " and " + GROOVY_1_6_0_RC1 + " are blacklisted from the supported Groovydoc versions because of their dependency on Ant.  Skipping Groovydoc generation.");
            return;
        }

        // get classes we need with reflection
        Class groovyDocToolClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.GroovyDocTool");
        Class outputToolClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.OutputTool");
        Class fileOutputToolClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.FileOutputTool");
        Class resourceManagerClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.ResourceManager");
        Class classpathResourceManagerClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager");

        // set up Groovydoc options
        Properties docProperties = setupProperties();
        Object fileOutputTool = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(fileOutputToolClass));
        Object classpathResourceManager = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(classpathResourceManagerClass));
        FileSetManager fileSetManager = new FileSetManager(getLog());
        List<String> sourceDirectoriesStrings = new ArrayList<String>();
        for (FileSet sourceDirectory : sourceDirectories) {
            sourceDirectoriesStrings.add(sourceDirectory.getDirectory());
        }
        GroovyDocTemplateInfo groovyDocTemplateInfo = new GroovyDocTemplateInfo(classWrangler.getGroovyVersion());
        List groovydocLinks = setupLinks();
        if (classWrangler.getGroovyVersion().compareTo(GROOVY_1_6_0_RC2) < 0) {
            getLog().warn("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support Groovydoc documentation properties (docTitle, footer, header, displayAuthor, overviewFile, and scope).  You need Groovy 1.6-RC-2 or newer to support this.  Ignoring properties.");
        }

        // prevent Java stubs (which lack Javadoc) from overwriting Groovydoc by removing Java sources
        List<String> groovydocSources = setupGroovyDocSources(sourceDirectories, fileSetManager);

        // instantiate GroovyDocTool
        Object groovyDocTool = createGroovyDocTool(groovyDocToolClass, resourceManagerClass, docProperties, classpathResourceManager, sourceDirectoriesStrings, groovyDocTemplateInfo, groovydocLinks);

        // generate Groovydoc
        generateGroovydoc(outputDirectory, groovyDocToolClass, outputToolClass, fileOutputTool, groovydocSources, groovyDocTool);

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
            getLog().warn("Scope (" + scope + ") was not recognized.  Skipping argument.");
        }

        return properties;
    }

    /**
     * Sets up the Groovydoc links.
     *
     * @return the Groovydoc links
     * @throws ClassNotFoundException when a class needed for setting up Groovydoc links cannot be found
     * @throws InstantiationException when a class needed for setting up Groovydoc links cannot be instantiated
     * @throws IllegalAccessException when a method needed for setting up Groovydoc links cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up Groovydoc links cannot be completed
     */
    @SuppressWarnings("unchecked")
    protected List setupLinks() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        List linksList = new ArrayList();
        if (links != null && links.size() > 0) {
            Class linkArgumentClass = null;
            if (classWrangler.getGroovyVersion().compareTo(GROOVY_1_6_0_RC2) >= 0) {
                linkArgumentClass = classWrangler.getClass("org.codehaus.groovy.tools.groovydoc.LinkArgument");
            } else if (classWrangler.getGroovyVersion().compareTo(GROOVY_1_5_2) >= 0) {
                linkArgumentClass = classWrangler.getClass("org.codehaus.groovy.ant.Groovydoc$LinkArgument");
            }
            if (linkArgumentClass != null) {
                for (Link link : links) {
                    Object linkArgument = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(linkArgumentClass));
                    ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(linkArgumentClass, "setHref", String.class), linkArgument, link.getHref());
                    ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(linkArgumentClass, "setPackages", String.class), linkArgument, link.getPackages());
                    linksList.add(linkArgument);
                }
            } else {
                getLog().warn("Requested to use Groovydoc links, but your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support it (must be 1.5.2 or newer).  Ignoring links parameter.");
            }
        }

        return linksList;
    }

    /**
     * Instantiates a new GroovyDocTool.
     *
     * @param groovyDocToolClass the GroovyDocTool class
     * @param resourceManagerClass the ResourceManager lass
     * @param docProperties the documentation properties
     * @param classpathResourceManager the ClasspathResourceManager for the GroovyDocTool
     * @param sourceDirectories the source directories for the GroovyDocTool
     * @param groovyDocTemplateInfo the GroovyDocTemplateInfo for the GroovyDocTool
     * @param groovydocLinks the Groovydoc links
     * @return the GroovyDocTool to use in Groovydoc generation
     * @throws InstantiationException when a class needed for setting up Groovydoc tool cannot be instantiated
     * @throws IllegalAccessException when a method needed for setting up Groovydoc tool cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up Groovydoc tool cannot be completed
     */
    protected Object createGroovyDocTool(final Class groovyDocToolClass, final Class resourceManagerClass, final Properties docProperties, final Object classpathResourceManager, final List<String> sourceDirectories, final GroovyDocTemplateInfo groovyDocTemplateInfo, final List groovydocLinks) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object groovyDocTool;
        if (classWrangler.getGroovyVersion().compareTo(GROOVY_1_6_0_RC2) >= 0) {
            groovyDocTool = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyDocToolClass, resourceManagerClass, String[].class, String[].class, String[].class, String[].class, List.class, Properties.class),
                    classpathResourceManager,
                    sourceDirectories.toArray(new String[sourceDirectories.size()]),
                    groovyDocTemplateInfo.defaultDocTemplates(),
                    groovyDocTemplateInfo.defaultPackageTemplates(),
                    groovyDocTemplateInfo.defaultClassTemplates(),
                    groovydocLinks,
                    docProperties
            );
        } else if (classWrangler.getGroovyVersion().compareTo(GROOVY_1_5_2) >= 0) {
            groovyDocTool = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyDocToolClass, resourceManagerClass, String.class, String[].class, String[].class, String[].class, List.class),
                    classpathResourceManager,
                    sourceDirectories.get(0),
                    groovyDocTemplateInfo.defaultDocTemplates(),
                    groovyDocTemplateInfo.defaultPackageTemplates(),
                    groovyDocTemplateInfo.defaultClassTemplates(),
                    groovydocLinks
            );
            if (sourceDirectories.size() > 1) {
                getLog().warn("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support more than one Groovydoc source directory (must be 1.6-RC-2 or newer).  Only using first source directory (" + sourceDirectories.get(0) + ").");
            }
        } else {
            groovyDocTool = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyDocToolClass, resourceManagerClass, String.class, String[].class, String[].class, String[].class),
                    classpathResourceManager,
                    sourceDirectories.get(0),
                    groovyDocTemplateInfo.defaultDocTemplates(),
                    groovyDocTemplateInfo.defaultPackageTemplates(),
                    groovyDocTemplateInfo.defaultClassTemplates()
            );
            if (sourceDirectories.size() > 1) {
                getLog().warn("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support more than one Groovydoc source directory (must be 1.6-RC-2 or newer).  Only using first source directory (" + sourceDirectories.get(0) + ").");
            }
        }

        return groovyDocTool;
    }

    /**
     * Gets the Groovy sources without the Java sources (since the Java sources don't have Javadoc).
     *
     * @param sourceDirectories the source directories to get the Groovy sources from
     * @param fileSetManager the FileSetmanager to use to get the included files
     * @return the groovy sources
     */
    protected List<String> setupGroovyDocSources(final FileSet[] sourceDirectories, final FileSetManager fileSetManager) {
        List<String> javaSources = new ArrayList<String>();
        List<String> groovySources = new ArrayList<String>();
        List<String> possibleGroovyStubs = new ArrayList<String>();
        for (FileSet sourceDirectory : sourceDirectories) {
            List<String> sources = Arrays.asList(fileSetManager.getIncludedFiles(sourceDirectory));
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
        List<String> groovydocSources = new ArrayList<String>();
        groovydocSources.addAll(javaSources);
        groovydocSources.addAll(groovySources);

        return groovydocSources;
    }

    /**
     * Performs the Groovydoc generation.
     *
     * @param outputDirectory the directory to output the Groovydoc to
     * @param groovyDocToolClass the GroovyDocTool class
     * @param outputToolClass the OutputTool class
     * @param fileOutputTool the FileOutputTool to use for Groovydoc generation
     * @param groovydocSources the sources to
     * @param groovyDocTool the GroovyDocTool to use for Groovydoc generation
     * @throws IllegalAccessException when a method needed for Groovydoc generation cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for Groovydoc generation cannot be completed
     */
    protected void generateGroovydoc(final File outputDirectory, final Class groovyDocToolClass, final Class outputToolClass, final Object fileOutputTool, final List<String> groovydocSources, final Object groovyDocTool) throws InvocationTargetException, IllegalAccessException {
        getLog().debug("Adding sources to generate Groovydoc for:");
        if (getLog().isDebugEnabled()) {
            getLog().debug("    " + groovydocSources);
        }
        if (classWrangler.getGroovyVersion().compareTo(GROOVY_1_6_0_RC2) >= 0) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "add", List.class), groovyDocTool, groovydocSources);
        } else {
            for (String groovydocSource : groovydocSources) {
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "add", String.class), groovyDocTool, groovydocSource);
            }
        }
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "renderToOutput", outputToolClass, String.class), groovyDocTool, fileOutputTool, outputDirectory.getAbsolutePath());
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
