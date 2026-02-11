package org.codehaus.gmavenplus.mojo;

import org.codehaus.gmavenplus.model.internal.Version;
import org.codehaus.gmavenplus.util.ClassWrangler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the AbstractCompileMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractCompileMojoTest {
    private TestMojo testMojo;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testMojo = new TestMojo();
    }

    @Test
    public void testGroovyVersionSupportsActionTrue() {
        testMojo = new TestMojo("1.5.0");
        assertTrue(testMojo.groovyVersionSupportsAction());
    }

    @Test
    public void testGroovyVersionSupportsActionFalse() {
        testMojo = new TestMojo("1.1-rc-3");
        assertFalse(testMojo.groovyVersionSupportsAction());
    }

    protected static class TestMojo extends AbstractCompileMojo {
        protected TestMojo() {
            this(GROOVY_1_5_0.toString(), false);
        }

        protected TestMojo(String groovyVersion) {
            this(groovyVersion, false);
        }

        protected TestMojo(String groovyVersion, boolean indy) {
            classWrangler = mock(ClassWrangler.class);
            doReturn(Version.parseFromString(groovyVersion)).when(classWrangler).getGroovyVersion();
            doReturn(indy).when(classWrangler).isGroovyIndy();
        }

        @Override
        public void execute() {
        }
    }

}
