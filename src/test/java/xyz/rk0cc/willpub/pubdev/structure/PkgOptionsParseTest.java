package xyz.rk0cc.willpub.pubdev.structure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgOptions;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

final class PkgOptionsParseTest {
    @DisplayName("Test parsing options")
    @Test
    void testParseOpts() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            PubPkgOptions mo = mapper.readValue(
                    getClass().getResource("mockapi/pkg_opts.json"),
                    PubPkgOptions.class
            );

            assertFalse(mo.discontinues());
            assertFalse(mo.unlisted());
            assertNull(mo.replacedBy());
        } catch (IOException e) {
            fail(e);
        }
    }
}
