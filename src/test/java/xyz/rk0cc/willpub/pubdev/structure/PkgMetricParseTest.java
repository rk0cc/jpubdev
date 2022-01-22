package xyz.rk0cc.willpub.pubdev.structure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import xyz.rk0cc.willpub.exceptions.pubdev.MetricsPendingAnalysisException;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgMetrics;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgMetrics.PanaReport.PanaEntry.*;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPointEntity.DetailedPubPointEntity.PubPointStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

final class PkgMetricParseTest {
    private ObjectMapper mapper;

    @BeforeEach
    void initMapper() {
        mapper = new ObjectMapper();
    }

    @DisplayName("Parse analysed metrics test")
    @Test
    void testAnalysedMetrics() {
        try {
            PubPkgMetrics mam = mapper.readValue(
                    getClass().getResource("mockapi/pkg_metric_analysed.json"),
                    PubPkgMetrics.class
            );

            assertNotNull(mam.dartDocReport());
            assertNotNull(mam.panaReport());
            assertNotNull(mam.dartDocReport().entry());
            assertNotNull(mam.panaReport().entry());
            assertEquals("2022.01.06", mam.runtimeVersion());
            assertEquals(2021, mam.packageCreated().getYear());
            assertTrue(mam.flags().contains("latest-stable"));
            assertFalse(mam.dartDocReport().entry().usesFlutter());
            assertEquals(1005208, mam.dartDocReport().entry().totalSize());
            assertTrue(mam.panaReport().entry().derivedTags().contains(SupportedSDK.DART));
            assertEquals("LGPL-3.0", mam.panaReport().entry().licenseName());
            assertTrue(mam.panaReport().entry().allDependencies().contains("quiver"));
            assertTrue(mam.panaReport().entry().reportSections().stream().allMatch(
                    rs -> rs.status() == PubPointStatus.PASSED
            ));
        } catch (IOException e) {
            fail(e);
        }
    }

    @DisplayName("Parse aborted metrics test")
    @Test
    void testAbortedMetrics() {
        try {
            PubPkgMetrics mab = mapper.readValue(
                    getClass().getResource("mockapi/pkg_metric_aborted.json"),
                    PubPkgMetrics.class
            );

            assertNotNull(mab.dartDocReport());
            assertNotNull(mab.panaReport());
            assertNull(mab.dartDocReport().entry());
            assertNull(mab.panaReport().entry());
        } catch (IOException e) {
            fail(e);
        }
    }

    @DisplayName("Parse pending metric test")
    @Test
    void testPendingMetrics() {
        try {
            PubPkgMetrics pam = mapper.readValue(
                    getClass().getResource("mockapi/pkg_metric_pending.json"),
                    PubPkgMetrics.class
            );

            fail("Pending analysis metric contains nothing and suppose to throw exception");
        } catch (IOException e) {
            assertInstanceOf(MetricsPendingAnalysisException.class, e);
        }
    }

    @DisplayName("Throw AssertionError if MetricsPendingAnalysisException")
    @Test
    void assertErrorIfThrowPAEOutsideDeserializer() {
        assertThrows(AssertionError.class, () -> {
            try {
                throw new MetricsPendingAnalysisException();
            } catch (MetricsPendingAnalysisException e) {
                fail("The exception should not be thrown outside the deserializer");
            }
        });
    }
}
