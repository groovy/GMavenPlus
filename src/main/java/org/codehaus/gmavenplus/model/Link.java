// note that this won't be properly consumed by mojo unless it's in the same package as the mojo
package org.codehaus.gmavenplus.model;


/**
 * This class was taken from the Groovy project, so that GroovyDoc links can be
 * added as mojo parameters without a compile dependency on Groovy. Represents a link pair (href, packages).
 * The packages are comma separated.
 */
public class Link {

    /**
     * Link URL.
     */
    private String href = "";

    /**
     * Link packages.
     */
    private String packages = "";

    /**
     * Get the packages attribute.
     *
     * @return the packages attribute
     */
    public String getPackages() {
        return packages;
    }

    /**
     * Set the packages attribute.
     *
     * @param newPackages the comma separated package prefixes corresponding to this link
     */
    public void setPackages(final String newPackages) {
        packages = newPackages;
    }

    /**
     * Get the href attribute.
     *
     * @return the href attribute
     */
    public String getHref() {
        return href;
    }

    /**
     * Set the href attribute.
     *
     * @param newHref a <code>String</code> value representing the URL to use for this link
     */
    public void setHref(final String newHref) {
        href = newHref;
    }

}
