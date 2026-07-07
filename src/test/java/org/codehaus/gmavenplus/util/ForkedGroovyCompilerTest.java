package org.codehaus.gmavenplus.util;

import org.codehaus.gmavenplus.model.GroovyCompileConfiguration;
import org.codehaus.gmavenplus.model.GroovyDocConfiguration;
import org.codehaus.gmavenplus.model.GroovyStubConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.security.Permission;
import java.util.Collections;

import static org.junit.Assert.*;

public class ForkedGroovyCompilerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private PrintStream originalErr;
    private ByteArrayOutputStream errContent;
    private SecurityManager originalSecurityManager;

    @Before
    public void setup() {
        originalErr = System.err;
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        originalSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new NoExitSecurityManager(originalSecurityManager));
    }

    @After
    public void tearDown() {
        System.setErr(originalErr);
        System.setSecurityManager(originalSecurityManager);
    }

    @Test
    public void testMainNoArgs() {
        try {
            ForkedGroovyCompiler.main(new String[0]);
            fail("Expected System.exit(1)");
        } catch (NoExitException e) {
            assertEquals(1, e.getStatus());
            assertTrue(errContent.toString().contains("Usage:"));
        }
    }

    @Test
    public void testMainTooManyArgs() {
        try {
            ForkedGroovyCompiler.main(new String[]{"arg1", "arg2"});
            fail("Expected System.exit(1)");
        } catch (NoExitException e) {
            assertEquals(1, e.getStatus());
            assertTrue(errContent.toString().contains("Usage:"));
        }
    }

    @Test
    public void testMainFileNotFound() {
        try {
            ForkedGroovyCompiler.main(new String[]{"non-existent-file"});
            fail("Expected System.exit(1)");
        } catch (NoExitException e) {
            assertEquals(1, e.getStatus());
        }
    }

    @Test
    public void testMainInvalidFileContent() throws IOException {
        File tempFile = temporaryFolder.newFile("invalid-config");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("not a serialized object".getBytes());
        }

        try {
            ForkedGroovyCompiler.main(new String[]{tempFile.getAbsolutePath()});
            fail("Expected System.exit(1)");
        } catch (NoExitException e) {
            assertEquals(1, e.getStatus());
        }
    }

    @Test
    public void testMainCompileSuccess() throws IOException {
        File tempFile = temporaryFolder.newFile("compile-config");
        GroovyCompileConfiguration config = new GroovyCompileConfiguration(Collections.emptySet(), Collections.emptyList(), temporaryFolder.newFolder());
        serializeConfiguration(config, tempFile);

        ForkedGroovyCompiler.main(new String[]{tempFile.getAbsolutePath()});
        // Success if no exception thrown (System.exit(0) is implicit if main finishes normally)
    }

    @Test
    public void testMainStubSuccess() throws IOException {
        File tempFile = temporaryFolder.newFile("stub-config");
        GroovyStubConfiguration config = new GroovyStubConfiguration(Collections.emptySet(), Collections.emptyList(), temporaryFolder.newFolder());
        serializeConfiguration(config, tempFile);

        ForkedGroovyCompiler.main(new String[]{tempFile.getAbsolutePath()});
    }

    @Test
    public void testMainDocSuccess() throws IOException {
        File tempFile = temporaryFolder.newFile("doc-config");
        GroovyDocConfiguration config = new GroovyDocConfiguration(new org.apache.maven.shared.model.fileset.FileSet[0], Collections.emptyList(), temporaryFolder.newFolder());
        serializeConfiguration(config, tempFile);

        ForkedGroovyCompiler.main(new String[]{tempFile.getAbsolutePath()});
    }

    @Test
    public void testMainUnknownConfigurationType() throws IOException {
        File tempFile = temporaryFolder.newFile("unknown-config");
        serializeConfiguration("just a string", tempFile);

        try {
            ForkedGroovyCompiler.main(new String[]{tempFile.getAbsolutePath()});
            fail("Expected System.exit(1)");
        } catch (NoExitException e) {
            assertEquals(1, e.getStatus());
        }
    }

    private void serializeConfiguration(Object config, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(config);
        }
    }

    private static class NoExitException extends RuntimeException {
        private final int status;
        public NoExitException(int status) {
            this.status = status;
        }
        public int getStatus() {
            return status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager {
        private final SecurityManager parent;
        public NoExitSecurityManager(SecurityManager parent) {
            this.parent = parent;
        }
        @Override
        public void checkPermission(Permission perm) {
            if (parent != null) {
                parent.checkPermission(perm);
            }
        }
        @Override
        public void checkExit(int status) {
            throw new NoExitException(status);
        }
    }

}
