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

import java.io.*;


/**
 * A collection of file utility methods taken from Guava so we don't have to
 * depend on Guava.
 *
 * @author Keegan Witt
 * @since 1.2
 */
public class FileUtils {

    private FileUtils() {}

    /**
     * Returns the file extension without the '.' for the given filename, or the empty string if the file has
     * no extension.
     *
     * @param file the file to get the extension from
     * @return the file extension
     */
    public static String getFileExtension(String file) {
        String fileName = new File(file).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /**
     * Returns the filename without the extension or '.'.
     *
     * @param file the name of the file remove the extension from
     * @return the file name without its path or extension
     */
    public static String getNameWithoutExtension(String file) {
        String fileName = new File(file).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    /**
     * Closes the InputStream if it is not null, swallowing any exceptions.
     *
     * @param inputStream the InputStream to close
     */
    public static void closeQuietly(InputStream inputStream) {
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
    public static void closeQuietly(OutputStream outputStream) {
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
    public static void closeQuietly(Reader reader) {
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
    public static void closeQuietly(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                // do nothing, close quietly
            }
        }
    }

}
