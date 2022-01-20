package xyz.rk0cc.willpub.exceptions.pubdev;

import java.io.IOException;

public class MetricsPendingAnalysisException extends IOException {
    public MetricsPendingAnalysisException() {
        super("This package is pending analysis state, no report exported.");
    }
}
