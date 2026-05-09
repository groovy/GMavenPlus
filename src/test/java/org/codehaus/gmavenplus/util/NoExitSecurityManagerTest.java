package org.codehaus.gmavenplus.util;

import org.junit.Test;
import java.security.Permission;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Unit tests for the NoExitSecurityManager class.
 *
 * @author Keegan Witt
 */
public class NoExitSecurityManagerTest {

    @Test
    public void testConstructorWithParent() {
        SecurityManager parent = new SecurityManager();
        NoExitSecurityManager manager = new NoExitSecurityManager(parent);
        // Verification of parent is indirect via checkPermission delegation
    }

    @Test
    public void testDefaultConstructor() {
        new NoExitSecurityManager();
    }

    @Test
    public void testCheckPermissionDelegates() {
        StubSecurityManager parent = new StubSecurityManager();
        NoExitSecurityManager manager = new NoExitSecurityManager(parent);
        Permission perm = new java.security.AllPermission();
        manager.checkPermission(perm);
        assertTrue("Expected checkPermission to be called on parent", parent.checkPermissionCalled);
        assertEquals("Expected correct permission to be passed to parent", perm, parent.passedPermission);
    }

    @Test
    public void testCheckPermissionWithNullParent() {
        NoExitSecurityManager manager = new NoExitSecurityManager(null);
        manager.checkPermission(new java.security.AllPermission());
        // Should not throw NullPointerException
    }

    @Test(expected = SecurityException.class)
    public void testCheckExitThrowsException() {
        NoExitSecurityManager manager = new NoExitSecurityManager();
        manager.checkExit(1);
    }

    private static class StubSecurityManager extends SecurityManager {
        boolean checkPermissionCalled = false;
        Permission passedPermission = null;

        @Override
        public void checkPermission(Permission perm) {
            checkPermissionCalled = true;
            passedPermission = perm;
        }
    }

}
