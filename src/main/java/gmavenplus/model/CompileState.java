/*
 * Copyright (C) 2006-2007 the original author or authors.
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

package gmavenplus.model;

import org.apache.maven.project.MavenProject;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Support for communication between stub generation and compilation
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class CompileState {
    private Map forceCompile = new HashMap();
    private Map forceCompileTest = new HashMap();

    public synchronized void addForcedCompilationSource(final MavenProject project, final File file) {
        String projectKey = projectKey(project);

        if (!forceCompile.containsKey(projectKey)) {
            forceCompile.put(projectKey, new TreeSet());
        }

        ((Set) forceCompile.get(projectKey)).add(file);
    }

    public synchronized Set getForcedCompilationSources(final MavenProject project) {
        String projectKey = projectKey(project);

        if (forceCompile.containsKey(projectKey)) {
            Set files = new TreeSet();

            for (Object o : ((Set) forceCompile.get(projectKey))) {
                File file = (File) o;

                if (file.isFile()) {
                    files.add(file);
                }
            }
            return Collections.unmodifiableSet(files);
        } else {
            return Collections.unmodifiableSet(new TreeSet());
        }
    }

    public synchronized void addForcedCompilationTestSource(final MavenProject project, final File file) {
        String projectKey = projectKey(project);

        if (!forceCompileTest.containsKey(projectKey)) {
            forceCompileTest.put(projectKey, new TreeSet());
        }

        ((Set) forceCompileTest.get(projectKey)).add(file);
    }

    public synchronized Set getForcedCompilationTestSources(final MavenProject project) {
        String projectKey = projectKey(project);

        if (forceCompileTest.containsKey(projectKey)) {
            Set files = new TreeSet();
            for (Object o : ((Set) forceCompileTest.get(projectKey))) {
                File file = (File) o;
                if (file.isFile()) {
                    files.add(file);
                }
            }
            return Collections.unmodifiableSet(files);
        } else {
            return Collections.unmodifiableSet(new TreeSet());
        }
    }

    private String projectKey(final MavenProject project) {
        if (project == null) {
            throw new IllegalArgumentException("Project must not be null.");
        }

        if (project.getBasedir() == null || !project.getBasedir().isDirectory()) {
            throw new IllegalStateException("Project " + project.getId() + " does not define a base directory: " + project.getBasedir() + ".");
        }

        try {
            return project.getBasedir().getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
