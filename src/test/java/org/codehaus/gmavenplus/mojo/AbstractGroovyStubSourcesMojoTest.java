package org.codehaus.gmavenplus.mojo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;


/**
 * Unit tests for the AbstractGroovyStubSourcesMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGroovyStubSourcesMojoTest {
    private TestMojo testMojo;
    @Mock
    private File outputDirectory;
    private static final String PATH = "path";

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testMojo = new TestMojo();
        doReturn(PATH).when(outputDirectory).getAbsolutePath();
    }

    @Test
    public void testGetStubsEmpty() {
        assertEquals(0, testMojo.getStubs(outputDirectory).size());
    }

    protected static class TestMojo extends AbstractGroovyStubSourcesMojo {
        @Override
        public void execute() {
        }
    }

}
