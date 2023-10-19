package org.codehaus.gmavenplus.util;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.maven.plugin.logging.Log;

public class DefaultSecurityManagerSetter extends AbstractSecurityManagerSetter {

    public DefaultSecurityManagerSetter(Log log, final boolean allowSystemExits) {
        super(log, allowSystemExits);
    }

    @Override
    public void setNoExitSecurityManager() {
        if (this.getAllowSystemExits()) {
            return;
        }

        getLog().warn("Setting a security manager is not supported starting with Java 21.");
    }

    @Override
    public void revertToPreviousSecurityManager() {
        if (this.getAllowSystemExits()) {
            return;
        }

        getLog().warn("Setting a security manager is not supported starting with Java 21.");

    }
}
