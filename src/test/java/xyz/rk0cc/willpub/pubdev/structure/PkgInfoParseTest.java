package xyz.rk0cc.willpub.pubdev.structure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import xyz.rk0cc.josev.NonStandardSemVerException;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgInfo;

import java.io.IOException;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

final class PkgInfoParseTest {
    @DisplayName("Testing parsing package info")
    @Test
    void testParseInfo() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            PubPkgInfo mi = mapper.readValue(getClass().getResource("mockapi/pkg_info.json"), PubPkgInfo.class);

            assertEquals(mi.versions().size(), 7);
            assertEquals(mi.versions().get(6).version(), mi.latest().version());
            assertTrue(mi.latest().pubspec().dependencies().isEmpty());
            assertTrue(mi.latest().pubspec().devDependencies().isUnmodifiable());
            assertEquals(SemVer.parse("1.0.0"), mi.versions().get(0).version());
            assertEquals(Month.AUGUST, mi.versions().get(2).published().getMonth());
        } catch (IOException | NonStandardSemVerException e) {
            fail(e);
        }
    }
}
