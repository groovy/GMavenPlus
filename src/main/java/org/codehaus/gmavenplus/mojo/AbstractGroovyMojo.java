package org.codehaus.gmavenplus.mojo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.codehaus.gmavenplus.model.IncludeClasspath;
import org.codehaus.gmavenplus.model.internal.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import static java.util.Collections.emptyList;


/**
 * The base mojo class, which all other mojos extend.
 *
 * @author Keegan Witt
 */
public abstract class AbstractGroovyMojo extends AbstractMojo {

    /**
     * The pattern defining Groovy files.
     */
    protected static final String GROOVY_SOURCES_PATTERN = "**" + File.separator + "*.groovy";

    /**
     * The pattern defining Java stub files.
     */
    protected static final String JAVA_SOURCES_PATTERN = "**" + File.separator + "*.java";

    /**
     * Java 1.7 version.
     */
    protected static final Version JAVA_1_7 = new Version(1, 7);

    /**
     * Java 1.8 version.
     */
    protected static final Version JAVA_1_8 = new Version(1, 8);

    /**
     * Java 1.8 version.
     */
    protected static final Version JAVA_12 = new Version(12);

    /**
     * Groovy 1.5.0 version.
     */
    protected static final Version GROOVY_1_5_0 = new Version(1, 5, 0);

    /**
     * The wrangler to use to work with Groovy classes, classpaths, classLoaders, and versions.
     */
    protected ClassWrangler classWrangler;

    // note that all supported parameter expressions can be found here: https://git-wip-us.apache.org/repos/asf?p=maven.git;a=blob;f=maven-core/src/main/java/org/apache/maven/plugin/PluginParameterExpressionEvaluator.java;hb=HEAD

    /**
     * The Maven project this plugin is being used on.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    /**
     * The Maven Session this plugin is being used on.
     */
    @Parameter(property = "session", required = true, readonly = true)
    protected MavenSession session;

    /**
     * The plugin dependencies.
     */
    @Parameter(property = "plugin.artifacts", required = true, readonly = true)
    protected List<Artifact> pluginArtifacts;

    /**
     * The plugin's mojo execution.
     */
    @Parameter(property = "mojoExecution", required = true, readonly = true)
    protected MojoExecution mojoExecution;

    /**
     * The plugin descriptor.
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    protected PluginDescriptor pluginDescriptor;

    /**
     * The minimum version of Groovy that this mojo supports (1.5.0 by
     * default, but other mojos can override).
     */
    protected Version minGroovyVersion = GROOVY_1_5_0;

