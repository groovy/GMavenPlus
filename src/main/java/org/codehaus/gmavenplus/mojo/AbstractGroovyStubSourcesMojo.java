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

package org.codehaus.gmavenplus.mojo;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.singletonList;


/**
 * This mojo provides access to the Groovy sources (including stubs).
 *
 * @author Keegan Witt
 * @since 1.0-beta-3
 */
public abstract class AbstractGroovyStubSourcesMojo extends AbstractGroovySourcesMojo {

    /**
     * Removes the source roots from the project, using reflection to avoid breaking changes in Maven 4.
     *
     * @param project the Maven project
     * @param scopeToRemove the scope to remove (main or test)
     * @param sourceDirectory the source directory to remove
     * @throws ClassNotFoundException when a class needed cannot be found
     * @throws NoSuchFieldException when a field needed cannot be found
     * @throws NoSuchMethodException when a method needed cannot be found
     * @throws IllegalAccessException when a method needed cannot be accessed
     */
    protected static void removeSourceRoot(MavenProject project, String scopeToRemove, File sourceDirectory)
            throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
        Class<?> sourceRoot = project.getClass().getClassLoader().loadClass("org.apache.maven.api.SourceRoot");
        Path path = project.getBasedir().toPath().resolve(sourceDirectory.getAbsolutePath()).normalize();
        Field field = project.getClass().getDeclaredField("sources");
        field.setAccessible(true);
        Method scope = sourceRoot.getMethod("scope");
        Method language = sourceRoot.getMethod("language");
        Method directory = sourceRoot.getMethod("directory");
        Method id = project.getClass().getClassLoader().loadClass("org.apache.maven.api.ExtensibleEnum").getMethod("id");
        Collection<?> sources = (Collection<?>) field.get(project);
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

    /**
     * Gets the set of stub files in specified directory.
     *
     * @param outputDirectory the directory to write stubs to
     * @return The set of stub files in specified directory
     */
    protected Set<File> getStubs(File outputDirectory) {
        Set<File> files = new HashSet<>();
        FileSetManager fileSetManager = new FileSetManager();

        FileSet fileSet = new FileSet();
        fileSet.setDirectory(outputDirectory.getAbsolutePath());
        fileSet.setIncludes(singletonList(JAVA_SOURCES_PATTERN));
        for (String file : fileSetManager.getIncludedFiles(fileSet)) {
            files.add(new File(outputDirectory, file));
        }

        return files;
    }

}
