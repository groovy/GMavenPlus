package gmavenplus.mojo;

import gmavenplus.util.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import org.apache.maven.plugin.AbstractMojo;


/**
 * @author Keegan Witt
 * @version $Rev$ $Date$
 *
 * @requiresDependencyResolution compile
 * @configurator include-project-dependencies
 */
public abstract class AbstractGroovyMojo  extends AbstractMojo {

    protected void logGroovyVersion(String goal) {
        if (getLog().isInfoEnabled()) {
            getLog().info("Using Groovy " + getGroovyVersion() + " from project compile classpath to perform " + goal);
        }
    }

    protected String getGroovyVersion() {
        String groovyVersion = null;

        try {
            Class InvokerHelperClass = Class.forName("org.codehaus.groovy.runtime.InvokerHelper");
            groovyVersion = (String) ReflectionUtils.invokeStaticMethod(ReflectionUtils.findMethod(InvokerHelperClass, "getVersion"));
        } catch (ClassNotFoundException e) {
            getLog().warn("Unable to log Groovy Version", e);
        } catch (IllegalAccessException e) {
            getLog().warn("Unable to log Groovy Version", e);
        } catch (InvocationTargetException e) {
            getLog().warn("Unable to log Groovy Version", e);
        }

        return groovyVersion;
    }

}
