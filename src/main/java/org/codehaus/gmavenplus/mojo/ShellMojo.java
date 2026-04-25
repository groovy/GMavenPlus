package org.codehaus.gmavenplus.mojo;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.gmavenplus.model.internal.Version;
import org.codehaus.gmavenplus.util.NoExitSecurityManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static org.codehaus.gmavenplus.util.ReflectionUtils.findConstructor;
import static org.codehaus.gmavenplus.util.ReflectionUtils.findField;
import static org.codehaus.gmavenplus.util.ReflectionUtils.findMethod;
import static org.codehaus.gmavenplus.util.ReflectionUtils.invokeConstructor;
import static org.codehaus.gmavenplus.util.ReflectionUtils.invokeMethod;
import static org.codehaus.gmavenplus.util.ReflectionUtils.invokeStaticMethod;


/**
 * Launches a Groovy shell bound to the current project.
 * Note that this mojo requires Groovy &gt;= 1.5.0.
 * Note that it references the plugin ClassLoader to pull in dependencies
 * Groovy didn't include (for things like Ant for AntBuilder, Ivy for @grab, and Jansi for Groovysh).
 * These dependencies are now optional and must be provided by the user if needed.
 *
 * @author Keegan Witt
 * @since 1.1
 */
@Mojo(name = "shell", requiresDependencyResolution = ResolutionScope.TEST)
public class ShellMojo extends AbstractToolsMojo {

    /**
     * Groovy 2.2.0 beta-1 version.
     */
    protected static final Version GROOVY_2_2_0_BETA1 = new Version(2, 2, 0, "beta-1");

    /**
     * Groovy 4.0.0 alpha-1 version.
     */
    protected static final Version GROOVY_4_0_0_ALPHA1 = new Version(4, 0, 0, "alpha-1");

     /**
      * Groovy 4.0.0 RC-1 version.
      */
     protected static final Version GROOVY_4_0_0_RC1 = new Version(4, 0, 0, "RC-1");

    /**
     * Groovy 5.0.0-alpha-1 version.
     */
    protected static final Version GROOVY_5_0_0_ALPHA1 = new Version(5, 0, 0, "alpha-1");

    /**
     * Groovy 5.0.0-beta-2 version.
     */
    protected static final Version GROOVY_5_0_0_BETA2 = new Version(5, 0, 0, "beta-2");

    /**
      * Groovy 5.0.4 version.
      */
     protected static final Version GROOVY_5_0_4 = new Version(5, 0, 4);

    /**
     * Groovy shell verbosity level. Should be one of:
     * <ul>
     *   <li>QUIET</li>
     *   <li>INFO</li>
     *   <li>DEBUG</li>
     *   <li>VERBOSE</li>
     * </ul>
     */
    @Parameter(defaultValue = "QUIET")
    protected String verbosity;

