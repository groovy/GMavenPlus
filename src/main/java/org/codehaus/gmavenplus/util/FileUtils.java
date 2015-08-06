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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;


/**
 * A collection of file utility methods taken from Guava so we don't have to
 * depend on Guava.
 *
 * @author Keegan Witt
 * @since 1.2
 */
public class FileUtils {

    /**
     * Private constructor that should never be called since this is a static
     * utility class.
     */
    private FileUtils() { }

    /**
     * Returns the file extension without the '.' for the given filename, or
     * the empty string if the file has
     * no extension.
     *
     * @param file the file to get the extension from
     * @return the file extension
     */
    public static String getFileExtension(final File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /**
     * Returns the file extension without the '.' for the given filename, or
     * the empty string if the file has
     * no extension.
     *
     * @param file the file to get the extension from
     * @return the file extension
     */
    public static String getFileExtension(final String file) {
        return getFileExtension(new File(file));
    }

    /**
     * Returns the filename without the extension or '.'.
     *
     * @param file the file remove the extension from
     * @return the file name without its path or extension
     */
    public static String getNameWithoutExtension(final File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    /**
     * Returns the filename without the extension or '.'.
     *
     * @param file the file remove the extension from
     * @return the file name without its path or extension
     */
    public static String getNameWithoutExtension(final String file) {
        return getNameWithoutExtension(new File(file));
    }

    /**
     * Closes the InputStream if it is not null, swallowing any exceptions.
     *
     * @param inputStream the InputStream to close
     */
    public static void closeQuietly(final InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // do nothing, close quietly
            }
        }
    }

    /**
     * Closes the OutputStream if it is not null, swallowing any exceptions.
     *
     * @param outputStream the OutputStream to close
     */
    public static void closeQuietly(final OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                // do nothing, close quietly
            }
        }
    }

    /**
     * Closes the Reader if it is not null, swallowing any exceptions.
     *
     * @param reader the Reader to close
     */
    public static void closeQuietly(final Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // do nothing, close quietly
            }
        }
    }

    /**
     * Closes the Writer if it is not null, swallowing any exceptions.
     *
     * @param writer the Writer to close
     */
    public static void closeQuietly(final Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                // do nothing, close quietly
            }
        }
    }

}
