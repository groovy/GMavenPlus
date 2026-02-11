package org.codehaus.gmavenplus.util;

import java.security.Permission;


/**
 * Custom security manager to {@link System#exit} (and related) from being used.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NoExitSecurityManager extends SecurityManager {

    /**
     * The parent SecurityManager.
     */
    private final SecurityManager parent;

    /**
     * Construct a new NoExitSecurityManager from the parent.
     *
     * @param newParent the parent to set
     */
    public NoExitSecurityManager(final SecurityManager newParent) {
        parent = newParent;
    }

    /**
     * Construct a new NoExitSecurityManager, using the System SecurityManager as the parent.
     */
    public NoExitSecurityManager() {
        this(System.getSecurityManager());
    }

    /**
     * Check the given Permission.
     *
     * @param permission the Permission to check
     */
    public void checkPermission(final Permission permission) {
        if (parent != null) {
            parent.checkPermission(permission);
        }
    }

    /**
     * Always throws {@link SecurityException}.
     *
     * @param code the exit code that is completely ignored
     */
    public void checkExit(final int code) {
        throw new SecurityException("Use of System.exit() is forbidden!");
    }

}
