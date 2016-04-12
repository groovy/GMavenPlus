/*
 * Copyright 2014 the original author or authors.
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

package org.codehaus.gmavenplus.mojo;

import groovy.util.AntBuilder;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the AbstractToolsMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractToolsMojoTest {
    private TestMojo testMojo;
    @Spy
    private Properties properties;
    @Mock
    private MavenProject project;
    @Mock
    private MavenSession session;
    @Mock
    private List<Artifact> pluginArtifacts;
    @Mock
    private MojoExecution mojoExecution;
    @Mock
    private ClassWrangler classWrangler;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        testMojo = new TestMojo();
        testMojo.project = project;
        testMojo.session= session;
        testMojo.pluginArtifacts = pluginArtifacts;
        testMojo.mojoExecution = mojoExecution;
        testMojo.classWrangler = classWrangler;
        doReturn(AntBuilder.class).when(classWrangler).getClass(anyString());
    }

    @Test
    public void testInitializeProperties() {
        testMojo.initializeProperties();

        assertNotNull(testMojo.properties.get("project"));
        assertNotNull(testMojo.properties.get("session"));
        assertNotNull(testMojo.properties.get("pluginArtifacts"));
        assertNotNull(testMojo.properties.get("mojoExecution"));
    }

    @Test
    public void testInitializePropertiesNull() {
        testMojo.project = null;
        testMojo.session= null;
        testMojo.pluginArtifacts = null;
        testMojo.mojoExecution = mojoExecution;

        testMojo.initializeProperties();

        verify(properties, never()).put(eq("project"), any(MavenProject.class));
        verify(properties, never()).put(eq("session"), any(MavenSession.class));
        verify(properties, never()).put(eq("pluginArtifacts"), anyListOf(Artifact.class));
        verify(properties, never()).put(eq("mojoExecution"), any(MojoExecution.class));
    }

    @Test
    public void testInitializePropertiesAlreadyInProps() {
        testMojo.properties = properties;

        testMojo.initializeProperties();
        testMojo.initializeProperties();

        verify(properties, times(1)).put(eq("project"), any(MavenProject.class));
        verify(properties, times(1)).put(eq("session"), any(MavenSession.class));
        verify(properties, times(1)).put(eq("pluginArtifacts"), anyListOf(Artifact.class));
        verify(properties, times(1)).put(eq("mojoExecution"), anyListOf(MojoExecution.class));
    }

    public class TestMojo extends AbstractToolsMojo {
        public void execute() throws MojoExecutionException, MojoFailureException { }
    }

}
