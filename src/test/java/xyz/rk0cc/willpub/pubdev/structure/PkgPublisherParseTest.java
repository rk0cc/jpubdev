package xyz.rk0cc.willpub.pubdev.structure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgPublisher;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

final class PkgPublisherParseTest {
    @DisplayName("Test parse publisher info")
    @Test
    void testParsePublisher() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            PubPkgPublisher mp = mapper.readValue(getClass().getResource(
                    "mockapi/pkg_publisher.json"),
                    PubPkgPublisher.class
            );

            assertNotNull(mp.publisherId());
            assertEquals("dart.dev", mp.publisherId());
        } catch (IOException e) {
            fail(e);
        }
    }
}
