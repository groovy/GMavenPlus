package org.codehaus.gmavenplus.util;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.gmavenplus.model.GroovyCompileConfiguration;
import org.codehaus.gmavenplus.model.GroovyDocConfiguration;
import org.codehaus.gmavenplus.model.GroovyStubConfiguration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Main class to be executed in a forked process for Groovy compilation.
 *
 * @author Keegan Witt
 */
public class ForkedGroovyCompiler {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java " + ForkedGroovyCompiler.class.getName() + " <configuration-file>");
            System.exit(1);
        }

        String configFilePath = args[0];
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(configFilePath)))) {
            Object configuration = ois.readObject();
            Log log = new SystemStreamLog();

            List<?> classpath = Collections.emptyList();
            org.codehaus.gmavenplus.model.IncludeClasspath includeClasspath = null;

            if (configuration instanceof GroovyCompileConfiguration) {
                classpath = ((GroovyCompileConfiguration) configuration).getClasspath();
                includeClasspath = ((GroovyCompileConfiguration) configuration).getIncludeClasspath();
            } else if (configuration instanceof GroovyStubConfiguration) {
                classpath = ((GroovyStubConfiguration) configuration).getClasspath();
                includeClasspath = ((GroovyStubConfiguration) configuration).getIncludeClasspath();
            } else if (configuration instanceof GroovyDocConfiguration) {
                classpath = ((GroovyDocConfiguration) configuration).getClasspath();
                includeClasspath = ((GroovyDocConfiguration) configuration).getIncludeClasspath();
            }

            ClassLoader parent = ClassLoader.getSystemClassLoader();
            if (includeClasspath != null && includeClasspath.name().equals("PROJECT_ONLY")) {
                try {
                    java.lang.reflect.Method getPlatformClassLoader = ClassLoader.class.getMethod("getPlatformClassLoader");
                    parent = (ClassLoader) getPlatformClassLoader.invoke(null);
                } catch (Exception e) {
                    parent = null;
                }
            }

            List<?> finalClasspath = classpath != null ? classpath : Collections.emptyList();
            if (includeClasspath != null && includeClasspath.name().equals("PLUGIN_ONLY")) {
                finalClasspath = Collections.emptyList();
            }

            ClassWrangler classWrangler = new ClassWrangler(finalClasspath, parent, log);
            GroovyCompiler compiler = new GroovyCompiler(classWrangler, log);

            if (configuration instanceof GroovyCompileConfiguration) {
                compiler.compile((GroovyCompileConfiguration) configuration);
            } else if (configuration instanceof GroovyStubConfiguration) {
                compiler.generateStubs((GroovyStubConfiguration) configuration);
            } else if (configuration instanceof GroovyDocConfiguration) {
                compiler.generateGroovyDoc((GroovyDocConfiguration) configuration);
            } else {
                throw new IllegalArgumentException("Unknown configuration type: " + configuration.getClass().getName());
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