    /**
     * Logs the plugin classpath.
     */
    protected void logPluginClasspath() {
        if (getLog().isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pluginArtifacts.size(); i++) {
                sb.append(pluginArtifacts.get(i).getFile());
                if (i < pluginArtifacts.size() - 1) {
                    sb.append(", ");
                }
            }
            getLog().debug("Plugin classpath:\n" + sb);
        }
    }

    /**
     * Determines whether the version of Java executing this mojo supports invokedynamic (is at least 1.7).
     *
     * @return <code>true</code> if the running Java supports invokedynamic, <code>false</code> otherwise
     */
    protected boolean isJavaSupportIndy() {
        return getJavaVersion().compareTo(JAVA_1_7, false) >= 0;
    }

    /**
     * Determines whether the version of Java executing this mojo supports preview features (is at least 12).
     *
     * @return <code>true</code> if the running Java supports preview features, <code>false</code> otherwise
     */
    protected boolean isJavaSupportPreviewFeatures() {
        return getJavaVersion().compareTo(JAVA_12, false) >= 0;
    }

    /**
     * Determines whether the version of Java executing this mojo supports JEP 118 (is at least 1.8).
     *
     * @return <code>true</code> if the running Java supports parameters, <code>false</code> otherwise
     */
    protected boolean isJavaSupportParameters() {
        return getJavaVersion().compareTo(JAVA_1_8, false) >= 0;
    }

    /**
     * Gets the version of Java executing this mojo as a Version object.
     *
     * @return a Version object of the running Java version
     */
    protected Version getJavaVersion() {
        return Version.parseFromString(getJavaVersionString());
    }

    /**
     * Gets the version of Java executing this mojo as a String.
     *
     * @return a String of the running Java version
     */
    protected String getJavaVersionString() {
        return System.getProperty("java.version");
    }

    /**
     * Determines whether this mojo can be run with the version of Groovy supplied.
     *
     * @return <code>true</code> only if the version of Groovy supports this mojo
     */
    protected boolean groovyVersionSupportsAction() {
        return classWrangler.getGroovyVersion() != null && groovyAtLeast(minGroovyVersion);
    }

    /**
     * Determines whether the detected Groovy version is the specified version or newer.
     *
     * @param version the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is the specified version or newer, <code>false</code> otherwise
     */
    protected boolean groovyAtLeast(Version version) {
        return ClassWrangler.groovyAtLeast(classWrangler.getGroovyVersion(), version);
    }

    /**
     * Determines whether the detected Groovy version is the specified version.
     *
     * @param version the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is the specified version, <code>false</code> otherwise
     */
    protected boolean groovyIs(Version version) {
        return ClassWrangler.groovyIs(classWrangler.getGroovyVersion(), version);
    }

    /**
     * Determines whether the detected Groovy version is newer than the specified version.
     *
     * @param version the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is newer than the specified version, <code>false</code> otherwise
     */
    protected boolean groovyNewerThan(Version version) {
        return ClassWrangler.groovyNewerThan(classWrangler.getGroovyVersion(), version);
    }

    /**
     * Determines whether the detected Groovy version is older than the specified version.
     *
     * @param version the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is older than the specified version, <code>false</code> otherwise
     */
    protected boolean groovyOlderThan(Version version) {
        return ClassWrangler.groovyOlderThan(classWrangler.getGroovyVersion(), version);
    }

    /**
     * Gets whether the version of Groovy on the classpath supports invokedynamic.
     *
     * @return <code>true</code> if the version of Groovy uses invokedynamic,
     * <code>false</code> if not or Groovy dependency cannot be found
     */
    protected boolean isGroovyIndy() {
        return classWrangler.isGroovyIndy();
    }

    /**
     * Instantiate a ClassWrangler.
     *
     * @param classpath        the classpath to load onto a new classloader (if includeClasspath is <code>PROJECT_ONLY</code>)
     * @param includeClasspath whether to use a shared classloader that includes both the project classpath and plugin classpath.
     * @throws MalformedURLException when a classpath element provides a malformed URL
     */
    protected void setupClassWrangler(List<?> classpath, IncludeClasspath includeClasspath) throws MalformedURLException {
        if (IncludeClasspath.PROJECT_ONLY.equals(includeClasspath)) {
            getLog().info("Using isolated classloader, without GMavenPlus classpath.");
            classWrangler = new ClassWrangler(classpath, ClassLoader.getSystemClassLoader(), getLog());
        } else if (IncludeClasspath.PROJECT_AND_PLUGIN.equals(includeClasspath)) {
            getLog().info("Using plugin classloader, includes GMavenPlus and project classpath.");
            classWrangler = new ClassWrangler(classpath, getClass().getClassLoader(), getLog());
        } else {
            getLog().info("Using plugin classloader, includes GMavenPlus classpath, but not project classpath.");
            classWrangler = new ClassWrangler(emptyList(), getClass().getClassLoader(), getLog());
        }
    }

    /**
     * Gets the Java executable to use for forked execution.
     *
     * @return the Java executable path
     */
    protected String getJavaExecutable() {
        // Try to get operation system process via JDK 9+ ProcessHandle
        try {
            Class<?> processHandleClass = Class.forName("java.lang.ProcessHandle");
            // ProcessHandle.current()
            java.lang.reflect.Method currentMethod = processHandleClass.getMethod("current");
            Object currentProcess = currentMethod.invoke(null);

            // ProcessHandle.info()
            java.lang.reflect.Method infoMethod = processHandleClass.getMethod("info");
            Object info = infoMethod.invoke(currentProcess);

            // ProcessHandle.Info.command()
            Class<?> infoClass = Class.forName("java.lang.ProcessHandle$Info");
            java.lang.reflect.Method commandMethod = infoClass.getMethod("command");
            @SuppressWarnings("unchecked")
            java.util.Optional<String> commandConfig = (java.util.Optional<String>) commandMethod.invoke(info);

            if (commandConfig.isPresent()) {
                return commandConfig.get();
            }
        } catch (Exception e) {
            // ignore, we are probably on Java 8 or the OS doesn't support this
        }

        String javaHome = System.getProperty("java.home");
        String javaExecutable = javaHome + File.separator + "bin" + File.separator + "java";
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            javaExecutable += ".exe";
        }
        return javaExecutable;
    }
}
