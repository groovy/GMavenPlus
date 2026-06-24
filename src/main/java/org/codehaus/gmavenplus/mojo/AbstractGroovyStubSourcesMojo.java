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
 */
public abstract class AbstractGroovyStubSourcesMojo extends AbstractGroovySourcesMojo {

    /**
     * Removes the source roots from the project, using reflection to avoid breaking changes in Maven 4.
     *
     * @param project the Maven project
     * @param sourceRootScope the source root scope to remove
     * @param sourceDirectory the source directory to remove
     * @throws ClassNotFoundException when a class needed cannot be found
     * @throws NoSuchFieldException when a field needed cannot be found
     * @throws NoSuchMethodException when a method needed cannot be found
     * @throws IllegalAccessException when a method needed cannot be accessed
     */
    protected static void removeSourceRoot(MavenProject project, SourceRootScope sourceRootScope, File sourceDirectory)
            throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
        try {
            project.getClass().getMethod(sourceRootScope.getRemovalMethod(), String.class)
                    .invoke(project, sourceDirectory.getAbsolutePath());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (NoSuchMethodException e) {
            try {
                removeMaven4SourceRoot(project, sourceRootScope, sourceDirectory);
            } catch (ClassNotFoundException e2) {
                removeMaven3SourceRoot(project, sourceRootScope, sourceDirectory);
            }
        }
    }

    protected static void removeMaven4SourceRoot(MavenProject project, SourceRootScope sourceRootScope, File sourceDirectory)
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
                return Objects.equals(id.invoke(scope.invoke(source)), sourceRootScope.getDirectoryName())
                        && Objects.equals(id.invoke(language.invoke(source)), "java")
                        && Objects.equals(directory.invoke(source), path);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    protected static void removeMaven3SourceRoot(MavenProject project, SourceRootScope sourceRootScope, File sourceDirectory) {
        if (sourceRootScope == SourceRootScope.MAIN) {
            project.getCompileSourceRoots().remove(sourceDirectory.getAbsolutePath());
        } else {
            project.getTestCompileSourceRoots().remove(sourceDirectory.getAbsolutePath());
        }
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
