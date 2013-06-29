/*
 * Shamelessly lifted (with minor modifications) from http://stackoverflow.com/questions/2659048/add-maven-build-classpath-to-plugin-execution-classpath
 */

package org.codehaus.gmavenplus.plexus;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A custom ComponentConfigurator which adds the project's compile time classpath elements
 *
 * @author Brian Jackson
 * @author Keegan Witt
 *
 * @plexus.component role="org.codehaus.plexus.component.configurator.ComponentConfigurator"
 *                   role-hint="include-project-dependencies"
 * @plexus.requirement role="org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup"
 *                     role-hint="default"
 */
public class IncludeProjectDependenciesComponentConfigurator extends AbstractComponentConfigurator {
    // TODO: add logging?
//    private static final Logger LOGGER = LoggerFactory.getLogger(IncludeProjectDependenciesComponentConfigurator.class);

    public void configureComponent(Object component, PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator,
                                   ClassRealm containerRealm, ConfigurationListener listener) throws ComponentConfigurationException {
        addProjectDependenciesToClassRealm(expressionEvaluator, containerRealm);
        converterLookup.registerConverter(new ClassRealmConverter(containerRealm));
        ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
        converter.processConfiguration(converterLookup, component, containerRealm.getParentClassLoader(), configuration, expressionEvaluator, listener);
    }

    private void addProjectDependenciesToClassRealm(ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm) throws ComponentConfigurationException {
        List compileClasspathElements;

        try {
            compileClasspathElements = (List) expressionEvaluator.evaluate("${project.compileClasspathElements}");
        } catch (ExpressionEvaluationException e) {
            throw new ComponentConfigurationException("There was a problem evaluating: ${project.compileClasspathElements}.", e);
        }

        // Add the project dependencies to the ClassRealm
        final URL[] urls = buildURLs(compileClasspathElements);
        for (URL url : urls) {
            containerRealm.addURL(url);
        }
    }

    private URL[] buildURLs(List compileClasspathElements) throws ComponentConfigurationException {
        List<URL> urls = new ArrayList<URL>(compileClasspathElements.size());
        for (Object element : compileClasspathElements) {
            try {
                final URL url = new File((String) element).toURI().toURL();
                urls.add(url);
//                LOGGER.debug("Added to project class loader: {}", url);
            } catch (MalformedURLException e) {
                throw new ComponentConfigurationException("Unable to access project dependency: " + element + ".", e);
            }
        }

        return urls.toArray(new URL[urls.size()]);
    }

}
