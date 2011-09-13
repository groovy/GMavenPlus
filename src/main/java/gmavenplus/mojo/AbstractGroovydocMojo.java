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

import gmavenplus.model.Scopes;
import gmavenplus.model.Version;
import gmavenplus.util.ReflectionUtils;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;


/**
 * Note that this mojo cannot be run on versions of Groovy before 1.5.0
 *
 * @author Keegan Witt
 */
public abstract class AbstractGroovydocMojo extends AbstractGroovyMojo {

    /**
     * Groovy source files (relative paths).
     * Default: "${project.basedir}/src/main/groovy/**&#47;*.groovy"
     *
     * @parameter
     */
    protected FileSet[] sources;

    /**
     * Location for the generated API docs
     *
     * @parameter default-value="${project.build.directory}/gapidocs"
     */
    protected File groovydocOutputDirectory;

    /**
     * Groovy test source files (relative paths).
     * Default: "${project.basedir}/src/test/groovy/**&#47;*.groovy"
     *
     * @parameter
     */
    protected FileSet[] testSources;

    /**
     * Location for the generated test API docs
     *
     * @parameter default-value="${project.build.directory}/testgapidocs"
     */
    protected File testGroovydocOutputDirectory;

    /**
     * Window title
     *
     * @parameter default-value="Groovy Documentation"
     */
    protected String windowTitle;

    /**
     * Page title
     *
     * @parameter default-value="Groovy Documentation"
     */
    protected String docTitle;

    /**
     * Page footer
     *
     * @parameter default-value="Groovy Documentation"
     */
    protected String footer;

    /**
     * Page header
     *
     * @parameter default-value="Groovy Documentation"
     */
    protected String header;

    /**
     * Whether or not to display the author in the generated Groovydoc
     *
     * @parameter default-value="true"
     */
    protected boolean displayAuthor;

    /**
     * HTML file to be used for overview documentation
     *
     * @parameter
     */
    protected File overviewFile;

    /**
     * Stylesheet file (absolute path) to copy to output directory (will overwrite default stylesheet.css)
     *
     * @parameter
     */
    protected File stylesheetFile;

    /**
     * Scope to generate Groovydoc for, should be
     * "public", "protected", "package", or "private"
     *
     * @parameter default-value="private"
     */
    protected String scope;

    /**
     * @param fileSet
     * @return
     */
    protected List<String> getSources(FileSet fileSet) {
        List<String> files = new ArrayList<String>();
        FileSetManager fileSetManager = new FileSetManager(getLog());

        if (fileSet != null) {
            for (String include : Arrays.asList(fileSetManager.getIncludedFiles(fileSet))) {
                files.add(include);
            }
        } else {
            FileSet fs = new FileSet();
            String directory = project.getBasedir().getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "groovy";
            fs.setDirectory(directory);
            fs.setIncludes(Arrays.asList(DEFAULT_SOURCE_PATTERN));
            String[] includes = fileSetManager.getIncludedFiles(fs);
            for (String file : includes) {
                files.add(directory + File.separator + file);
            }
        }

        return files;
    }

    /**
     * @param sourceDirectories
     * @param outputDirectory
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected void generateGroovydoc(FileSet[] sourceDirectories, File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // get classes we need with reflection
        Class groovyDocToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.GroovyDocTool");
        Class outputToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.OutputTool");
        Class fileOutputToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.FileOutputTool");
        Class resourceManagerClass = Class.forName("org.codehaus.groovy.tools.groovydoc.ResourceManager");
        Class classpathResourceManagerClass = Class.forName("org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager");
        Class groovyDocTemplateInfoClass = Class.forName("org.codehaus.groovy.tools.groovydoc.gstringTemplates.GroovyDocTemplateInfo");

        // set up Groovydoc options
        List links = new ArrayList();
        Properties properties = new Properties();
        properties.setProperty("windowTitle", windowTitle);
        properties.setProperty("docTitle", docTitle);
        properties.setProperty("footer", footer);
        properties.setProperty("header", header);
        properties.setProperty("author", Boolean.toString(displayAuthor));
        properties.setProperty("overviewFile", overviewFile != null ? overviewFile.getAbsolutePath() : "");
        try {
            Scopes scopeVal = Scopes.valueOf(scope.toUpperCase());
            switch (scopeVal) {
                case PUBLIC:
                    properties.setProperty("publicScope", "true");
                    break;
                case PROTECTED:
                    properties.setProperty("protectedScope", "true");
                    break;
                case PACKAGE:
                    properties.setProperty("packageScope", "true");
                    break;
                case PRIVATE:
                    properties.setProperty("privateScope", "true");
                    break;
            }
        } catch (IllegalArgumentException e) {
            getLog().warn("Scope (" + scope + ") was not recognized.  Skipping argument.");
        }
        Object fileOutputTool = ReflectionUtils.findConstructor(fileOutputToolClass).newInstance();
        Object classpathResourceManager = ReflectionUtils.findConstructor(classpathResourceManagerClass).newInstance();

        // generate Groovydoc
        for (FileSet sourceDirectory : sourceDirectories) {
            Object groovyDocTool = ReflectionUtils.findConstructor(groovyDocToolClass, resourceManagerClass, String[].class, String[].class, String[].class, String[].class, List.class, Properties.class).newInstance(
                    classpathResourceManager,
                    new String[] {sourceDirectory.getDirectory()},
                    ReflectionUtils.getField(ReflectionUtils.findField(groovyDocTemplateInfoClass, "DEFAULT_DOC_TEMPLATES", String[].class)),
                    ReflectionUtils.getField(ReflectionUtils.findField(groovyDocTemplateInfoClass, "DEFAULT_PACKAGE_TEMPLATES", String[].class)),
                    ReflectionUtils.getField(ReflectionUtils.findField(groovyDocTemplateInfoClass, "DEFAULT_CLASS_TEMPLATES", String[].class)),
                    links,
                    properties
            );
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "add", List.class), groovyDocTool, getSources(sourceDirectory));
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "renderToOutput", outputToolClass, String.class), groovyDocTool, fileOutputTool, outputDirectory.getAbsolutePath());
        }

        // overwrite stylesheet.css with provided stylesheet (if configured)
        if (stylesheetFile != null) {
            getLog().info("Using stylesheet from " + stylesheetFile.getAbsolutePath() + ".");
            try {
                BufferedReader in = new BufferedReader(new FileReader(stylesheetFile));
                StringBuilder css = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    css.append(line).append("\n");
                }
                in.close();
                File outfile = new File(outputDirectory, "stylesheet.css");
                BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
                out.write(css.toString());
                out.flush();
                out.close();
            } catch (IOException e) {
                getLog().warn("Unable to copy specified stylesheet (" + stylesheetFile.getAbsolutePath() + ").", e);
            }
        }
    }

    /**
     * Determines whether this mojo can be run with the version of Groovy supplied.
     * Must be >= 1.5.0 because not all the classes needed were available in
     * previous versions.
     *
     * @return
     */
    protected boolean groovyVersionSupportsAction() {
        return Version.parseFromString(getGroovyVersion()).compareTo(new Version(1, 5, 0)) >= 0;
    }

    /**
     * @param sources
     */
    protected void setDefaultSourceDirectories(FileSet[] sources) {
        if (sources == null) {
            FileSet fileSet = new FileSet();
            String directory = project.getBasedir().getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "groovy";
            fileSet.setDirectory(directory);
            fileSet.setIncludes(Arrays.asList(DEFAULT_SOURCE_PATTERN));
            this.sources = new FileSet[]{fileSet};
        }
    }

}
