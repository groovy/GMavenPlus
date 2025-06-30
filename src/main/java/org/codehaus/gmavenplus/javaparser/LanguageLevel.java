package org.codehaus.gmavenplus.javaparser;

import java.util.Arrays;

public enum LanguageLevel {
    JAVA_1_0,
    JAVA_1_1,
    JAVA_1_2,
    JAVA_1_3,
    JAVA_1_4,
    JAVA_5,
    JAVA_6,
    JAVA_7,
    JAVA_8,
    JAVA_9,
    JAVA_10,
    JAVA_10_PREVIEW,
    JAVA_11,
    JAVA_11_PREVIEW,
    JAVA_12,
    JAVA_12_PREVIEW,
    JAVA_13,
    JAVA_13_PREVIEW,
    JAVA_14,
    JAVA_14_PREVIEW,
    JAVA_15,
    JAVA_15_PREVIEW,
    JAVA_16,
    JAVA_16_PREVIEW,
    JAVA_17,
    JAVA_17_PREVIEW,
    JAVA_18,
    JAVA_19,
    JAVA_20,
    JAVA_21;

    public static final LanguageLevel RAW = null;
    public static final LanguageLevel POPULAR = JAVA_11;
    public static final LanguageLevel CURRENT = JAVA_18;
    public static final LanguageLevel BLEEDING_EDGE = JAVA_21;
    private static final LanguageLevel[] yieldSupport = new LanguageLevel[]{
            JAVA_1_0, JAVA_13, JAVA_13_PREVIEW, JAVA_14, JAVA_14_PREVIEW, JAVA_15, JAVA_15_PREVIEW, JAVA_16,
            JAVA_16_PREVIEW, JAVA_17, JAVA_17_PREVIEW, JAVA_18, JAVA_19, JAVA_20, JAVA_21};

    public boolean isYieldSupported() {
        return Arrays.stream(yieldSupport).anyMatch((level) -> level == this);
    }
}
