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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import gmavenplus.model.Version;
import gmavenplus.util.ReflectionUtils;
import org.codehaus.plexus.util.DirectoryScanner;


/**
 * Note that this mojo cannot be run on versions of Groovy before 1.5.0
 *
 * @author Keegan Witt
 */
public abstract class AbstractGroovyDocMojo extends AbstractGroovyMojo {

    /**
     * Location of the Groovy source files
     *
     * @parameter expression="${project.basedir}/src/main/groovy"
     * @readonly
     * @required
     */
    protected File sourceDirectory;

    /**
     * Location for the generated API docs
     *
     * @parameter expression="${project.build.directory}/site/gapidocs"
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
     * Location for the generated test API docs
     *
     * @parameter expression="${project.build.directory}/site/testgapidocs"
     * @readonly
     * @required
     */
    protected File testOutputDirectory;

    /**
     * @param sourceDirectory
     * @return
     */
    protected List<String> getSources(File sourceDirectory) {
        List<String> sources = new ArrayList<String>();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**/*.groovy"};
        ds.setIncludes(includes);
        ds.setBasedir(sourceDirectory);
        ds.setCaseSensitive(true);
        ds.scan();

        String[] files = ds.getIncludedFiles();
        for (String file : files) {
            sources.add(new File(sourceDirectory, file).getAbsolutePath());
        }

        return sources;
    }

    /**
     * @param sourceDirectory
     * @param outputDirectory
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected void generateGroovyDoc(File sourceDirectory, File outputDirectory) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // TODO: get working
        // get classes we need with reflection
        Class groovyDocToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.GroovyDocTool");
        Class fileOutputToolClass = Class.forName("org.codehaus.groovy.tools.groovydoc.FileOutputTool");
        Class classpathResourceManagerClass = Class.forName("org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager");
        Class groovyDocTemplateInfoClass = Class.forName("org.codehaus.groovy.tools.groovydoc.gstringTemplates.GroovyDocTemplateInfo");

        // set up GroovyDoc options
        List links = new ArrayList();
        Properties properties = new Properties();
        Object fileOutputTool = ReflectionUtils.findConstructor(fileOutputToolClass).newInstance();
        Object classpathResourceManager = ReflectionUtils.findConstructor(classpathResourceManagerClass).newInstance();
        Object groovyDocTool = ReflectionUtils.findConstructor(classpathResourceManagerClass, String[].class, String[].class, String[].class, String[].class, List.class, Properties.class).newInstance(
                classpathResourceManager,
                new String[] {sourceDirectory.getAbsolutePath()},
                ReflectionUtils.getField(ReflectionUtils.findField(groovyDocTemplateInfoClass, "DEFAULT_DOC_TEMPLATES", String[].class)),
                ReflectionUtils.getField(ReflectionUtils.findField(groovyDocTemplateInfoClass, "DEFAULT_PACKAGE_TEMPLATES", String[].class)),
                ReflectionUtils.getField(ReflectionUtils.findField(groovyDocTemplateInfoClass, "DEFAULT_CLASS_TEMPLATES", String[].class)),
                links,
                properties
        );
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "add", List.class), groovyDocTool, getSources(sourceDirectory));

        // generate GroovyDoc
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(groovyDocToolClass, "renderToOutput", fileOutputToolClass, String.class), groovyDocTool, fileOutputTool, outputDirectory.getAbsolutePath());
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

}
