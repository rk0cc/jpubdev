package xyz.rk0cc.willpub.pubdev.structure.pkg;

import org.junit.jupiter.api.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

final class DerivedTagParseTest {
    @DisplayName("Resolve string tag to Java enum")
    @Test
    void resolveFromStringTest() {
        assertEquals(
                PubPkgMetrics.PanaReport.PanaEntry.DerivedTags.parseTags("sdk:flutter"),
                PubPkgMetrics.PanaReport.PanaEntry.SupportedSDK.FLUTTER
        );
        assertEquals(PubPkgMetrics.PanaReport.PanaEntry.DerivedTags.parseTags("is:null-safe"),
                PubPkgMetrics.PanaReport.PanaEntry.IsSupportedTags.NULL_SAFE
        );
        assertEquals(PubPkgMetrics.PanaReport.PanaEntry.DerivedTags.parseTags("platform:linux"),
                PubPkgMetrics.PanaReport.PanaEntry.SupportedPlatform.LINUX
        );
        assertEquals(PubPkgMetrics.PanaReport.PanaEntry.DerivedTags.parseTags("runtime:web"),
                PubPkgMetrics.PanaReport.PanaEntry.SupportedRuntime.WEB
        );
        assertNull(PubPkgMetrics.PanaReport.PanaEntry.DerivedTags.parseTags("foo:bar"));
    }

    @DisplayName("Resolve a list of string tags to Java enum")
    @Test
    void resolveFromListTest() {
        Set<PubPkgMetrics.PanaReport.PanaEntry.DerivedTags<? extends Enum<?>>> dts
                = PubPkgMetrics.PanaReport.PanaEntry.DerivedTags.parseTagsList(List.of(
                        "sdk:dart",
                        "platform:windows",
                        "platform:macos",
                        "runtime:native-jit",
                        "runtime:native-aot"
        ));

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    Iterator<PubPkgMetrics.PanaReport.PanaEntry.DerivedTags<? extends Enum<?>>> dti = dts.iterator();
                    dti.next();
                    dti.remove();
                },
                "Returned tags set can not be modified via Iterator"
        );

        System.out.println("Assigned sets: " + dts);

        assertTrue(dts.contains(PubPkgMetrics.PanaReport.PanaEntry.SupportedSDK.DART));
        assertTrue(dts.contains(PubPkgMetrics.PanaReport.PanaEntry.SupportedPlatform.WINDOWS));
        assertTrue(dts.contains(PubPkgMetrics.PanaReport.PanaEntry.SupportedPlatform.MACOS));
        assertTrue(dts.contains(PubPkgMetrics.PanaReport.PanaEntry.SupportedRuntime.NATIVE_AOT));
        assertTrue(dts.contains(PubPkgMetrics.PanaReport.PanaEntry.SupportedRuntime.NATIVE_JIT));
        assertFalse(dts.contains(PubPkgMetrics.PanaReport.PanaEntry.IsSupportedTags.NULL_SAFE));
    }
}
