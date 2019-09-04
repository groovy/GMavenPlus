package org.codehaus.gmavenplus.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Unit tests for the Version class.
 *
 * @author Keegan Witt
 */
public class VersionTest {

    @Test
    public void testCompare() {
        assertTrue(new Version(1, 9).compareTo(new Version(1, 10)) < 0);
    }

    @Test
    public void testCompareWithTag() {
        Version v1 = Version.parseFromString("1.0.0");
        Version v2 = Version.parseFromString("1.0.0-beta-1");
        assertTrue(v1.compareTo(v2) > 0);
    }

    @Test
    public void testCompareWithTwoTags() {
        Version v1 = Version.parseFromString("1.0.0-beta-1");
        Version v2 = Version.parseFromString("1.0.0-beta-2");
        assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testCompareWithTwoTagsDifferentStartUppercase() {
        Version v1 = Version.parseFromString("1.0.0-beta-1");
        Version v2 = Version.parseFromString("1.0.0-RC-2");
        assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testCompareWithTwoTagsDifferentStartLowercase() {
        Version v1 = Version.parseFromString("1.0.0-beta-1");
        Version v2 = Version.parseFromString("1.0.0-rc-2");
        assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testCompareWithSnapshotTag() {
        Version v1 = Version.parseFromString("1.0-SNAPSHOT");
        Version v2 = Version.parseFromString("1.0");
        assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testCompareWithSnapshotTagOtherHasRevision() {
        Version v1 = Version.parseFromString("1.0-SNAPSHOT");
        Version v2 = Version.parseFromString("1.0.1");
        assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testCompareWithTwoTagsOneSnapshot() {
        Version v1 = Version.parseFromString("1.0.0-beta-1");
        Version v2 = Version.parseFromString("1.0.0-beta-2-SNAPSHOT");
        assertTrue(v1.compareTo(v2) < 0);

        v1 = Version.parseFromString("1.0.0-beta-1-SNAPSHOT");
        v2 = Version.parseFromString("1.0.0-beta-2");
        assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testSort() {
        Version v1 = Version.parseFromString("1.0.0-alpha-1");
        Version v2 = Version.parseFromString("1.0.0-alpha-2");
        Version v3 = Version.parseFromString("1.0.0-beta-1");
        Version v4 = Version.parseFromString("1.0.0-beta-2");
        Version v5 = Version.parseFromString("1.0.0-rc-1");
        Version v6 = Version.parseFromString("1.0.0-rc-2");
        Version v7 = Version.parseFromString("1.0.0");
        Version v8 = Version.parseFromString("1.0.1");
        Version v9 = Version.parseFromString("1.1.0");
        Version v10 = Version.parseFromString("2.0.0");
        List<Version> versions = new ArrayList<Version>();
        versions.add(v10);
        versions.add(v9);
        versions.add(v8);
        versions.add(v7);
        versions.add(v6);
        versions.add(v5);
        versions.add(v4);
        versions.add(v3);
        versions.add(v2);
        versions.add(v1);
        Collections.sort(versions);
        assertEquals(v1, versions.get(0));
        assertEquals(v2, versions.get(1));
        assertEquals(v3, versions.get(2));
        assertEquals(v4, versions.get(3));
        assertEquals(v5, versions.get(4));
        assertEquals(v6, versions.get(5));
        assertEquals(v7, versions.get(6));
        assertEquals(v8, versions.get(7));
        assertEquals(v9, versions.get(8));
        assertEquals(v10, versions.get(9));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegatives() {
        new Version(-1);
    }

    @Test
    public void testNotEqualsWithNonVersion() {
        Version version = new Version(0);
        assertFalse(version.toString().equals(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromStringMajorIsNotInteger() {
        Version.parseFromString("a.b");
    }

    @Test
    public void testParseFromStringMinorIsNotInteger() {
        Version version = Version.parseFromString("0.a");
        assertEquals(0, version.getMinor());
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
        assertEquals(5, Version.parseFromString("0-multi-part-tag").toString().split("-").length);
    }

    @Test
    public void testParsingJavaVersion() {
        assertTrue(Version.parseFromString("1.7.0_45").compareTo(new Version(1, 7), false) >= 0);
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
        assertEquals(num, version.getMajor());
        assertEquals(num, version.getMinor());
        assertEquals(num, version.getRevision());
        assertEquals(str, version.getTag());
    }

    @Test
    public void testHashCodeSame() {
        Version one = new Version(1, 2, 3);
        Version two = new Version(1, 2, 3);
        assertEquals(one.hashCode(), two.hashCode());
    }

    @Test
    public void testBetaIsNewerThanAlpha() {
        Version v1 = Version.parseFromString("1.0.0-alpha-1");
        Version v2 = Version.parseFromString("1.0.0-beta-2");
        assertTrue(v1.compareTo(v2) < 0);
        v1 = Version.parseFromString("1.0.0-alpha-2");
        v2 = Version.parseFromString("1.0.0-beta-1");
        assertTrue(v1.compareTo(v2) < 0);
    }

    @Test
    public void testDotSeparatingQualifierAndVersion() {
        Version v1 = Version.parseFromString("3.0.0.beta-3");
        Version v2 = Version.parseFromString("3.0.0-beta-3");
        assertEquals(v1, v2);
    }

}
