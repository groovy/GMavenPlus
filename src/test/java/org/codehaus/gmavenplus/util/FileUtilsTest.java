package org.codehaus.gmavenplus.util;

import org.junit.Test;

import java.io.*;

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
