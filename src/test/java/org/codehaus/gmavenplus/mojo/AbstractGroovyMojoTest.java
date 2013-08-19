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

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;


/**
 * Unit tests for the AbstractGroovyMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGroovyMojoTest {
    private static final String GROOVY_VERSION = "2.0.5";

    @Test
    public void testIsGroovyIndy() {
        AbstractGroovyMojo testMojo = new TestGroovyMojo();
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
        AbstractGroovyMojo testMojo = new TestGroovyMojo();
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

    private static class TestGroovyMojo extends AbstractGroovyMojo {
        public void execute() throws MojoExecutionException, MojoFailureException { }
    }

}
