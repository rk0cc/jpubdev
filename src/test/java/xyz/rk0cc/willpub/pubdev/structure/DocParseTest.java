package xyz.rk0cc.willpub.pubdev.structure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import xyz.rk0cc.josev.NonStandardSemVerException;
import xyz.rk0cc.josev.SemVer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

final class DocParseTest {
    @DisplayName("Parse mock JSON of documentation status")
    @Test
    void testParseDoc() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            PubPkgDoc md = mapper.readValue(getClass().getResource("mockapi/doc.json"), PubPkgDoc.class);

            assertEquals("darttindie", md.packageName());
            assertEquals(SemVer.parse("1.0.0-dev.3"), md.latestStableVersion());
            assertEquals(3, md.versions().size());
            assertFalse(md.versions().get(0).hasDoc());
            assertEquals("success", md.versions().get(1).status());
        } catch (IOException | NonStandardSemVerException e) {
            fail(e);
        }
    }
}
