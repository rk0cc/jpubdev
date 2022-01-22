package xyz.rk0cc.willpub.exceptions.pubdev;

import xyz.rk0cc.willpub.pubdev.parser.PubPkgMetricsDeserializer;

import java.io.IOException;

/**
 * {@link PubPkgMetricsDeserializer Deserializing package's metrics} that is not finished its analysis yet and return
 * empty {@link com.fasterxml.jackson.databind.node.ObjectNode empty object} on REST response.
 *
 * @since 1.0.0
 */
public final class MetricsPendingAnalysisException extends IOException {
    /**
     * Construct an exception to indicate the analysis in still uncompleted.
     * <br/>
     * This exception only allow throw from {@link PubPkgMetricsDeserializer} only. If this exception throw outside the
     * {@link PubPkgMetricsDeserializer deserializer}, it throws {@link AssertionError}.
     */
    public MetricsPendingAnalysisException() {
        super("This package is pending analysis state, no report exported.");
        assert StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .getCallerClass().equals(PubPkgMetricsDeserializer.class);
    }
}
