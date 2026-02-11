package org.codehaus.gmavenplus.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Unit tests for the Link class
 */
public class LinkTest {

    @Test
    public void testGettersAndSetters() {
        String packages = "PACKAGES";
        String href = "HREF";
        Link link = new Link();
        assertEquals(0, link.getPackages().length());
        assertEquals(0, link.getHref().length());
        link.setPackages(packages);
        link.setHref(href);
        assertEquals(packages, link.getPackages());
        assertEquals(href, link.getHref());
    }

}
