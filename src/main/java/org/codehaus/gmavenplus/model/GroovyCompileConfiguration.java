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

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Holds configuration for Groovy compilation.
 *
 * @author Keegan Witt
 */
public class GroovyCompileConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<File> sources;
    private List<?> classpath;
    private File compileOutputDirectory;
    private IncludeClasspath includeClasspath;
    private String groovyVersion;
    private boolean skipBytecodeCheck;
    private boolean debug;
    private boolean verbose;
    private int warningLevel;
    private int tolerance;
    private boolean invokeDynamic;
    private Boolean parallelParsing;
    private File configScript;
    private boolean parameters;
    private boolean previewFeatures;
    private String sourceEncoding;
    private String targetBytecode;

    public GroovyCompileConfiguration(Set<File> sources, List<?> classpath, File compileOutputDirectory) {
        this.sources = sources;
        this.classpath = classpath;
        this.compileOutputDirectory = compileOutputDirectory;
    }

    public Set<File> getSources() {
        return sources;
    }

    public List<?> getClasspath() {
        return classpath;
    }

    public File getCompileOutputDirectory() {
        return compileOutputDirectory;
    }

    public IncludeClasspath getIncludeClasspath() {
        return includeClasspath;
    }

    public void setIncludeClasspath(IncludeClasspath includeClasspath) {
        this.includeClasspath = includeClasspath;
    }

    public boolean isSkipBytecodeCheck() {
        return skipBytecodeCheck;
    }

    public void setSkipBytecodeCheck(boolean skipBytecodeCheck) {
        this.skipBytecodeCheck = skipBytecodeCheck;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public int getWarningLevel() {
        return warningLevel;
    }

    public void setWarningLevel(int warningLevel) {
        this.warningLevel = warningLevel;
    }

    public int getTolerance() {
        return tolerance;
    }

    public void setTolerance(int tolerance) {
        this.tolerance = tolerance;
    }

    public boolean isInvokeDynamic() {
        return invokeDynamic;
    }

    public void setInvokeDynamic(boolean invokeDynamic) {
        this.invokeDynamic = invokeDynamic;
    }

    public Boolean getParallelParsing() {
        return parallelParsing;
    }

    public void setParallelParsing(Boolean parallelParsing) {
        this.parallelParsing = parallelParsing;
    }

    public File getConfigScript() {
        return configScript;
    }

    public void setConfigScript(File configScript) {
        this.configScript = configScript;
    }

    public boolean isParameters() {
        return parameters;
    }

    public void setParameters(boolean parameters) {
        this.parameters = parameters;
    }

    public boolean isPreviewFeatures() {
        return previewFeatures;
    }

    public void setPreviewFeatures(boolean previewFeatures) {
        this.previewFeatures = previewFeatures;
    }

    public String getSourceEncoding() {
        return sourceEncoding;
    }

    public void setSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }

    public String getTargetBytecode() {
        return targetBytecode;
    }

    public void setTargetBytecode(String targetBytecode) {
        this.targetBytecode = targetBytecode;
    }
}
