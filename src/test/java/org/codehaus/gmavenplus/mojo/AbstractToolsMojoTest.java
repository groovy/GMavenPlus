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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Properties;


/**
 * Unit tests for the AbstractToolsMojo class.
 *
 * @author Keegan Witt
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractToolsMojoTest {
    private TestMojo testMojo;
    @Spy
    private Properties properties;
    @Mock
    private MavenProject project;
    @Mock
    private MavenSession session;
    @Mock
    private Settings settings;
    @Mock
    private List<Artifact> pluginArtifacts;
    @Mock
    private ArtifactRepository localRepository;
    @Mock
    private List<MavenProject> reactorProjects;

    @Before
    public void init() {
        testMojo = new TestMojo();
        testMojo.project = project;
        testMojo.session= session;
        testMojo.settings = settings;
        testMojo.pluginArtifacts = pluginArtifacts;
        testMojo.localRepository = localRepository;
        testMojo.reactorProjects = reactorProjects;
    }

    @Test
    public void testInitializeProperties() {
        testMojo.initializeProperties();

        Assert.assertNotNull(testMojo.properties.get("settings"));
        Assert.assertNotNull(testMojo.properties.get("project"));
        Assert.assertNotNull(testMojo.properties.get("session"));
        Assert.assertNotNull(testMojo.properties.get("pluginArtifacts"));
        Assert.assertNotNull(testMojo.properties.get("localRepository"));
        Assert.assertNotNull(testMojo.properties.get("reactorProjects"));
    }

    @Test
    public void testInitializePropertiesNull() {
        testMojo.project = null;
        testMojo.session= null;
        testMojo.settings = null;
        testMojo.pluginArtifacts = null;
        testMojo.localRepository = null;
        testMojo.reactorProjects = null;

        testMojo.initializeProperties();

        Mockito.verify(properties, Mockito.never()).put(Mockito.eq("settings"), Mockito.any(Settings.class));
        Mockito.verify(properties, Mockito.never()).put(Mockito.eq("project"), Mockito.any(MavenProject.class));
        Mockito.verify(properties, Mockito.never()).put(Mockito.eq("session"), Mockito.any(MavenSession.class));
        Mockito.verify(properties, Mockito.never()).put(Mockito.eq("pluginArtifacts"), Mockito.anyListOf(Artifact.class));
        Mockito.verify(properties, Mockito.never()).put(Mockito.eq("localRepository"), Mockito.any(ArtifactRepository.class));
        Mockito.verify(properties, Mockito.never()).put(Mockito.eq("reactorProjects"), Mockito.anyListOf(MavenProject.class));
    }

    @Test
    public void testInitializePropertiesAlreadyInProps() {
        testMojo.properties = properties;

        testMojo.initializeProperties();
        testMojo.initializeProperties();

        Mockito.verify(properties, Mockito.times(1)).put(Mockito.eq("settings"), Mockito.any(Settings.class));
        Mockito.verify(properties, Mockito.times(1)).put(Mockito.eq("project"), Mockito.any(MavenProject.class));
        Mockito.verify(properties, Mockito.times(1)).put(Mockito.eq("session"), Mockito.any(MavenSession.class));
        Mockito.verify(properties, Mockito.times(1)).put(Mockito.eq("pluginArtifacts"), Mockito.anyListOf(Artifact.class));
        Mockito.verify(properties, Mockito.times(1)).put(Mockito.eq("localRepository"), Mockito.any(ArtifactRepository.class));
        Mockito.verify(properties, Mockito.times(1)).put(Mockito.eq("reactorProjects"), Mockito.anyListOf(MavenProject.class));
    }

    private class TestMojo extends AbstractToolsMojo {
        public void execute() throws MojoExecutionException, MojoFailureException { }
    }

}
