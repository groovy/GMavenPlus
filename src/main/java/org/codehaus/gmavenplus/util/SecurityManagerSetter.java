package org.codehaus.gmavenplus.util;

/**
 * Sets a custom security manager and returns the previous instance.
 * Can also revert to the previous security Manager.
 * <p>
 * Implementation notice: For Java 21 and above,
 * the implementation must be a no-op and issue a warning.
 *
 */
public interface SecurityManagerSetter {

    /**
     * For Java until 20, this method should set the given Security manager if property {@code allowSystemExits} is set.
     */
    void setNoExitSecurityManager();

    void revertToPreviousSecurityManager();
}
