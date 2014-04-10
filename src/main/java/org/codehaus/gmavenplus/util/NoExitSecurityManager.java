/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus.util;

import java.security.Permission;


/**
 * Custom security manager to {@link System#exit} (and related) from being used.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 1.2
 */
public class NoExitSecurityManager extends SecurityManager {
    /** The parent SecurityManager. **/
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
     * Construct a new NoExitSecurityManager, using the System SecurityManager
     * as the parent.
     */
    public NoExitSecurityManager() {
        this(System.getSecurityManager());
    }

    /**
     * Check the given Permission.
     *
     * @param permission the Permission to check.
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
