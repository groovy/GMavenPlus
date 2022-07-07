/*
 * Copyright (C) 2014 the original author or authors.
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

package org.codehaus.gmavenplus.util;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.gmavenplus.model.internal.Version;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

import static org.codehaus.gmavenplus.util.ReflectionUtils.findMethod;
import static org.codehaus.gmavenplus.util.ReflectionUtils.invokeStaticMethod;


/**
 * Handles getting Groovy classes and version from the specified classpath.
 *
 * @author Keegan Witt
 * @since 1.2
 */
public class ClassWrangler {

    /**
     * Cached Groovy version.
     */
    private String groovyVersion = null;

    /**
     * Cached whether Groovy supports invokedynamic (indy jar).
     */
    private Boolean isIndy = null;

    /**
     * ClassLoader to use for class wrangling.
     */
    private final ClassLoader classLoader;

    /**
     * Plugin log.
     */
    private final Log log;

    /**
     * Creates a new ClassWrangler using the specified parent ClassLoader, loaded with the items from the specified classpath.
     *
     * @param classpath         the classpath to load the new ClassLoader with
     * @param parentClassLoader the parent for the new ClassLoader used to use to load classes
     * @param pluginLog         the Maven log to use for logging
     * @throws MalformedURLException when a classpath element provides a malformed URL
     */
    public ClassWrangler(final List<?> classpath, final ClassLoader parentClassLoader, final Log pluginLog) throws MalformedURLException {
        log = pluginLog;
        classLoader = createNewClassLoader(classpath, parentClassLoader);
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    /**
     * Gets the version string of Groovy used from classpath.
     *
     * @return The version string of Groovy used by the project
     */
    public String getGroovyVersionString() {
        if (groovyVersion == null) {
            // this method should work for all Groovy versions >= 1.6.6
            try {
                Class<?> groovySystemClass = getClass("groovy.lang.GroovySystem");
                String ver = (String) invokeStaticMethod(findMethod(groovySystemClass, "getVersion"));
                if (ver != null && ver.length() > 0) {
                    groovyVersion = ver;
                }
            } catch (ClassNotFoundException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                // do nothing, will try another way
            }

            // this should work for Groovy versions < 1.6.6 (technically can work up to 1.9.0)
            if (groovyVersion == null) {
                log.info("Unable to get Groovy version from GroovySystem, trying InvokerHelper.");
                try {
                    Class<?> invokerHelperClass = getClass("org.codehaus.groovy.runtime.InvokerHelper");
                    String ver = (String) invokeStaticMethod(findMethod(invokerHelperClass, "getVersion"));
                    if (ver != null && ver.length() > 0) {
                        groovyVersion = ver;
                    }
                } catch (ClassNotFoundException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                    // do nothing, will try another way
                }
            }

            /*
             * This handles the circumstances in which neither the GroovySystem or InvokerHelper methods
             * worked (GAE with versions older than 1.6.6 is one example, see
             * https://jira.codehaus.org/browse/GROOVY-3884). One case this can't handle properly is uber
             * jars that include Groovy. It should also be noted this method assumes jars will be named
             * in the Maven convention (<artifactId>-<version>-<classifier>.jar).
             */
            if (groovyVersion == null) {
                log.warn("Unable to get Groovy version from InvokerHelper or GroovySystem, trying jar name.");
                String jar = getGroovyJar();
                int idx = Integer.MAX_VALUE;
                for (int i = 0; i < 9; i++) {
                    int newIdx = jar.indexOf("-" + i);
                    if (newIdx >= 0 && newIdx < idx) {
                        idx = newIdx;
                    }
                }
                if (idx < Integer.MAX_VALUE) {
                    groovyVersion = jar.substring(idx + 1, jar.length() - 4).replace("-indy", "").replace("-grooid", "");
                }
            }
        }

        return groovyVersion;
    }

    /**
     * Gets the version of Groovy used from the classpath.
     *
     * @return The version of Groovy used by the project
     */
    public Version getGroovyVersion() {
        try {
            return Version.parseFromString(getGroovyVersionString());
        } catch (Exception e) {
            throw new RuntimeException("Unable to determine Groovy version. Is Groovy declared as a dependency?");
        }
    }

    /**
     * Determines whether the detected Groovy version is the specified version or newer.
     *
     * @param detectedVersion  the detected Groovy version
     * @param compareToVersion the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is the specified version or newer, <code>false</code> otherwise
     */
    public static boolean groovyAtLeast(Version detectedVersion, Version compareToVersion) {
        return detectedVersion.compareTo(compareToVersion) >= 0;
    }

    /**
     * Determines whether the detected Groovy version is the specified version.
     *
     * @param detectedVersion  the detected Groovy version
     * @param compareToVersion the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is the specified version, <code>false</code> otherwise
     */
    public static boolean groovyIs(Version detectedVersion, Version compareToVersion) {
        return detectedVersion.compareTo(compareToVersion) == 0;
    }

    /**
     * Determines whether the detected Groovy version is newer than the specified version.
     *
     * @param detectedVersion  the detected Groovy version
     * @param compareToVersion the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is newer than the specified version, <code>false</code> otherwise
     */
    public static boolean groovyNewerThan(Version detectedVersion, Version compareToVersion) {
        return detectedVersion.compareTo(compareToVersion) > 0;
    }

    /**
     * Determines whether the detected Groovy version is older than the specified version.
     *
     * @param detectedVersion  the detected Groovy version
     * @param compareToVersion the version to compare the detected Groovy version to
     * @return <code>true</code> if the detected Groovy version is older than the specified version, <code>false</code> otherwise
     */
    public static boolean groovyOlderThan(Version detectedVersion, Version compareToVersion) {
        return detectedVersion.compareTo(compareToVersion) < 0;
    }

    /**
     * Gets whether the version of Groovy on the classpath supports invokedynamic.
     *
     * @return <code>true</code> if the version of Groovy uses invokedynamic,
     * <code>false</code> if not or Groovy dependency cannot be found.
     */
    public boolean isGroovyIndy() {
        if (isIndy == null) {
            try {
                getClass("org.codehaus.groovy.vmplugin.v8.IndyInterface");
                isIndy = true;
            } catch (ClassNotFoundException e1) {
                try {
                    getClass("org.codehaus.groovy.vmplugin.v7.IndyInterface");
                    isIndy = true;
                } catch (ClassNotFoundException e2) {
                    isIndy = false;
                }
            }
        }

        return isIndy;
    }

    /**
     * Logs the version of groovy used by this mojo.
     *
     * @param goal The goal to mention in the log statement showing Groovy version
     */
    public void logGroovyVersion(final String goal) {
        log.info("Using Groovy " + getGroovyVersionString() + " to perform " + goal + ".");
    }

    /**
     * Gets a class for the given class name.
     *
     * @param className the class name to retrieve the class for
     * @return the class for the given class name
     * @throws ClassNotFoundException when a class for the specified class name cannot be found
     */
    public Class<?> getClass(final String className) throws ClassNotFoundException {
        return Class.forName(className, true, classLoader);
    }

    /**
     * Returns the classloader used for loading classes.
     *
     * @return the classloader used for loading classes
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Creates a new ClassLoader with the specified classpath.
     *
     * @param classpath   the classpath (a list of file path Strings) to include in the new loader
     * @param classLoader the ClassLoader to use as the parent for the new CLassLoader
     * @return the new ClassLoader
     * @throws MalformedURLException when a classpath element provides a malformed URL
     */
    protected ClassLoader createNewClassLoader(final List<?> classpath, final ClassLoader classLoader) throws MalformedURLException {
        List<URL> urlsList = new ArrayList<>();
        for (Object classPathObject : classpath) {
            String path = (String) classPathObject;
            urlsList.add(new File(path).toURI().toURL());
        }
        URL[] urlsArray = urlsList.toArray(new URL[0]);
        return new URLClassLoader(urlsArray, classLoader);
    }

    /**
     * Returns the filename of the Groovy jar on the classpath.
     *
     * @return the Groovy jar filename
     */
    protected String getGroovyJar() {
        try {
            String groovyObjectClassPath = getJarPath();
            String groovyJar = null;
            if (groovyObjectClassPath != null) {
                groovyJar = groovyObjectClassPath.replaceAll("!.+", "");
                groovyJar = groovyJar.substring(groovyJar.lastIndexOf("/") + 1);
            }

            return groovyJar;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to determine Groovy version. Is Groovy declared as a dependency?");
        }
    }

    /**
     * Returns the path of the Groovy jar on the classpath.
     *
     * @return the path of the Groovy jar
     * @throws ClassNotFoundException when Groovy couldn't be found on the classpath
     */
    protected String getJarPath() throws ClassNotFoundException {
        Class<?> groovyObjectClass = getClass("groovy.lang.GroovyObject");
        String groovyObjectClassPath = String.valueOf(groovyObjectClass.getResource("/" + groovyObjectClass.getName().replace('.', '/') + ".class"));
        if (groovyObjectClassPath == null) {
            CodeSource codeSource = groovyObjectClass.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                groovyObjectClassPath = String.valueOf(codeSource.getLocation());
            }
        }
        return groovyObjectClassPath;
    }
}
