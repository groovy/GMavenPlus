package org.codehaus.gmavenplus.model;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
        Assert.assertTrue(v1.compareTo(v2) > 0);
    }

    @Test
    public void testCompareWithTwoTags() {
        Version v1 = Version.parseFromString("1.0.0-beta1");
        Version v2 = Version.parseFromString("1.0.0-beta2");
        Assert.assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testSort() {
        Version v1 = Version.parseFromString("1.0.0-beta2");
        Version v2 = Version.parseFromString("1.0.0-beta1");
        List<Version> versions = new ArrayList<Version>();
        versions.add(v1);
        versions.add(v2);
        Collections.sort(versions);
        Assert.assertEquals(v2, versions.get(0));
        Assert.assertEquals(v1, versions.get(1));
    }

}
