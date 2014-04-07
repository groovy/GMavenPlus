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

import com.google.common.io.Closer;
import com.google.common.io.Files;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.gmavenplus.groovyworkarounds.GroovyDocTemplateInfo;
import org.codehaus.gmavenplus.model.Scopes;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ReflectionUtils;

import java.io.*;
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
     * The stylesheet file (absolute path) to copy to output directory (will overwrite default stylesheet.css).
     *
     * @parameter
     */
    protected File stylesheetFile;

    /**
     * The encoding of stylesheetFile.
     *
     * @parameter default-value="UTF-8"
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
     * Links to include in the generated groovydoc (key is link href, value is comma-separated packages to use that link).
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
     * Generates the groovydoc for the specified sources.
     *
     * @param sourceDirectories The source directories to generate groovydoc for
     * @param classpath The classpath to use for compilation
     * @param outputDirectory The directory to save the generated groovydoc in
     * @throws ClassNotFoundException When a class needed for groovydoc generation cannot be found
     * @throws InstantiationException When a class needed for groovydoc generation cannot be instantiated
     * @throws IllegalAccessException When a method needed for groovydoc generation cannot be accessed
     * @throws InvocationTargetException When a reflection invocation needed for groovydoc generation cannot be completed
     * @throws java.net.MalformedURLException When a classpath element provides a malformed URL
     */
    @SuppressWarnings("unchecked")
    protected void generateGroovydoc(final FileSet[] sourceDirectories, final List classpath, final File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        if (sourceDirectories == null || sourceDirectories.length == 0) {
            getLog().info("No source directories specified for Groovydoc generation.  Skipping.");
            return;
        }
        if (getGroovyVersion().compareTo(new Version(1, 6, 0, "RC-1")) == 0) {
            // Groovy 1.6-RC-1 is excluded because of its dependency on org.apache.tools.ant.types.Path for constructing GroovyDocTool
            getLog().warn("Groovy 1.6-RC-1 is blacklisted from the supported Groovydoc versions because of its dependency on Ant.  Skipping Groovydoc generation.");
            return;
        }

        // create an isolated ClassLoader with all the appropriate project dependencies in it
        ClassLoader isolatedClassLoader = createNewClassLoader(classpath);


        // get classes we need with reflection
        Class groovyDocToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.GroovyDocTool", true, isolatedClassLoader);
        Class outputToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.OutputTool", true, isolatedClassLoader);
        Class fileOutputToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.FileOutputTool", true, isolatedClassLoader);
        Class resourceManagerClass = Class.forName("org.codehaus.groovy.tools.groovydoc.ResourceManager", true, isolatedClassLoader);
        Class classpathResourceManagerClass = Class.forName("org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager", true, isolatedClassLoader);

        // set up Groovydoc options
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
        Object fileOutputTool = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(fileOutputToolClass));
        Object classpathResourceManager = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(classpathResourceManagerClass));
        FileSetManager fileSetManager = new FileSetManager(getLog());
        List<String> sourceDirectoriesStrings = new ArrayList<String>();
        for (FileSet sourceDirectory : sourceDirectories) {
            sourceDirectoriesStrings.add(sourceDirectory.getDirectory());
        }
        GroovyDocTemplateInfo groovyDocTemplateInfo = new GroovyDocTemplateInfo(getGroovyVersion());
        Object groovyDocTool;
        List linksList = new ArrayList();
        if (links != null && links.size() > 0) {
            Class linkArgumentClass = null;
            if (getGroovyVersion().compareTo(new Version(1, 6, 0, "RC-2")) >= 0) {
                linkArgumentClass = Class.forName("org.codehaus.groovy.tools.groovydoc.LinkArgument", true, isolatedClassLoader);
            } else if (getGroovyVersion().compareTo(new Version(1, 5, 2)) >= 0) {
                linkArgumentClass = Class.forName("org.codehaus.groovy.ant.Groovydoc$LinkArgument", true, isolatedClassLoader);
            }
            if (linkArgumentClass != null) {
                for (Link link : links) {
                    Object linkArgument = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(linkArgumentClass));
                    ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(linkArgumentClass, "setHref", String.class), linkArgument, link.getHref());
                    ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(linkArgumentClass, "setPackages", String.class), linkArgument, link.getPackages());
                    linksList.add(linkArgument);
                }
            } else {
                getLog().warn("Requested to use Groovydoc links, but your Groovy version doesn't support it (must be 1.5.2 or newer).  Ignoring links parameter.");
            }
        }
        if (getGroovyVersion().compareTo(new Version(1, 6, 0, "RC-2")) < 0) {
            getLog().warn("Your Groovy version doesn't support Groovydoc documentation properties (docTitle, footer, header, displayAuthor, overviewFile, and scope).  You need Groovy 1.6-RC-2 or newer to support this.  Ignoring properties.");
        }
        if (getGroovyVersion().compareTo(new Version(1, 6, 0, "RC-2")) >= 0) {
            groovyDocTool = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyDocToolClass, resourceManagerClass, String[].class, String[].class, String[].class, String[].class, List.class, Properties.class),
                    classpathResourceManager,
                    sourceDirectoriesStrings.toArray(new String[sourceDirectoriesStrings.size()]),
                    groovyDocTemplateInfo.defaultDocTemplates(),
                    groovyDocTemplateInfo.defaultPackageTemplates(),
                    groovyDocTemplateInfo.defaultClassTemplates(),
                    linksList,
                    properties
            );
        } else if (getGroovyVersion().compareTo(new Version(1, 5, 2)) >= 0) {
            groovyDocTool = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyDocToolClass, resourceManagerClass, String.class, String[].class, String[].class, String[].class, List.class),
                    classpathResourceManager,
                    sourceDirectoriesStrings.get(0),
                    groovyDocTemplateInfo.defaultDocTemplates(),
                    groovyDocTemplateInfo.defaultPackageTemplates(),
                    groovyDocTemplateInfo.defaultClassTemplates(),
                    linksList
            );
            if (sourceDirectoriesStrings.size() > 1) {
                getLog().warn("Your Groovy version doesn't support more than one Groovydoc source directory (must be 1.6-RC-2 or newer).  Only using first source directory (" + sourceDirectoriesStrings.get(0) + ").");
            }
        } else {
            groovyDocTool = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyDocToolClass, resourceManagerClass, String.class, String[].class, String[].class, String[].class),
                    classpathResourceManager,
                    sourceDirectoriesStrings.get(0),
                    groovyDocTemplateInfo.defaultDocTemplates(),
                    groovyDocTemplateInfo.defaultPackageTemplates(),
                    groovyDocTemplateInfo.defaultClassTemplates()
            );
            if (sourceDirectoriesStrings.size() > 1) {
                getLog().warn("Your Groovy version doesn't support more than one Groovydoc source directory (must be 1.6-RC-2 or newer).  Only using first source directory (" + sourceDirectoriesStrings.get(0) + ").");
            }
        }

        // prevent Java stubs (which lack Javadoc) from overwriting Groovydoc by removing Java sources
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
                    possibleGroovyStubs.add(source.replaceFirst("\\." + Files.getFileExtension(source), ".java"));
                }
            }
        }
        javaSources.removeAll(possibleGroovyStubs);
        List<String> groovydocSources = new ArrayList<String>();
        groovydocSources.addAll(javaSources);
        groovydocSources.addAll(groovySources);

        // add the Groovydoc sources
        getLog().debug("Adding sources to generate Groovydoc for:");
        if (getLog().isDebugEnabled()) {
            getLog().debug("    " + groovydocSources);
        }

        // generate Groovydoc
        if (getGroovyVersion().compareTo(new Version(1, 6, 0, "RC-2")) >= 0) {
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "add", List.class), groovyDocTool, groovydocSources);
        } else {
            for (String groovydocSource : groovydocSources) {
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "add", String.class), groovyDocTool, groovydocSource);
            }
        }
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "renderToOutput", outputToolClass, String.class), groovyDocTool, fileOutputTool, outputDirectory.getAbsolutePath());

        // overwrite stylesheet.css with provided stylesheet (if configured)
        if (stylesheetFile != null) {
            copyStylesheet(outputDirectory);
        }
    }

    /**
     * Copies the stylesheet to the specified output directory.
     *
     * @param outputDirectory The output directory to copy the stylesheet to
     */
    private void copyStylesheet(final File outputDirectory) {
        getLog().info("Using stylesheet from " + stylesheetFile.getAbsolutePath() + ".");
        Closer closer = Closer.create();
        try {
            try {
                BufferedReader bufferedReader = closer.register(new BufferedReader(new InputStreamReader(new FileInputStream(stylesheetFile), stylesheetEncoding)));
                StringBuilder css = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    css.append(line).append("\n");
                }
                File outfile = new File(outputDirectory, "stylesheet.css");
                BufferedWriter bufferedWriter = closer.register(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), stylesheetEncoding)));
                bufferedWriter.write(css.toString());
            } catch (Throwable throwable) {
                throw closer.rethrow(throwable);
            } finally {
                closer.close();
            }
        } catch (IOException e) {
            getLog().warn("Unable to copy specified stylesheet (" + stylesheetFile.getAbsolutePath() + ").");
        }
    }

}
