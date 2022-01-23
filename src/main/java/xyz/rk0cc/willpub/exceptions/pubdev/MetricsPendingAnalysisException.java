package xyz.rk0cc.willpub.exceptions.pubdev;

import com.fasterxml.jackson.databind.node.ObjectNode;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgMetricsDeserializer;

import javax.annotation.Nonnull;

/**
 * {@link PubPkgMetricsDeserializer Deserializing package's metrics} that is not finished its analysis yet and return
 * empty {@link com.fasterxml.jackson.databind.node.ObjectNode empty object} on REST response.
 *
 * @since 1.0.0
 */
public final class MetricsPendingAnalysisException extends PubDevReturnNothingException {
    /**
     * Construct an exception to indicate the analysis in still uncompleted.
     * <br/>
     * This exception only allow throw from {@link PubPkgMetricsDeserializer} only. If this exception throw outside the
     * {@link PubPkgMetricsDeserializer deserializer}, it throws {@link AssertionError}.
     */
    public MetricsPendingAnalysisException(@Nonnull ObjectNode objectNode) {
        super(objectNode, "This package is pending analysis state, no report exported.");
    }

    @Nonnull
    @Override
    Class<?> triggeredClass() {
        return PubPkgMetricsDeserializer.class;
    }
}
