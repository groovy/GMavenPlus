/*
 * Copyright 2014 Keegan Witt
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
    private final SecurityManager parent;

    public NoExitSecurityManager(final SecurityManager parent) {
        assert parent != null;

        this.parent = parent;
    }

    public NoExitSecurityManager() {
        this(System.getSecurityManager());
    }

    public void checkPermission(final Permission perm) {
        if (parent != null) {
            parent.checkPermission(perm);
        }
    }

    /**
     * Always throws {@link SecurityException}.
     */
    public void checkExit(final int code) {
        throw new SecurityException("Use of System.exit() is forbidden!");
    }
}
