package org.codehaus.gmavenplus.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Unit tests for the Version class.
 *
 * @author Keegan Witt
 */
public class VersionTest {

    @Test
    public void testCompare() {
        Assert.assertTrue(new Version(1, 9).compareTo(new Version(1, 10)) < 0);
    }

    @Test
    public void testCompareWithTag() {
        Version v1 = Version.parseFromString("1.0.0");
        Version v2 = Version.parseFromString("1.0.0-beta-1");
        Assert.assertTrue(v1.compareTo(v2) > 0);
    }

    @Test
    public void testCompareWithTwoTags() {
        Version v1 = Version.parseFromString("1.0.0-beta-1");
        Version v2 = Version.parseFromString("1.0.0-beta-2");
        Assert.assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testCompareWithSnapshotTag() {
        Version v1 = Version.parseFromString("1.0-SNAPSHOT");
        Version v2 = Version.parseFromString("1.0");
        Assert.assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testCompareWithSnapshotTagOtherHasRevision() {
        Version v1 = Version.parseFromString("1.0-SNAPSHOT");
        Version v2 = Version.parseFromString("1.0.1");
        Assert.assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testCompareWithTwoTagsOneSnapshot() {
        Version v1 = Version.parseFromString("1.0.0-beta-1");
        Version v2 = Version.parseFromString("1.0.0-beta-2-SNAPSHOT");
        Assert.assertTrue(v1.compareTo(v2) < 0);

        v1 = Version.parseFromString("1.0.0-beta-1-SNAPSHOT");
        v2 = Version.parseFromString("1.0.0-beta-2");
        Assert.assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testSort() {
        Version v1 = Version.parseFromString("1.0.0-beta-2");
        Version v2 = Version.parseFromString("1.0.0-beta-1");
        List<Version> versions = new ArrayList<Version>();
        versions.add(v1);
        versions.add(v2);
        Collections.sort(versions);
        Assert.assertEquals(v2, versions.get(0));
        Assert.assertEquals(v1, versions.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegatives() {
        Version version = new Version(-1);
    }

    @Test
    public void testNotEqualsWithNonVersion() {
        Version version = new Version(0);
        Assert.assertFalse(version.equals(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromStringMajorIsNotInteger() {
        Version.parseFromString("a.b");
    }

    @Test
    public void testParseFromStringMinorIsNotInteger() {
        Version version = Version.parseFromString("0.a");
        Assert.assertEquals(0, version.getMinor());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromStringNull() {
        Version.parseFromString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromStringEmpty() {
        Version.parseFromString("");
    }

    @Test
    public void testParseFromStringAndToStringWithMultiPartTag() {
        Assert.assertEquals(5, Version.parseFromString("0-multi-part-tag").toString().split("-").length);
    }

    @Test
    public void testGettersAndSetters() {
        int num = 1;
        String str = "string";
        Version version = new Version(0)
                .setMajor(num)
                .setMinor(num)
                .setRevision(num)
                .setTag(str);
        Assert.assertEquals(num, version.getMajor());
        Assert.assertEquals(num, version.getMinor());
        Assert.assertEquals(num, version.getRevision());
        Assert.assertEquals(str, version.getTag());
    }

    @Test
    public void testHashCode() {
        Assert.assertNotNull(new Version(0).hashCode());
    }

}
