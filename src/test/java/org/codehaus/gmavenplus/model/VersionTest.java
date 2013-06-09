package org.codehaus.gmavenplus.model;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author wittk
 * @version $Rev$ $Date$
 */
public class VersionTest {

    @Test
    public void testCompare() {
        Assert.assertTrue(new Version(1, 9).compareTo(new Version(1, 10)) < 0);
    }

    @Test
    public void testCompareWithTag() {
        Version v1 = Version.parseFromString("1.0.0");
        Version v2 = Version.parseFromString("1.0.0-beta1");
        Assert.assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testCompareWithTwoTags() {
        Version v1 = Version.parseFromString("1.0.0-beta1");
        Version v2 = Version.parseFromString("1.0.0-beta2");
        Assert.assertTrue(v1.compareTo(v2) > 0);
    }

}
