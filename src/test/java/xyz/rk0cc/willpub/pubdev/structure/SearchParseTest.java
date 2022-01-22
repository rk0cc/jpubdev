package xyz.rk0cc.willpub.pubdev.structure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

final class SearchParseTest {
    @DisplayName("Parse search result context from mock JSON file")
    @Test
    void testParseSearchContext() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            PubSearchResult.PubSearchContext ctx = mapper.readValue(
                    getClass().getResource("mockapi/search.json"),
                    PubSearchResult.PubSearchContext.class
            );
            assertTrue(ctx.hasNextPage());
            assertTrue(ctx.packages().contains("foo"));
            assertThrows(
                    UnsupportedOperationException.class,
                    () -> ctx.packages().add("bar")
            );
            assertEquals("path", ctx.packages().get(3));
            assertFalse(ctx.packages().contains("flutter"));
        } catch (IOException e) {
            fail(e);
        }
    }
}
