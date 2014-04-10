/*
 * Copyright (C) 2014 the original author or authors.
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

package org.codehaus.gmavenplus.util;

import java.lang.reflect.Array;


/**
 * Utility methods ported from later JDK versions for use with JDK 5.
 *
 * @author Keegan Witt
 * @since 1.2
 */
public class JDK5Utils {

    @SuppressWarnings("unchecked")
    public static <T> T[] Arrays_copyOf(T[] original, int newLength) {
        return (T[]) Arrays_copyOf(original, newLength, original.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T,U> T[] Arrays_copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        T[] copy = ((Object)newType == (Object)Object[].class)
                ? (T[]) new Object[newLength]
                : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0,
                Math.min(original.length, newLength));
        return copy;
    }

}
