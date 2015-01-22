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
import org.codehaus.gmavenplus.model.Version;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;


/**
 * The base compile mojo, which all compile mojos extend.
 *
 * @author Keegan Witt
 * @since 1.2
 */
public class ClassWrangler {

    /**
     * Cached Groovy version.
     */
    protected String groovyVersion = null;

    /**
     * ClassLoader to use for class wrangling.
     */
    protected ClassLoader classLoader;

    /**
     * Plugin log.
     */
    protected Log log;

    /**
     * Creates a new ClassWrangler using the specified ClassLoader.
     *
     * @param classLoaderForLoading the ClassLoader to use to load classes
     * @param pluginLog the Maven log to use for logging
     */
    public ClassWrangler(final ClassLoader classLoaderForLoading, final Log pluginLog) {
        log = pluginLog;
        classLoader = classLoaderForLoading;
    }

    /**
     * Creates a new ClassWrangler using a new ClassLoader, loaded with the
     * items from the specified classpath.
     *
     * @param classpath the classpath to load the new ClassLoader with
     * @param pluginLog the Maven log to use for logging
     * @throws MalformedURLException
     */
    public ClassWrangler(final List classpath, final Log pluginLog) throws MalformedURLException {
        log = pluginLog;
        // create an isolated ClassLoader with all the appropriate project dependencies in it
        classLoader = createNewClassLoader(classpath);
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    /**
     * Gets the version string of Groovy used from the dependency information.
     *
     * @return The version string of Groovy used by the project
     */
    public String getGroovyVersionString() {
        /*
         * You can call InvokerHelper.getVersion() for versions 1.0 - 1.8.x but
         * not for 1.9+.
         * You can call GroovySystem.getVersion() for versions 1.6.6+.
         * And for some reason InvokerHelper.getVersion() was returning an empty
         * String for 1.5.0, so I decided to just get it from the jar itself.
         */
        if (groovyVersion == null) {
            String jar = getGroovyJar();
            int idx = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                int newIdx = jar.indexOf("-" + i);
                if (newIdx >= 0 && newIdx < idx) {
                    idx = newIdx;
                }
            }
            if (idx < Integer.MAX_VALUE) {
                groovyVersion = jar.substring(idx + 1, jar.length() - 4);
            }
        }

        return groovyVersion;
    }

    /**
     * Gets the version of Groovy used from the dependency information.
     *
     * @return The version of Groovy used by the project
     */
    public Version getGroovyVersion() {
        try {
            return Version.parseFromString(getGroovyVersionString().replace("-indy", "").replace("-grooid", ""));
        } catch (Exception e) {
            log.error("Unable to determine Groovy version.  Is Groovy declared as a dependency?");
            return null;
        }
    }

    /**
     * Gets the version of Groovy used from the dependency information.
     *
     * @return <code>true</code> if the version of Groovy uses InvokeDynamic,
     *         <code>false</code> if not or Groovy dependency cannot be found.
     */
    public boolean isGroovyIndy() {
        return getGroovyVersionString().contains("-indy");
    }

    /**
     * Returns the filename of the Groovy jar on the classpath.
     *
     * @return the Groovy jar filename
     */
    public String getGroovyJar() {
        try {
            String groovyObjectClassPath = getJarPath();
            String groovyJar = null;
            if (groovyObjectClassPath != null) {
                groovyJar = groovyObjectClassPath.replaceAll("!.+", "");
                groovyJar = groovyJar.substring(groovyJar.lastIndexOf("/") + 1, groovyJar.length());
            }

            return groovyJar;
        } catch (ClassNotFoundException e) {
            log.error("Unable to determine Groovy version.  Is Groovy declared as a dependency?");
            return null;
        }
    }

    /**
     * Returns the path of the Groovy jar on the classpath.
     *
     * @return the path of the Groovy jar
     * @throws ClassNotFoundException when Groovu couldn't be found on the classpath
     */
    protected String getJarPath() throws ClassNotFoundException {
        Class groovyObjectClass = Class.forName("groovy.lang.GroovyObject", true, classLoader);
        String groovyObjectClassPath = String.valueOf(groovyObjectClass.getResource("/" + groovyObjectClass.getName().replace('.', '/') + ".class"));
        if (groovyObjectClassPath == null) {
            CodeSource codeSource = groovyObjectClass.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                groovyObjectClassPath = String.valueOf(codeSource.getLocation());
            }
        }
        return groovyObjectClassPath;
    }

    /**
     * Logs the version of groovy used by this mojo.
     *
     * @param goal The goal to mention in the log statement showing Groovy version
     */
    public void logGroovyVersion(final String goal) {
        if (log.isInfoEnabled()) {
            log.info("Using Groovy " + getGroovyVersionString() + " to perform " + goal + ".");
        }
    }

    /**
     * Creates a new ClassLoader with the specified classpath.
     *
     * @param classpath the classpath (a list of file path Strings) to include in the new loader
     * @return the new ClassLoader
     * @throws MalformedURLException when a classpath element provides a malformed URL
     */
    public ClassLoader createNewClassLoader(final List classpath) throws MalformedURLException {
        List<URL> urlsList = new ArrayList<URL>();
        for (Object classPathObject : classpath) {
            String path = (String) classPathObject;
            urlsList.add(new File(path).toURI().toURL());
        }
        URL[] urlsArray = urlsList.toArray(new URL[urlsList.size()]);
        return new URLClassLoader(urlsArray, ClassLoader.getSystemClassLoader());
    }

    /**
     * Gets a class for the given class name.
     *
     * @param className the class name to retrieve the class for
     * @return the class for the given class name
     * @throws ClassNotFoundException when a class for the specified class name cannot be found
     */
    public Class getClass(final String className) throws ClassNotFoundException {
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

}
