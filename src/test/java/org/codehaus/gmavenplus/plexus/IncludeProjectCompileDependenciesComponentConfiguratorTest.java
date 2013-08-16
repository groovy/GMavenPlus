/*
 * Copyright (C) 2013 the original author or authors.
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

package org.codehaus.gmavenplus.plexus;

import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Unit tests for the IncludeProjectCompileDependenciesComponentConfigurator class.
 *
 * @author Keegan Witt
 */
public class IncludeProjectCompileDependenciesComponentConfiguratorTest {
    private IncludeProjectCompileDependenciesComponentConfigurator configurator;

    @Before
    public void setup() {
        configurator = new IncludeProjectCompileDependenciesComponentConfigurator();
    }

    @Test
    public void testAddProjectCompileDependenciesToClassRealm() throws Exception {
        ExpressionEvaluator expressionEvaluator = Mockito.mock(ExpressionEvaluator.class);
        List classpathElements = Arrays.asList("CLASSPATH_ELEMENT");
        Mockito.doReturn(classpathElements).when(expressionEvaluator).evaluate(Mockito.anyString());
        org.codehaus.classworlds.ClassRealm containerRealm = Mockito.mock(org.codehaus.classworlds.ClassRealm.class);
        configurator.addDependenciesToClassRealm(expressionEvaluator, IncludeProjectCompileDependenciesComponentConfigurator.Classpath.COMPILE, containerRealm);
        Mockito.verify(expressionEvaluator, Mockito.times(1)).evaluate(Mockito.anyString());
        Mockito.verify(containerRealm, Mockito.times(1)).addConstituent(Mockito.any(URL.class));
    }


    @Test
    public void testAddProjectCompileDependenciesToPlexusClassRealm() throws Exception {
        ExpressionEvaluator expressionEvaluator = Mockito.mock(ExpressionEvaluator.class);
        List classpathElements = Arrays.asList("CLASSPATH_ELEMENT");
        Mockito.doReturn(classpathElements).when(expressionEvaluator).evaluate(Mockito.anyString());
        org.codehaus.plexus.classworlds.realm.ClassRealm containerRealm = Mockito.mock(org.codehaus.plexus.classworlds.realm.ClassRealm.class);
        configurator.addDependenciesToClassRealm(expressionEvaluator, IncludeProjectCompileDependenciesComponentConfigurator.Classpath.COMPILE, containerRealm);
        Mockito.verify(expressionEvaluator, Mockito.times(1)).evaluate(Mockito.anyString());
        Mockito.verify(containerRealm, Mockito.times(1)).addURL(Mockito.any(URL.class));
    }

    @Test
    public void testBuildURLs() throws Exception {
        List<String> elements = new ArrayList<String>();
        elements.add("ELEMENT_1");
        URL[] urls = configurator.buildURLs(elements);
        Assert.assertEquals(elements.size(), urls.length);
    }

}
