/*
 * Copyright 2003-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// note that this won't be properly consumed by mojo unless it's in the same package as the mojo
package org.codehaus.gmavenplus.mojo;


/**
 * This class was taken from the Groovy project, so that Groovydoc links can be
 * added as mojo parameters without a compile dependency on Groovy.
 *
 * Represents a link pair (href, packages).
 * The packages are comma separated.
 */
public class Link {
    /** Link URL. */
    private String href = "";
    /** Link packages. */
    private String packages = "";

    /**
     * Get the packages attribute.
     *
     * @return the packages attribute.
     */
    public String getPackages() {
        return packages;
    }

    /**
     * Set the packages attribute.
     *
     * @param newPackages the comma separated package prefixs corresponding to this link
     * @return this object (for fluent invocation)
     */
    public Link setPackages(final String newPackages) {
        packages = newPackages;
        return this;
    }

    /**
     * Get the href attribute.
     *
     * @return the href attribute.
     */
    public String getHref() {
        return href;
    }

    /**
     * Set the href attribute.
     *
     * @param newHref a <code>String</code> value representing the URL to use for this link
     * @return this object (for fluent invocation)
     */
    public Link setHref(final String newHref) {
        href = newHref;
        return this;
    }

}
