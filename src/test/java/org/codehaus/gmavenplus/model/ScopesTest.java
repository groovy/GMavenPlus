package org.codehaus.gmavenplus.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Unit tests for Scopes class.
 *
 * @author Keegan Witt
 */
public class ScopesTest {

    @Test
    public void test() {
        assertEquals(4, Scopes.values().length);
        assertEquals(Scopes.PUBLIC, Scopes.values()[0]);
        assertEquals(Scopes.PROTECTED, Scopes.values()[1]);
        assertEquals(Scopes.PACKAGE, Scopes.values()[2]);
        assertEquals(Scopes.PRIVATE, Scopes.values()[3]);
    }

}
