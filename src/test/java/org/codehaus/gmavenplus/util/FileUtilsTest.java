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

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for the FileUtils class.
 *
 * @author Keegan Witt
 */
public class FileUtilsTest {
    private static final IOException ioException = new IOException("Intentionally blowing up.");

    @Test
    public void testGetFileExtension() {
        assertEquals("gz", FileUtils.getFileExtension("foo.tar.gz"));
    }

    @Test
    public void testGetNameWithoutExtension() {
        assertEquals("foo.tar", FileUtils.getNameWithoutExtension("foo.tar.gz"));
    }

    @Test
    public void testCloseInputStreamQuietly() throws Exception {
        InputStream inputStream = mock(InputStream.class);
        doThrow(ioException).when(inputStream).close();
        FileUtils.closeQuietly(inputStream);
    }

    @Test
    public void testCloseOutputStreamQuietly() throws Exception {
        OutputStream outputStream = mock(OutputStream.class);
        doThrow(ioException).when(outputStream).close();
        FileUtils.closeQuietly(outputStream);
    }

    @Test
    public void testCloseReaderQuietly() throws Exception {
        Reader reader = mock(Reader.class);
        doThrow(ioException).when(reader).close();
        FileUtils.closeQuietly(reader);
    }

    @Test
    public void testCloseWriterQuietly() throws Exception {
        Writer writer = mock(Writer.class);
        doThrow(ioException).when(writer).close();
        FileUtils.closeQuietly(writer);
    }

}
