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
import org.codehaus.gmavenplus.javaparser.LanguageLevel;
import org.codehaus.gmavenplus.model.IncludeClasspath;
import org.codehaus.gmavenplus.model.Link;
import org.codehaus.gmavenplus.model.Scopes;
import org.codehaus.gmavenplus.model.internal.Version;
import org.codehaus.gmavenplus.util.FileUtils;

import org.codehaus.gmavenplus.model.GroovyDocConfiguration;
import org.codehaus.gmavenplus.util.GroovyCompiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * The base GroovyDoc mojo, which all GroovyDoc mojos extend.
 *
 * @author Keegan Witt
 * @since 1.0-beta-1
 */
public abstract class AbstractGroovyDocMojo extends AbstractGroovySourcesMojo {

    /**
     * Groovy 1.6.0 RC-1 version.
     */
    protected static final Version GROOVY_1_6_0_RC1 = new Version(1, 6, 0, "RC-1");

    /**
     * Groovy 1.5.8 version.
     */
    protected static final Version GROOVY_1_5_8 = new Version(1, 5, 8);

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
     * The Java language level to use for GroovyDoc generation.
     */
    @Parameter
    protected LanguageLevel languageLevel;

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
     * Override the default Groovydoc default top-level templates. Uses Groovy's standard templates by default.
     *
     * @since 1.10.1
     */
    @Parameter
    protected String[] defaultDocTemplates = null;

    /**
     * Override the default Groovydoc package-level templates. Uses Groovy's standard templates by default.
     *
     * @since 1.10.1
     */
    @Parameter
    protected String[] defaultPackageTemplates = null;

    /**
     * Override the default Groovydoc class-level templates. Uses Groovy's standard templates by default.
     *
     * @since 1.10.1
     */
    @Parameter
    protected String[] defaultClassTemplates = null;

    /**
     * Allows you to override the class that is normally org.codehaus.groovy.tools.groovydoc.GroovyDocTool, for use when
     * creating custom GroovyDoc implementations.
     *
     * @since 1.10.1
     */
    @Parameter
    protected String groovyDocToolClass = null;

    /**
     * Allows you to override the class that is normally org.codehaus.groovy.tools.groovydoc.OutputTool, for use when
     * creating custom GroovyDoc implementations.
     *
     * @since 1.10.1
     */
    @Parameter
    protected String outputToolClass = null;

    /**
     * Allows you to override the class that is normally org.codehaus.groovy.tools.groovydoc.FileOutputTool, for use
     * when creating custom GroovyDoc implementations.
     *
     * @since 1.10.1
     */
    @Parameter
    protected String fileOutputToolClass = null;

    /**
     * Allows you to override the class that is normally org.codehaus.groovy.tools.groovydoc.ResourceManager, for use
     * when creating custom GroovyDoc implementations.
     *
     * @since 1.10.1
     */
    @Parameter
    protected String resourceManagerClass = null;

    /**
     * Allows you to override the class that is normally org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager,
     * for use when creating custom GroovyDoc implementations.
     *
     * @since 1.10.1
     */
    @Parameter
    protected String classpathResourceManagerClass = null;

    /**
     * Allows you to override the class that is normally org.codehaus.groovy.tools.groovydoc.LinkArgument (or
     * org.codehaus.groovy.ant.Groovydoc$LinkArgument for Groovy older than 1.6-RC-2), for use when creating custom
     * GroovyDoc implementations.
     *
     * @since 1.10.1
     */
    @Parameter
    protected String linkArgumentClass = null;

    /**
     * Enable attaching GroovyDoc annotation. Requires Groovy 3.0.0 alpha-4 or newer.
     *
     * @since 1.11.0
     */
    @Parameter(defaultValue = "false")
    protected boolean attachGroovyDocAnnotation;

    /**
     * The Maven ToolchainManager.
     */
    @javax.inject.Inject
    protected org.apache.maven.toolchain.ToolchainManager toolchainManager;

    /**
     * The Maven Session.
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected org.apache.maven.execution.MavenSession session;

    /**
     * Whether to execute in a forked process.
     *
     * @since 1.13.0
     */
    @Parameter(property = "fork", defaultValue = "false")
    protected boolean fork;

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

        GroovyDocConfiguration configuration = new GroovyDocConfiguration(sourceDirectories, classpath, outputDirectory);
        configuration.setIncludeClasspath(includeClasspath);
        configuration.setDocProperties(setupProperties());
        configuration.setLinks(links);