    /**
     * Executes this mojo.
     *
     * @throws MojoExecutionException If an unexpected problem occurs (causes a "BUILD ERROR" message to be displayed)
     */
    @Override
    public void execute() throws MojoExecutionException {
        try {
            setupClassWrangler(project.getTestClasspathElements(), includeClasspath);
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to add project test dependencies to classpath.", e);
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Test dependencies weren't resolved.", e);
        }

        logPluginClasspath();
        classWrangler.logGroovyVersion(mojoExecution.getMojoDescriptor().getGoal());

        try {
            getLog().debug("Project test classpath:\n" + project.getTestClasspathElements());
        } catch (DependencyResolutionRequiredException e) {
            getLog().debug("Unable to log project test classpath");
        }

        if (!groovyVersionSupportsAction()) {
            getLog().error("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support running a shell. The minimum version of Groovy required is " + minGroovyVersion + ". Skipping shell startup.");
            return;
        }

        final SecurityManager defaultSecurityManager = System.getSecurityManager();
        try {
            if (!allowSystemExits) {
                getLog().warn("JEP 411 deprecated Security Manager in Java 17 for removal. Therefore `allowSystemExits` is also deprecated for removal.");
                try {
                    System.setSecurityManager(new NoExitSecurityManager());
                } catch (UnsupportedOperationException e) {
                    getLog().warn("Attempted to use Security Manager in a JVM where it's disabled by default. You might try `-Djava.security.manager=allow` to override this.");
                }
            }

            if (groovyAtLeast(GROOVY_5_0_4)) {
                executeGroovy5Shell();
            } else {
                if (groovyAtLeast(GROOVY_5_0_0_ALPHA1)) {
                    try {
                        classWrangler.getClass("org.jline.reader.LineReader");
                    } catch (ClassNotFoundException e) {
                        getLog().warn("JLine 3 not found on classpath. Terminal colors and advanced features will be disabled. To enable them, add org.jline:jline-terminal-jna:3 and net.java.dev.jna:jna:5 to the plugin's dependencies.");
                    }
                } else {
                    try {
                        if (groovyAtLeast(GROOVY_2_2_0_BETA1)) {
                            classWrangler.getClass("jline.Terminal");
                        } else {
                            classWrangler.getClass("jline.Terminal");
                        }
                    } catch (ClassNotFoundException e) {
                        getLog().warn("JLine 2 not found on classpath. Terminal colors and advanced features will be disabled. To enable them, add jline:jline:2 to the plugin's dependencies.");
                    }
                }
                // get classes we need with reflection
                Class<?> shellClass = classWrangler.getClass(groovyAtLeast(GROOVY_4_0_0_ALPHA1) ? "org.apache.groovy.groovysh.Groovysh" : "org.codehaus.groovy.tools.shell.Groovysh");
                Class<?> bindingClass = classWrangler.getClass("groovy.lang.Binding");
                Class<?> ioClass = classWrangler.getClass("org.codehaus.groovy.tools.shell.IO");
                Class<?> verbosityClass = classWrangler.getClass("org.codehaus.groovy.tools.shell.IO$Verbosity");
                Class<?> loggerClass = classWrangler.getClass("org.codehaus.groovy.tools.shell.util.Logger");

                // create shell to run
                Object shell = setupShell(shellClass, bindingClass, ioClass, verbosityClass, loggerClass);

                // run the shell
                if (groovyOlderThan(GROOVY_5_0_4)) {
                    try {
                        Class<?> ansiConsoleClass = classWrangler.getClass("org.fusesource.jansi.AnsiConsole");
                        ansiConsoleClass.getMethod("wrapOutputStream", java.io.OutputStream.class);
                    } catch (Exception e) {
                        String message = "Jansi 2.x detected, which is incompatible with JLine 2. Falling back to dumb terminal. Colors will be disabled.";
                        if (groovyAtLeast(GROOVY_5_0_0_ALPHA1)) {
                            message += " We recommend upgrading to Groovy 5.0.4 or newer for full JLine 3 support.";
                        } else if (groovyOlderThan(GROOVY_5_0_0_ALPHA1)) {
                            try {
                                classWrangler.getClass("org.fusesource.jansi.AnsiConsole");
                            } catch (ClassNotFoundException cnfe) {
                                message = "Jansi 1 not found on classpath. Terminal colors will be disabled. To enable them, add org.fusesource.jansi:jansi:1 to the plugin's dependencies.";
                            }
                        }
                        getLog().warn(message);
                        System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
                    }
                }
                invokeMethod(findMethod(shellClass, "run", String.class), shell, (String) null);
            }
        } catch (ClassNotFoundException e) {
            if (groovyAtLeast(GROOVY_5_0_0_ALPHA1)) {
                throw new MojoExecutionException("Unable to get a Groovy class from classpath (" + e.getMessage() + "). This Groovy version (" + classWrangler.getGroovyVersionString() + ") revamped the shell to use JLine 3 and removed some older classes. You might need to use Groovy 5.0.4 or later for full support.", e);
            }
            throw new MojoExecutionException("Unable to get a Groovy class from classpath (" + e.getMessage() + "). Do you have Groovy as a compile dependency in your project or the plugin?", e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof NoClassDefFoundError && e.getCause().getMessage() != null && e.getCause().getMessage().contains("jline")) {
                if (groovyAtLeast(GROOVY_5_0_0_ALPHA1)) {
                    throw new MojoExecutionException("Unable to get a JLine 3 class from classpath. This might be because of a JLine version mismatch. Make sure you include JLine 3.x as a runtime dependency in your project or the plugin.", e);
                } else if (groovyAtLeast(GROOVY_2_2_0_BETA1)) {
                    throw new MojoExecutionException("Unable to get a JLine 2 class from classpath. This might be because of a JLine version mismatch. Make sure you include JLine 2.x as a runtime dependency in your project or the plugin.", e);
                } else {
                    throw new MojoExecutionException("Unable to get a JLine 1 class from classpath. This might be because of a JLine version mismatch. Make sure you include JLine 1.x as a runtime dependency in your project or the plugin.", e);
                }
            } else {
                throw new MojoExecutionException("Error occurred while calling a method on a Groovy class from classpath.", e);
            }
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Unable to access a method on a Groovy class from classpath.", e);
        } catch (InstantiationException e) {
            throw new MojoExecutionException("Error occurred while instantiating a Groovy class from classpath.", e);
        } finally {
            if (!allowSystemExits) {
                try {
                    System.setSecurityManager(defaultSecurityManager);
                } catch (UnsupportedOperationException e) {
                    getLog().warn("Attempted to use Security Manager in a JVM where it's disabled by default. You might try `-Djava.security.manager=allow` to override this.");
                }
            }
        }
    }

