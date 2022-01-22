package xyz.rk0cc.willpub.pubdev.structure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgScore;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

final class PkgScoreParseTest {
    @DisplayName("Test parsing score")
    @Test
    void testParseScore() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            PubPkgScore ms = mapper.readValue(getClass().getResource(
                    "mockapi/pkg_score.json"),
                    PubPkgScore.class
            );

            assertEquals(4796, ms.likeCount());
            assertEquals(0.9998893315626384, ms.popularityScore());
            assertEquals(20, ms.lastUpdated().getHour());
        } catch (IOException e) {
            fail(e);
        }
    }
}
