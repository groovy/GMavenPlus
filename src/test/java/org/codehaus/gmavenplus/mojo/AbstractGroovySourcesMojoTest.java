package org.codehaus.gmavenplus.mojo;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;


/**
 * Unit tests for the AbstractGroovySourcesMojo class.
 *
 * @author Keegan Witt
 */
public class AbstractGroovySourcesMojoTest {
    private TestMojo testMojo;

    @Mock
    private MavenProject project;

    @Mock
    private File basedir;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testMojo = new TestMojo();
        doReturn(basedir).when(project).getBasedir();
        doReturn(singletonList("basedir" + File.separator + "src" + File.separator + "main" + File.separator + "java")).when(project).getCompileSourceRoots();
        doReturn(singletonList("basedir" + File.separator + "src" + File.separator + "test" + File.separator + "java")).when(project).getTestCompileSourceRoots();
        testMojo.project = project;
    }

    @Test
    public void mainDefaultPattern() {
        FileSet[] results = testMojo.getFilesets(null, false);

        assertEquals(1, results.length);
        assertEquals("basedir" + File.separator + "src" + File.separator + "main" + File.separator + "groovy", results[0].getDirectory());
        assertEquals(1, results[0].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.groovy"), results[0].getIncludes());
    }

    @Test
    public void testDefaultPattern() {
        FileSet[] results = testMojo.getTestFilesets(null, false);

        assertEquals(1, results.length);
        assertEquals("basedir" + File.separator + "src" + File.separator + "test" + File.separator + "groovy", results[0].getDirectory());
        assertEquals(1, results[0].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.groovy"), results[0].getIncludes());
    }

    @Test
    public void mainWithJavaPattern() {
        FileSet[] results = testMojo.getFilesets(null, true);

        assertEquals(2, results.length);
        assertEquals("basedir" + File.separator + "src" + File.separator + "main" + File.separator + "groovy", results[0].getDirectory());
        assertEquals(1, results[0].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.groovy"), results[0].getIncludes());
        assertEquals(1, results[1].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.java"), results[1].getIncludes());
    }

    @Test
    public void testWithJavaPattern() {
        FileSet[] results = testMojo.getTestFilesets(null, true);

        assertEquals(2, results.length);
        assertEquals("basedir" + File.separator + "src" + File.separator + "test" + File.separator + "groovy", results[0].getDirectory());
        assertEquals(1, results[0].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.groovy"), results[0].getIncludes());
        assertEquals(1, results[1].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.java"), results[1].getIncludes());
    }

    @Test
    public void mainWithSpecifiedPattern() {
        FileSet fileSet = new FileSet();
        fileSet.setDirectory("basedir" + File.separator + "src" + File.separator + "custom");
        fileSet.setIncludes(singletonList("**" + File.separator + "*.gvy"));
        FileSet[] specifiedSources = new FileSet[]{fileSet};
        FileSet[] results = testMojo.getTestFilesets(specifiedSources, false);

        assertEquals(1, results.length);
        assertEquals("basedir" + File.separator + "src" + File.separator + "custom", results[0].getDirectory());
        assertEquals(1, results[0].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.gvy"), results[0].getIncludes());
    }

    @Test
    public void testWithSpecifiedPattern() {
        FileSet fileSet = new FileSet();
        fileSet.setDirectory("basedir" + File.separator + "src" + File.separator + "custom");
        fileSet.setIncludes(singletonList("**" + File.separator + "*.gvy"));
        FileSet[] specifiedSources = new FileSet[]{fileSet};
        FileSet[] results = testMojo.getTestFilesets(specifiedSources, false);

        assertEquals(1, results.length);
        assertEquals("basedir" + File.separator + "src" + File.separator + "custom", results[0].getDirectory());
        assertEquals(1, results[0].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.gvy"), results[0].getIncludes());
    }

    @Test
    public void mainWithSpecifiedPatternAndJavaPattern() {
        FileSet fileSet = new FileSet();
        fileSet.setDirectory("basedir" + File.separator + "src" + File.separator + "custom");
        fileSet.setIncludes(singletonList("**" + File.separator + "*.gvy"));
        FileSet[] specifiedSources = new FileSet[]{fileSet};
        FileSet[] results = testMojo.getTestFilesets(specifiedSources, true);

        assertEquals(2, results.length);
        assertEquals("basedir" + File.separator + "src" + File.separator + "custom", results[0].getDirectory());
        assertEquals(1, results[0].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.gvy"), results[0].getIncludes());
        assertEquals(1, results[1].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.java"), results[1].getIncludes());
    }

    @Test
    public void testWithSpecifiedPatternAndJavaPattern() {
        FileSet fileSet = new FileSet();
        fileSet.setDirectory("basedir" + File.separator + "src" + File.separator + "custom");
        fileSet.setIncludes(singletonList("**" + File.separator + "*.gvy"));
        FileSet[] specifiedSources = new FileSet[]{fileSet};
        FileSet[] results = testMojo.getTestFilesets(specifiedSources, true);

        assertEquals(2, results.length);
        assertEquals("basedir" + File.separator + "src" + File.separator + "custom", results[0].getDirectory());
        assertEquals(1, results[0].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.gvy"), results[0].getIncludes());
        assertEquals(1, results[1].getIncludes().size());
        assertListsEqual(singletonList("**" + File.separator + "*.java"), results[1].getIncludes());
    }

    private void assertListsEqual(List<String> expected, List<String> actual) {
        assertEquals(expected.size(), actual.size());
        for (String expectedEntry : expected) {
            assertTrue("Expected " + actual + " to contain " + expectedEntry, actual.contains(expectedEntry));
        }
    }

    public static class TestMojo extends AbstractGroovySourcesMojo {
        @Override
        public void execute() {
        }
    }

}