    /**
     * Creates the Groovysh to run.
     *
     * @param shellClass     the Groovysh class
     * @param bindingClass   the Binding class
     * @param ioClass        the IO class
     * @param verbosityClass the Verbosity
     * @param loggerClass    the Logger class
     * @return the Groovysh shell to run
     * @throws InstantiationException    when a class needed for setting up a shell cannot be instantiated
     * @throws IllegalAccessException    when a method needed for setting up a shell cannot be accessed
     * @throws InvocationTargetException when a reflection invocation needed for setting up a shell cannot be completed
     */
    protected Object setupShell(final Class<?> shellClass, final Class<?> bindingClass, final Class<?> ioClass, final Class<?> verbosityClass, final Class<?> loggerClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object binding = invokeConstructor(findConstructor(bindingClass));
        initializeProperties();
        Method setVariable = findMethod(bindingClass, "setVariable", String.class, Object.class);
        if (bindPropertiesToSeparateVariables) {
            for (Object k : properties.keySet()) {
                invokeMethod(setVariable, binding, k, properties.get(k));
            }
        } else {
            if (groovyOlderThan(GROOVY_4_0_0_RC1)) {
                invokeMethod(setVariable, binding, "properties", properties);
            } else {
                throw new IllegalArgumentException("properties is a read-only property in Groovy " + GROOVY_4_0_0_RC1 + " and later.");
            }
        }
        Object io = invokeConstructor(findConstructor(ioClass));
        invokeMethod(findMethod(ioClass, "setVerbosity", verbosityClass), io, invokeStaticMethod(findMethod(verbosityClass, "forName", String.class), verbosity));
        findField(loggerClass, "io", ioClass).set(null, io);

        return invokeConstructor(findConstructor(shellClass, ClassLoader.class, bindingClass, ioClass), classWrangler.getClassLoader(), binding, io);
    }
    /**
     * Executes the Groovy 5 shell.
     *
     * @throws MojoExecutionException when a problem occurs during shell execution
     * @throws ClassNotFoundException when a class needed for shell configuration cannot be found
     * @throws InvocationTargetException when a reflection invocation needed for shell configuration cannot be completed
     * @throws IllegalAccessException    when a method needed for shell configuration cannot be accessed
     */
    protected void executeGroovy5Shell() throws MojoExecutionException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        try {
            classWrangler.getClass("org.jline.reader.LineReader");
        } catch (ClassNotFoundException e) {
            getLog().warn("JLine 3 not found on classpath. Terminal colors and advanced features will be disabled. To enable them, add org.jline:jline-terminal-jna:3 and net.java.dev.jna:jna:5 to the plugin's dependencies.");
        }
        if (!bindPropertiesToSeparateVariables) {
            throw new IllegalArgumentException("properties is a read-only property in Groovy " + GROOVY_4_0_0_RC1 + " and later.");
        }
        Class<?> mainClass = classWrangler.getClass("org.apache.groovy.groovysh.Main");
        Method startMethod = null;
        try {
            startMethod = findMethod(mainClass, "start", Map.class, String[].class);
        } catch (IllegalArgumentException e) {
            // Method not found
        }

        if (startMethod != null) {
            Map<String, Object> initialBindings = new HashMap<>();
            initializeProperties();
            for (Object k : properties.keySet()) {
                initialBindings.put((String) k, properties.get(k));
            }

            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(classWrangler.getClassLoader());
                invokeStaticMethod(startMethod, initialBindings, new String[0]);
            } finally {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        } else {
            throw new MojoExecutionException("Your Groovy version (" + classWrangler.getGroovyVersionString() + ") doesn't support running a shell. The minimum version of Groovy required is " + GROOVY_5_0_4 + ". Skipping shell startup.");
        }
    }

}
