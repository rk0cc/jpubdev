package xyz.rk0cc.willpub.exceptions.pubdev;

import xyz.rk0cc.willpub.pubdev.parser.PubPkgMetricsDeserializer;

import java.io.IOException;

/**
 *
 */
public class MetricsPendingAnalysisException extends IOException {
    public MetricsPendingAnalysisException() {
        super("This package is pending analysis state, no report exported.");
        assert StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .getCallerClass().equals(PubPkgMetricsDeserializer.class);
    }
}
