package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public class FailClosure {

    static final FailClosure INSTANCE = new FailClosure();

    public void call(Object[] args) throws MojoExecutionException, MojoFailureException {
        if (args == null || args.length == 0) {
            throw new MojoExecutionException("Failed");
        } else if (args.length == 1) {
            if (args[0] instanceof Throwable) {
                throw new MojoExecutionException("Failed", (Throwable) args[0]);
            } else {
                throw new MojoExecutionException(String.valueOf(args[0]));
            }
        } else if (args.length == 2 && args[1] instanceof Throwable) {
            throw new MojoExecutionException(String.valueOf(args[0]), (Throwable) args[1]);
        }

        throw new MojoFailureException("Incorrect fail() usage. Valid usages are: fail(), fail(Throwable), fail(Object, Throwable)");
    }
}
