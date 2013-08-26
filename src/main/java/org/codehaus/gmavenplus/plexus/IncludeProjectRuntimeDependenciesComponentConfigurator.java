/*
 * Copyright 2013 Keegan Witt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Originally from http://stackoverflow.com/questions/2659048/add-maven-build-classpath-to-plugin-execution-classpath
 */

package org.codehaus.gmavenplus.plexus;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;


/**
 * A custom ComponentConfigurator which adds the project's runtime classpath elements.
 *
 * @author Brian Jackson
 * @author Keegan Witt
 *
 * @plexus.component role="org.codehaus.plexus.component.configurator.ComponentConfigurator"
 *                   role-hint="include-project-runtime-dependencies"
 * @plexus.requirement role="org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup"
 *                     role-hint="default"
 */
public class IncludeProjectRuntimeDependenciesComponentConfigurator extends AbstractIncludeProjectDependenciesComponentConfigurator {
//    private static final Log LOG = new SystemStreamLog();

    public void configureComponent(final Object component, final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator,
                                   final org.codehaus.plexus.classworlds.realm.ClassRealm containerRealm, final ConfigurationListener listener) throws ComponentConfigurationException {
        addDependenciesToClassRealm(expressionEvaluator, Classpath.RUNTIME, containerRealm);
        converterLookup.registerConverter(new ClassRealmConverter(containerRealm));
        ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
        converter.processConfiguration(converterLookup, component, containerRealm.getParentClassLoader(), configuration, expressionEvaluator, listener);
    }

    @SuppressWarnings("deprecation")
    public void configureComponent(final Object component, final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator,
                                   final org.codehaus.classworlds.ClassRealm containerRealm, final ConfigurationListener listener) throws ComponentConfigurationException {
        addDependenciesToClassRealm(expressionEvaluator, Classpath.RUNTIME, containerRealm);
        converterLookup.registerConverter(new ClassRealmConverter(containerRealm));
        ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
        converter.processConfiguration(converterLookup, component, containerRealm.getClassLoader(), configuration, expressionEvaluator, listener);
    }

}
