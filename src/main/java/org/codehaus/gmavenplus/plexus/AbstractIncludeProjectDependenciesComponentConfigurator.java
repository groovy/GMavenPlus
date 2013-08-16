/*
 * Shamelessly lifted (with minor modifications) from http://stackoverflow.com/questions/2659048/add-maven-build-classpath-to-plugin-execution-classpath
 */

package org.codehaus.gmavenplus.plexus;

//import org.apache.maven.plugin.logging.Log;
//import org.apache.maven.plugin.logging.SystemStreamLog;

import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A custom ComponentConfigurator which adds the project's classpath elements.
 *
 * @author Brian Jackson
 * @author Keegan Witt
 */
public abstract class AbstractIncludeProjectDependenciesComponentConfigurator extends AbstractComponentConfigurator {
//    private static final Log LOG = new SystemStreamLog();

    /**
     * Adds the project's compile dependencies to the specified ClassRealm.
     *
     * @param expressionEvaluator The expression evaluator to use to get project elements
     * @param containerRealm The ClassRealm to add dependencies to
     * @throws ComponentConfigurationException When parsing components configuration fails
     */
    protected void addDependenciesToClassRealm(final ExpressionEvaluator expressionEvaluator, final Classpath classpath, final org.codehaus.plexus.classworlds.realm.ClassRealm containerRealm) throws ComponentConfigurationException {
        List classpathElements;

        try {
            classpathElements = (List) expressionEvaluator.evaluate("${project." + classpath.toString().toLowerCase() + "ClasspathElements}");
        } catch (ExpressionEvaluationException e) {
            throw new ComponentConfigurationException("There was a problem evaluating: ${project." + classpath.toString().toLowerCase() + "ClasspathElements}.", e);
        }

        // Add the project dependencies to the ClassRealm
        final URL[] urls = buildURLs(classpathElements);
        for (URL url : urls) {
            containerRealm.addURL(url);
        }
    }

    /**
     * Adds the project's compile dependencies to the specified ClassRealm.
     *
     * @param expressionEvaluator The expression evaluator to use to get project elements
     * @param containerRealm The ClassRealm to add dependencies to
     * @throws ComponentConfigurationException When parsing components configuration fails
     */
    protected void addDependenciesToClassRealm(final ExpressionEvaluator expressionEvaluator, final Classpath classpath, final org.codehaus.classworlds.ClassRealm containerRealm) throws ComponentConfigurationException {
        List classpathElements;

        try {
            classpathElements = (List) expressionEvaluator.evaluate("${project." + classpath.toString().toLowerCase() + "ClasspathElements}");
        } catch (ExpressionEvaluationException e) {
            throw new ComponentConfigurationException("There was a problem evaluating: ${project." + classpath.toString().toLowerCase() + "ClasspathElements}.", e);
        }

        // Add the project dependencies to the ClassRealm
        final URL[] urls = buildURLs(classpathElements);
        for (URL url : urls) {
            containerRealm.addConstituent(url);
        }
    }

    /**
     * Create an array of URLs for all the elements in the classpath.
     *
     * @param classpathElements The classpath elements to create URLs for
     * @return URLs for all the classpath elements
     * @throws ComponentConfigurationException When parsing components configuration fails
     */
    protected URL[] buildURLs(final List classpathElements) throws ComponentConfigurationException {
        List<URL> urls = new ArrayList<URL>(classpathElements.size());
        for (Object element : classpathElements) {
            try {
                final URL url = new File((String) element).toURI().toURL();
                urls.add(url);
                // commented out because debug seems to be on all the time
//                LOG.debug("Added to project class loader: " + url);
            } catch (MalformedURLException e) {
                throw new ComponentConfigurationException("Unable to access project dependency: " + element + ".", e);
            }
        }

        return urls.toArray(new URL[urls.size()]);
    }

    public enum Classpath {
        COMPILE,
        RUNTIME,
        TEST,
        SYSTEM
    }

}
