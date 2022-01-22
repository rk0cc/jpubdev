package xyz.rk0cc.willpub.pubdev;

import org.junit.jupiter.api.*;
import xyz.rk0cc.willpub.pubdev.structure.PubSearchResult;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WARNING: This test SHOULD BE {@link Disabled DISABLED} before push to reduce risk of DoS concern on pub repository
 * server.
 * <br/>
 * All test tasks will be followed result of pub.dev's API. If using other repository site, package name may be required
 * to modify.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Do not perform fetch test on CI/CD platform which may cause DoS for pub repository server if run repeatedly.")
final class FetchTest {
    private static PubDevRepository spdr;

    @BeforeAll
    static void initRepo() {
        // Replace to PubDevRepository.getInstance() if using prefer repository.
        spdr = new PubDevRepository(URI.create("https://replace.me.to.pub.repo.url"));
    }

    @DisplayName("Test return search result")
    @Test
    void testJustSearch() {
        try {
            PubSearchResult mr = new PubDevRESTClient(spdr).search("foo");
            assertDoesNotThrow(() -> mr.nextPageURL(spdr));
            assertThrows(IndexOutOfBoundsException.class, () -> mr.previousPageURL(spdr));
            assertTrue(mr.context().packages().contains("foo"));
        } catch (IOException | InterruptedException e) {
            fail(e);
        }
    }

    // Implement tests whatever you want below
}
