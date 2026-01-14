/*
 * Copyright (C) 2025 the original author or authors.
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

package org.codehaus.gmavenplus.model;

import org.apache.maven.shared.model.fileset.FileSet;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * Holds configuration for GroovyDoc generation.
 *
 * @author Keegan Witt
 * @since 1.13.0
 */
public class GroovyDocConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private FileSet[] sourceDirectories;
    private List<?> classpath;
    private File outputDirectory;
    private IncludeClasspath includeClasspath;
    private Properties docProperties;
    private List<Link> links;
    private String[] defaultDocTemplates;
    private String[] defaultPackageTemplates;
    private String[] defaultClassTemplates;
    private String groovyDocToolClass;
    private String outputToolClass;
    private String fileOutputToolClass;
    private String resourceManagerClass;
    private String classpathResourceManagerClass;
    private String linkArgumentClass;
    private boolean attachGroovyDocAnnotation;
    private File overviewFile;
    private String scope;
    private String windowTitle;
    private String docTitle;
    private String footer;
    private String header;
    private boolean displayAuthor;
    private String languageLevel;

    public GroovyDocConfiguration(FileSet[] sourceDirectories, List<?> classpath, File outputDirectory) {
        this.sourceDirectories = sourceDirectories;
        this.classpath = classpath;
        this.outputDirectory = outputDirectory;
    }

    public FileSet[] getSourceDirectories() {
        return sourceDirectories;
    }

    public List<?> getClasspath() {
        return classpath;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public IncludeClasspath getIncludeClasspath() {
        return includeClasspath;
    }

    public void setIncludeClasspath(IncludeClasspath includeClasspath) {
        this.includeClasspath = includeClasspath;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String[] getDefaultDocTemplates() {
        return defaultDocTemplates;
    }

    public void setDefaultDocTemplates(String[] defaultDocTemplates) {
        this.defaultDocTemplates = defaultDocTemplates;
    }

    public String[] getDefaultPackageTemplates() {
        return defaultPackageTemplates;
    }

    public void setDefaultPackageTemplates(String[] defaultPackageTemplates) {
        this.defaultPackageTemplates = defaultPackageTemplates;
    }

    public String[] getDefaultClassTemplates() {
        return defaultClassTemplates;
    }

    public void setDefaultClassTemplates(String[] defaultClassTemplates) {
        this.defaultClassTemplates = defaultClassTemplates;
    }

    public Properties getDocProperties() {
        return docProperties;
    }

    public void setDocProperties(Properties docProperties) {
        this.docProperties = docProperties;
    }

    public String getGroovyDocToolClass() {
        return groovyDocToolClass;
    }

    public void setGroovyDocToolClass(String groovyDocToolClass) {
        this.groovyDocToolClass = groovyDocToolClass;
    }

    public String getOutputToolClass() {
        return outputToolClass;
    }

    public void setOutputToolClass(String outputToolClass) {
        this.outputToolClass = outputToolClass;
    }

    public String getFileOutputToolClass() {
        return fileOutputToolClass;
    }

    public void setFileOutputToolClass(String fileOutputToolClass) {
        this.fileOutputToolClass = fileOutputToolClass;
    }

    public String getResourceManagerClass() {
        return resourceManagerClass;
    }

    public void setResourceManagerClass(String resourceManagerClass) {
        this.resourceManagerClass = resourceManagerClass;
    }

    public String getClasspathResourceManagerClass() {
        return classpathResourceManagerClass;
    }

    public void setClasspathResourceManagerClass(String classpathResourceManagerClass) {
        this.classpathResourceManagerClass = classpathResourceManagerClass;
    }

    public String getLinkArgumentClass() {
        return linkArgumentClass;
    }

    public void setLinkArgumentClass(String linkArgumentClass) {
        this.linkArgumentClass = linkArgumentClass;
    }

    public boolean isAttachGroovyDocAnnotation() {
        return attachGroovyDocAnnotation;
    }

    public void setAttachGroovyDocAnnotation(boolean attachGroovyDocAnnotation) {
        this.attachGroovyDocAnnotation = attachGroovyDocAnnotation;
    }

    public File getOverviewFile() {
        return overviewFile;
    }

    public void setOverviewFile(File overviewFile) {
        this.overviewFile = overviewFile;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isDisplayAuthor() {
        return displayAuthor;
    }

    public void setDisplayAuthor(boolean displayAuthor) {
        this.displayAuthor = displayAuthor;
    }

    public String getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(String languageLevel) {
        this.languageLevel = languageLevel;
    }
}
