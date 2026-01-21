import org.junit.Test

class VersionTest {
    @Test
    void verifyJavaVersion() {
        assert Runtime.version().feature() == 17
    }
}
