/*
 * Shamelessly lifted (with minor modifications) from http://stackoverflow.com/questions/2659048/add-maven-build-classpath-to-plugin-execution-classpath
 */

package org.codehaus.gmavenplus.plexus;

//import org.apache.maven.plugin.logging.Log;
//import org.apache.maven.plugin.logging.SystemStreamLog;
// TODO: figure out why switching to Plexus ClassRealm instead of Classworlds ClassRealm causes a StackOverflowError in some projects
//import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A custom ComponentConfigurator which adds the project's compile-time classpath elements.
 *
 * @author Brian Jackson
 * @author Keegan Witt
 *
 * @plexus.component role="org.codehaus.plexus.component.configurator.ComponentConfigurator"
 *                   role-hint="include-project-dependencies"
 * @plexus.requirement role="org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup"
 *                     role-hint="default"
 */
@SuppressWarnings("deprecation")
public class IncludeProjectDependenciesComponentConfigurator extends AbstractComponentConfigurator {
//    private static final Log LOG = new SystemStreamLog();

    public void configureComponent(Object component, PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator,
                                   ClassRealm containerRealm, ConfigurationListener listener) throws ComponentConfigurationException {
        addProjectCompileDependenciesToClassRealm(expressionEvaluator, containerRealm);
        converterLookup.registerConverter(new ClassRealmConverter(containerRealm));
        ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
//        converter.processConfiguration(converterLookup, component, containerRealm.getParentClassLoader(), configuration, expressionEvaluator, listener);
        converter.processConfiguration(converterLookup, component, containerRealm.getClassLoader(), configuration, expressionEvaluator, listener);
    }

    /**
     * Adds the project's compile dependencies to the specified ClassRealm.
     *
     * @param expressionEvaluator the expression evaluator to use to get project elements
     * @param containerRealm the ClassRealm to add dependencies to
     * @throws ComponentConfigurationException
     */
    protected void addProjectCompileDependenciesToClassRealm(ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm) throws ComponentConfigurationException {
        List classpathElements;

        try {
            classpathElements = (List) expressionEvaluator.evaluate("${project.compileClasspathElements}");
        } catch (ExpressionEvaluationException e) {
            throw new ComponentConfigurationException("There was a problem evaluating: ${project.compileClasspathElements}.", e);
        }

        // Add the project dependencies to the ClassRealm
        final URL[] urls = buildURLs(classpathElements);
        for (URL url : urls) {
//            containerRealm.addURL(url);
            containerRealm.addConstituent(url);
        }
    }

    /**
     * Create an array of URLs for all the elements in the classpath.
     *
     * @param classpathElements the classpath elements to create URLs for
     * @return URLs for all the classpath elements
     * @throws ComponentConfigurationException
     */
    protected URL[] buildURLs(List classpathElements) throws ComponentConfigurationException {
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

}