        configuration.setAttachGroovyDocAnnotation(attachGroovyDocAnnotation);
        configuration.setDefaultDocTemplates(defaultDocTemplates);
        configuration.setDefaultPackageTemplates(defaultPackageTemplates);
        configuration.setDefaultClassTemplates(defaultClassTemplates);
        configuration.setGroovyDocToolClass(groovyDocToolClass);
        configuration.setOutputToolClass(outputToolClass);
        configuration.setFileOutputToolClass(fileOutputToolClass);
        configuration.setResourceManagerClass(resourceManagerClass);
        configuration.setClasspathResourceManagerClass(classpathResourceManagerClass);
        configuration.setLinkArgumentClass(linkArgumentClass);
        configuration.setWindowTitle(windowTitle);
        configuration.setDocTitle(docTitle);
        configuration.setFooter(footer);
        configuration.setHeader(header);
        configuration.setDisplayAuthor(displayAuthor);
        configuration.setDisplayAuthor(displayAuthor);
        configuration.setOverviewFile(overviewFile);
        configuration.setLanguageLevel(languageLevel != null ? languageLevel.toString() : null);

        configuration.setScope(scope);

        org.apache.maven.toolchain.Toolchain toolchain = toolchainManager.getToolchainFromBuildContext("jdk", session);
        if (toolchain != null) {
            getLog().info("Toolchain in gmavenplus-plugin: " + toolchain);
            performForkedGroovyDocGeneration(configuration, toolchain.findTool("java"));
        } else if (fork) {
            String javaExecutable = getJavaExecutable();
            getLog().info("Forking GroovyDoc generation using " + javaExecutable);
            performForkedGroovyDocGeneration(configuration, javaExecutable);
        } else {
            performInProcessGroovyDocGeneration(configuration);
        }

        // overwrite stylesheet.css with provided stylesheet (if configured)
        if (stylesheetFile != null) {
            copyStylesheet(outputDirectory);
        }
    }

    protected void performInProcessGroovyDocGeneration(GroovyDocConfiguration configuration) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        setupClassWrangler(configuration.getClasspath(), includeClasspath);
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

        GroovyCompiler compiler = new GroovyCompiler(classWrangler, getLog());
        compiler.generateGroovyDoc(configuration);
    }

    protected void performForkedGroovyDocGeneration(GroovyDocConfiguration configuration, String javaExecutable) throws InvocationTargetException {
        try {
             // Write configuration to file
            File configFile = File.createTempFile("groovy-doc-config", ".ser");
            configFile.deleteOnExit();
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(java.nio.file.Files.newOutputStream(configFile.toPath()))) {
                oos.writeObject(configuration);
            }

            // Build classpath for forked process (plugin + dependencies)
            String forkClasspath = buildForkClasspath();

            List<String> command = new ArrayList<>();
            command.add(javaExecutable);
            command.add("-cp");
            command.add(forkClasspath);
            command.add("org.codehaus.gmavenplus.util.ForkedGroovyCompiler");
            command.add(configFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new InvocationTargetException(new RuntimeException("Forked GroovyDoc generation failed with exit code " + exitCode));
            }
        } catch (IOException | InterruptedException e) {
            throw new InvocationTargetException(e);
        }
    }

    protected String buildForkClasspath() {
        StringBuilder cp = new StringBuilder();
        // Add plugin artifact
        cp.append(pluginDescriptor.getPluginArtifact().getFile().getAbsolutePath());

        // Add plugin dependencies
        for (org.apache.maven.artifact.Artifact artifact : pluginDescriptor.getArtifacts()) {
            cp.append(File.pathSeparator);
            cp.append(artifact.getFile().getAbsolutePath());
        }

        // Add maven-plugin-api jar which is 'provided' so not in getArtifacts()
        try {
            Class<?> logClass = org.apache.maven.plugin.logging.Log.class;
            java.security.CodeSource codeSource = logClass.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                String logJar = new File(codeSource.getLocation().toURI()).getAbsolutePath();
                cp.append(File.pathSeparator).append(logJar);
            }
        } catch (Exception e) {
            getLog().warn("Could not find maven-plugin-api jar to add to fork classpath", e);
        }

        return cp.toString();
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
                    bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(stylesheetFile.toPath()), stylesheetEncoding));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(stylesheetFile.toPath())));
                }
                StringBuilder css = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    css.append(line).append("\n");
                }
                File outfile = new File(outputDirectory, "stylesheet.css");
                if (stylesheetEncoding != null) {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outfile.toPath()), stylesheetEncoding));
                } else {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outfile.toPath())));
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
