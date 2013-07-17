/*
 * Copyright (C) 2006-2007 the original author or authors.
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

package org.codehaus.gmavenplus.model;

import com.google.common.base.Objects;


/**
 * Container for Version information in the form of
 * <tt>major.minor.revision-tag</tt>.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author Keegan Witt
 */
public class Version implements Comparable<Version> {
    public int major;
    public int minor;
    public int revision;
    public String tag;

    /**
     * Constructs a new version object with the specified parameters.
     *
     * @param major version major number
     * @param minor version minor number
     * @param revision version revision number
     * @param tag version tag string
     */
    public Version(int major, int minor, int revision, String tag) {
        if (major <= 0 || minor < 0 || revision < 0) {
            // note we don't check the tag since it can be null
            throw new IllegalArgumentException("Major must be > 0 and minor >= 0 and revision >= 0.");
        }

        this.major = major;
        this.minor = minor;
        this.revision = revision;
        if (tag == null || !tag.isEmpty()) {
            this.tag = tag;
        } else if (tag.isEmpty()) {
            this.tag = null;
        }
    }

    /**
     * Constructs a new Version object with the specified parameters.
     *
     * @param major version major number
     * @param minor version minor number
     * @param revision version revision number
     */
    public Version(int major, int minor, int revision) {
        this(major, minor, revision, null);
    }

    /**
     * Constructs a new Version object with the specified parameters.
     *
     * @param major version major number
     * @param minor version minor number
     */
    public Version(int major, int minor) {
        this(major, minor, 0);
    }

    /**
     * Constructs a new Version object with the specified parameters.
     *
     * @param major version major number
     */
    public Version(int major) {
        this(major, 0);
    }

    /**
     * Parses a new Version object from a string.
     *
     * @param version the version string to parse
     * @return the version parsed from the string
     */
    public static Version parseFromString(String version) {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Version must not be null or empty.");
        }
        String[] split = version.split("[.-]", 4);
        try {
            int major = Integer.parseInt(split[0]);
            int minor = 0;
            int revision = 0;
            StringBuilder tag = new StringBuilder();
            if (split.length >= 2) {
                try {
                    minor = Integer.parseInt(split[1]);
                } catch (NumberFormatException nfe) {
                    // version string must not have specified a minor version, leave minor as 0
                    tag.append(split[1]);
                }
            }
            if (split.length >= 3) {
                try {
                    revision = Integer.parseInt(split[2]);
                } catch (NumberFormatException nfe) {
                    // version string must not have specified a revision version, leave revision as 0
                    tag.append(split[2]);
                }
            }
            if (split.length >= 4) {
                for (int i = 3; i < split.length; i++) {
                    if (i > 3) {
                        tag.append("-");
                    }
                    tag.append(split[i]);
                }
            }
            return new Version(major, minor, revision, tag.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Major, minor, and revision must be integers.", e);
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return Objects.hashCode(major, minor, revision, tag);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj) {
        if (obj instanceof Version) {
            final Version other = (Version) obj;
            return major == other.major
                    && minor == other.minor
                    && revision == other.revision
                    && Objects.equal(tag, other.tag);
        } else {
            return false;
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder buff = new StringBuilder();

        buff.append(major)
                .append(".").append(minor)
                .append(".").append(revision);
        if (tag != null) {
            buff.append("-").append(tag);
        }

        return buff.toString();
    }

    /**
     * Compares two versions objects.  Note that if the major, minor, and revision are all
     * the same, tags are compared with {@link java.lang.String#compareTo(String) String.compareTo()}.
     *
     * @param version the version to compare this version to
     * @return <code>0</code> if the version is equal to this version, <code>1</code> if the version is greater than
     *         this version, or <code>-1</code> if the version is lower than this version.
     */
    public int compareTo(Version version) {
        int mine = (1000 * major) + (100 * minor) + (revision * 10);
        int theirs = (1000 * version.major) + (100 * version.minor) + (version.revision * 10);

        if (mine == theirs && tag != null && version.tag != null) {
            return tag.compareTo(version.tag);
        } else if (mine == theirs && tag == null && version.tag != null) {
            return 1;
        } else if (mine == theirs && tag != null && version.tag == null) {
            return -1;
        } else {
            return mine - theirs;
        }
    }

}
