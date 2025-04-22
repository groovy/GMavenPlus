/*
 * Copyright (C) 2012 the original author or authors.
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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * This mojo removes Groovy stubs from the project's sources.
 *
 * @author Keegan Witt
 * @since 1.0-beta-3
 */
@Mojo(name = "removeStubs", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class RemoveStubsMojo extends AbstractGroovyStubSourcesMojo {

    /**
     * The location for the compiled classes.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/groovy-stubs/main")
    protected File stubsOutputDirectory;

     /**
     * Executes this mojo.
     */
    @Override
    public void execute() {
        try {
            project.getCompileSourceRoots().remove(stubsOutputDirectory.getAbsolutePath());
        } catch (UnsupportedOperationException e) {
            String scopeToRemove = "main";
            try {
                RemoveStubsMojo.removeSourceRoot(project, scopeToRemove, stubsOutputDirectory);
            } catch (Throwable e2) {
                e.addSuppressed(e2);
                throw e;
            }
        }
    }

    static void removeSourceRoot(MavenProject project, String scopeToRemove, File file) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
        Class<?> sourceRoot = project.getClass().getClassLoader().loadClass("org.apache.maven.api.SourceRoot");
        Path path = project.getBasedir().toPath().resolve(file.getAbsolutePath()).normalize();
        Field field = project.getClass().getDeclaredField("sources");
        field.setAccessible(true);
        Method scope = sourceRoot.getMethod("scope");
        Method language = sourceRoot.getMethod("language");
        Method directory = sourceRoot.getMethod("directory");
        Method id = project.getClass().getClassLoader().loadClass("org.apache.maven.api.ExtensibleEnum").getMethod("id");
        Collection<?> sources = (Collection) field.get(project);
        sources.removeIf(source -> {
            try {
                return Objects.equals(id.invoke(scope.invoke(source)), scopeToRemove)
                        && Objects.equals(id.invoke(language.invoke(source)), "java")
                        && Objects.equals(directory.invoke(source), path);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
