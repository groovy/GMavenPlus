/*
 * Copyright (C) 2011 the original author or authors.
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

package org.codehaus.gmavenplus.mojo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.gmavenplus.model.Version;
import org.codehaus.gmavenplus.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Unit tests for the AbstractGroovyMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGroovyMojoTest {
    private static final String GROOVY_VERSION = "2.0.5";
    private AbstractGroovyMojo testMojo;

    @Before
    public void setup() throws Exception {
        testMojo = Mockito.spy(new TestGroovyMojo());
        Field groovyDependency = ReflectionUtils.findField(AbstractGroovyMojo.class, "groovyDependency", Artifact.class);
        groovyDependency.setAccessible(true);
        groovyDependency.set(null, null);
    }

    @Test
    public void testIsGroovyIndy() {
        testMojo.project = Mockito.mock(MavenProject.class);
        Dependency groovyDependency = new Dependency();
        groovyDependency.setGroupId("org.codehaus.groovy");
        groovyDependency.setArtifactId("groovy-all");
        groovyDependency.setVersion(GROOVY_VERSION);
        groovyDependency.setType("jar");
        List<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.add(groovyDependency);
        Mockito.when(testMojo.project.getCompileDependencies()).thenReturn(dependencies);

        Assert.assertFalse(testMojo.isGroovyIndy());
        Assert.assertEquals(GROOVY_VERSION, testMojo.getGroovyVersionString());
    }

    @Test
    public void testIsGroovyNotIndy() {
        testMojo.project = Mockito.mock(MavenProject.class);
        Dependency groovyDependency = new Dependency();
        groovyDependency.setGroupId("org.codehaus.groovy");
        groovyDependency.setArtifactId("groovy-all");
        groovyDependency.setVersion(GROOVY_VERSION);
        groovyDependency.setClassifier("indy");
        groovyDependency.setType("jar");
        List<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.add(groovyDependency);
        Mockito.when(testMojo.project.getCompileDependencies()).thenReturn(dependencies);

        Assert.assertTrue(testMojo.isGroovyIndy());
        Assert.assertEquals(GROOVY_VERSION, testMojo.getGroovyVersionString());
    }

    @Test
    public void testGetJavaVersion() {
        Assert.assertTrue(testMojo.getJavaVersionString() != null && !testMojo.getJavaVersionString().isEmpty());
        Assert.assertNotNull(testMojo.getJavaVersion());
    }

    @Test
    public void testIsJavaSupportIndy() {
        Mockito.when(testMojo.getJavaVersion()).thenReturn(Version.parseFromString("1.7.0_45"));
        Assert.assertTrue(testMojo.isJavaSupportIndy());
    }

    @Test
    public void testIsJavaSupportIndyNo() {
        Mockito.when(testMojo.getJavaVersion()).thenReturn(Version.parseFromString("1.6.0_45"));
        Assert.assertFalse(testMojo.isJavaSupportIndy());
    }

    private static class TestGroovyMojo extends AbstractGroovyMojo {
        public void execute() throws MojoExecutionException, MojoFailureException { }
    }

}
