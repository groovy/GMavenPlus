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
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.gmavenplus.model.Scopes;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.groovyworkarounds.GroovyDocTemplateInfo;
import org.codehaus.gmavenplus.util.ReflectionUtils;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import org.apache.maven.shared.model.fileset.FileSet;


/**
 * The base groovydoc mojo, which all groovydoc mojos extend.
 *
 * @author Keegan Witt
 */
public abstract class AbstractGroovydocMojo extends AbstractGroovySourcesMojo {
    // TODO: support Groovy 1.5.0 - 1.6.1?
    /*
     * For some reason some NPE about a rootDoc occurs with older versions
     * (note that I used a different constructor and an addSource(File) instead
     * of addSources(List<File>) because that didn't exist back then.
     */

    /**
     * The minimum version of Groovy that this mojo supports.
     */
    protected static final Version MIN_GROOVY_VERSION = new Version(1, 6, 2);

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
     * The scope to generate Groovydoc for, should be one of:
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
     *
     * @parameter
     */
    protected List<Link> links;

    /**
     * Whether to include Java sources in groovydoc generation.
     *
     * @parameter default-value="true"
     */
    protected boolean groovydocJavaSources;

    /**
     * Generates the groovydoc for the specified sources.
     *
     * @param sourceDirectories The source directories to generate groovydoc for
     * @param outputDirectory The directory to save the generated groovydoc in
     * @throws ClassNotFoundException When a class needed for groovydoc generation cannot be found
     * @throws InstantiationException When a class needed for groovydoc generation cannot be instantiated
     * @throws IllegalAccessException When a method needed for groovydoc generation cannot be accessed
     * @throws InvocationTargetException When a reflection invocation needed for groovydoc generation cannot be completed
     */
    @SuppressWarnings("unchecked")
    protected void generateGroovydoc(final FileSet[] sourceDirectories, final File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // get classes we need with reflection
        Class<?> groovyDocToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.GroovyDocTool");
        Class<?> outputToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.OutputTool");
        Class<?> fileOutputToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.FileOutputTool");
        Class<?> resourceManagerClass = Class.forName("org.codehaus.groovy.tools.groovydoc.ResourceManager");
        Class<?> classpathResourceManagerClass = Class.forName("org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager");
        Class<?> linkArgumentClass = Class.forName("org.codehaus.groovy.tools.groovydoc.LinkArgument");

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

        // generate Groovydoc
        FileSetManager fileSetManager = new FileSetManager(getLog());
        List<String> sourceDirectoriesStrings = new ArrayList<String>();
        for (FileSet sourceDirectory : sourceDirectories) {
            sourceDirectoriesStrings.add(sourceDirectory.getDirectory());
        }
        List linksList = new ArrayList();
        if (links != null) {
            for (Link link : links) {
                Object linkArgument = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(linkArgumentClass));
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(linkArgumentClass, "setHref", String.class), linkArgument, link.getHref());
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(linkArgumentClass, "setPackages", String.class), linkArgument, link.getPackages());
                linksList.add(linkArgument);
            }
        }
        Object groovyDocTool = ReflectionUtils.invokeConstructor(ReflectionUtils.findConstructor(groovyDocToolClass, resourceManagerClass, String[].class, String[].class, String[].class, String[].class, List.class, Properties.class),
                classpathResourceManager,
                sourceDirectoriesStrings.toArray(new String[sourceDirectoriesStrings.size()]),
                GroovyDocTemplateInfo.DEFAULT_DOC_TEMPLATES,
                GroovyDocTemplateInfo.DEFAULT_PACKAGE_TEMPLATES,
                GroovyDocTemplateInfo.DEFAULT_CLASS_TEMPLATES,
                linksList,
                properties
        );
        getLog().debug("Adding sources to generate stubs for:");
        for (FileSet sourceDirectory : sourceDirectories) {
            getLog().debug("    " + Arrays.toString(fileSetManager.getIncludedFiles(sourceDirectory)));
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "add", List.class), groovyDocTool, Arrays.asList(fileSetManager.getIncludedFiles(sourceDirectory)));
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

    /**
     * Determines whether this mojo can be run with the version of Groovy supplied.
     * Must be >= 1.6.2 because not all the classes/methods needed were available
     * and functioning correctly in previous versions.
     *
     * @return <code>true</code> only if the version of Groovy supports this mojo
     */
    protected boolean groovyVersionSupportsAction() {
        return Version.parseFromString(getGroovyVersion()).compareTo(MIN_GROOVY_VERSION) >= 0;
    }

}
