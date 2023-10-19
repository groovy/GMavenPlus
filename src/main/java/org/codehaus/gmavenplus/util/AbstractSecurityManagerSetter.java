package org.codehaus.gmavenplus.util;

import org.apache.maven.plugin.logging.Log;

public abstract class AbstractSecurityManagerSetter implements SecurityManagerSetter{

    private final boolean allowSystemExits;

    private final Log log;

    public AbstractSecurityManagerSetter(final Log log, final boolean allowSystemExits) {
        this.log = log;
        this.allowSystemExits = allowSystemExits;
    }

    protected boolean getAllowSystemExits() {
        return allowSystemExits;
    }

    protected Log getLog() {
        return log;
    }
}
