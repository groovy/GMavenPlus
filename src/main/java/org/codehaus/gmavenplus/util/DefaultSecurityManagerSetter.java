package org.codehaus.gmavenplus.util;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.maven.plugin.logging.Log;

public class DefaultSecurityManagerSetter extends AbstractSecurityManagerSetter {

    private final AtomicReference<SecurityManager> previousSecurityManager = new AtomicReference<>();

    public DefaultSecurityManagerSetter(Log log, final boolean allowSystemExits) {
        super(log, allowSystemExits);
    }

    @Override
    public void setNoExitSecurityManager() {
        if (this.getAllowSystemExits()) {
            return;
        }

        this.previousSecurityManager.set(System.getSecurityManager());

        getLog().warn("Setting a security manager is deprecated. Running this build with Java 21 or newer might result in different behaviour.");
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @Override
    public void revertToPreviousSecurityManager() {
        if (this.getAllowSystemExits()) {
            return;
        }

        this.getPreviousSecurityManager().getAndUpdate((previousSecurityManager -> {
            if (previousSecurityManager == null) {
                return null;
            }

            System.setSecurityManager(previousSecurityManager);

            return null;
        }));
    }

    protected AtomicReference<SecurityManager> getPreviousSecurityManager() {
        return previousSecurityManager;
    }
}
