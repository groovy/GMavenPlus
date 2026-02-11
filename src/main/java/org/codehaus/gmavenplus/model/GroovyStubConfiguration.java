package org.codehaus.gmavenplus.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Holds configuration for Groovy stub generation.
 *
 * @author Keegan Witt
 */
public class GroovyStubConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<File> stubSources;
    private List<?> classpath;
    private File outputDirectory;
    private IncludeClasspath includeClasspath;
    private boolean skipBytecodeCheck;
    private boolean debug;
    private boolean verbose;
    private int warningLevel;
    private int tolerance;
    private String sourceEncoding;
    private String targetBytecode;

    public GroovyStubConfiguration(Set<File> stubSources, List<?> classpath, File outputDirectory) {
        this.stubSources = stubSources;
        this.classpath = classpath;
        this.outputDirectory = outputDirectory;
    }

    public Set<File> getStubSources() {
        return stubSources;
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
