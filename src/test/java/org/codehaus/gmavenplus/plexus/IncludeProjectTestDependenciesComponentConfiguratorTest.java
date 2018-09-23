/*
 * Copyright 2013 the original author or authors.
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

package org.codehaus.gmavenplus.plexus;

import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the IncludeProjectTestDependenciesComponentConfigurator class.
 *
 * @author Keegan Witt
 */
public class IncludeProjectTestDependenciesComponentConfiguratorTest {
    @Spy
    private IncludeProjectTestDependenciesComponentConfigurator configurator = new IncludeProjectTestDependenciesComponentConfigurator();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConfigureComponent_1() throws Exception {
        ConverterLookup converterLookup = mock(ConverterLookup.class);
        Field modifiersField = configurator.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("converterLookup");
        modifiersField.setAccessible(true);
        modifiersField.set(configurator, converterLookup);
        Object component = mock(Object.class);
        PlexusConfiguration configuration = mock(PlexusConfiguration.class);
        ExpressionEvaluator expressionEvaluator = mock(ExpressionEvaluator.class);
        org.codehaus.plexus.classworlds.realm.ClassRealm containerRealm = mock(org.codehaus.plexus.classworlds.realm.ClassRealm.class);
        ConfigurationListener listener = mock(ConfigurationListener.class);
        doNothing().when(configurator).addDependenciesToClassRealm(any(ExpressionEvaluator.class), any(AbstractIncludeProjectDependenciesComponentConfigurator.Classpath.class), any(org.codehaus.plexus.classworlds.realm.ClassRealm.class));

        configurator.configureComponent(component, configuration, expressionEvaluator, containerRealm, listener);

        verify(configurator, atLeastOnce()).addDependenciesToClassRealm(any(ExpressionEvaluator.class), any(AbstractIncludeProjectDependenciesComponentConfigurator.Classpath.class), any(org.codehaus.plexus.classworlds.realm.ClassRealm.class));
        verify(converterLookup, atLeastOnce()).registerConverter(any(ClassRealmConverter.class));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testConfigureComponent_2() throws Exception {
        ConverterLookup converterLookup = mock(ConverterLookup.class);
        Field modifiersField = configurator.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("converterLookup");
        modifiersField.setAccessible(true);
        modifiersField.set(configurator, converterLookup);
        Object component = mock(Object.class);
        PlexusConfiguration configuration = mock(PlexusConfiguration.class);
        ExpressionEvaluator expressionEvaluator = mock(ExpressionEvaluator.class);
        org.codehaus.classworlds.ClassRealm containerRealm = mock(org.codehaus.classworlds.ClassRealm.class);
        ConfigurationListener listener = mock(ConfigurationListener.class);
        doNothing().when(configurator).addDependenciesToClassRealm(any(ExpressionEvaluator.class), any(AbstractIncludeProjectDependenciesComponentConfigurator.Classpath.class), any(org.codehaus.classworlds.ClassRealm.class));

        configurator.configureComponent(component, configuration, expressionEvaluator, containerRealm, listener);

        verify(configurator, atLeastOnce()).addDependenciesToClassRealm(any(ExpressionEvaluator.class), any(AbstractIncludeProjectDependenciesComponentConfigurator.Classpath.class), any(org.codehaus.classworlds.ClassRealm.class));
        verify(converterLookup, atLeastOnce()).registerConverter(any(ClassRealmConverter.class));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAddProjectTestDependenciesToClassRealm() throws Exception {
        ExpressionEvaluator expressionEvaluator = mock(ExpressionEvaluator.class);
        List classpathElements = singletonList("CLASSPATH_ELEMENT");
        doReturn(classpathElements).when(expressionEvaluator).evaluate(anyString());
        org.codehaus.classworlds.ClassRealm containerRealm = mock(org.codehaus.classworlds.ClassRealm.class);
        configurator.addDependenciesToClassRealm(expressionEvaluator, IncludeProjectTestDependenciesComponentConfigurator.Classpath.TEST, containerRealm);
        verify(expressionEvaluator, times(1)).evaluate(anyString());
        verify(containerRealm, times(1)).addConstituent(any(URL.class));
    }

    @Test
    public void testAddProjectTestDependenciesToPlexusClassRealm() throws Exception {
        ExpressionEvaluator expressionEvaluator = mock(ExpressionEvaluator.class);
        List classpathElements = singletonList("CLASSPATH_ELEMENT");
        doReturn(classpathElements).when(expressionEvaluator).evaluate(anyString());
        org.codehaus.plexus.classworlds.realm.ClassRealm containerRealm = mock(org.codehaus.plexus.classworlds.realm.ClassRealm.class);
        configurator.addDependenciesToClassRealm(expressionEvaluator, IncludeProjectTestDependenciesComponentConfigurator.Classpath.TEST, containerRealm);
        verify(expressionEvaluator, times(1)).evaluate(anyString());
        verify(containerRealm, times(1)).addURL(any(URL.class));
    }

    @Test
    public void testBuildURLs() throws Exception {
        List<String> elements = new ArrayList<String>();
        elements.add("ELEMENT_1");
        URL[] urls = configurator.buildURLs(elements);
        assertEquals(elements.size(), urls.length);
    }

}
