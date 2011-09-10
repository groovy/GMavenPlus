package gmavenplus.model;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author wittk
 * @version $Rev$ $Date$
 */
public class VersionTest {

    @Test
    public void testCompare() {
        Assert.assertTrue(new Version(1, 9).compareTo(new Version(1, 10)) < 0);
    }

}
