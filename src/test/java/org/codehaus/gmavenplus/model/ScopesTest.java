package org.codehaus.gmavenplus.model;

import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for Scopes class.
 *
 * @author Keegan Witt
 */
public class ScopesTest {

    @Test
    public void test() {
        Assert.assertEquals(4, Scopes.values().length);
        Assert.assertEquals(Scopes.PUBLIC, Scopes.values()[0]);
        Assert.assertEquals(Scopes.PROTECTED, Scopes.values()[1]);
        Assert.assertEquals(Scopes.PACKAGE, Scopes.values()[2]);
        Assert.assertEquals(Scopes.PRIVATE, Scopes.values()[3]);
    }

}
