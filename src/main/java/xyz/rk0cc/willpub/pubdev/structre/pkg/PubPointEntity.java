package xyz.rk0cc.willpub.pubdev.structre.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.pubdev.parser.DetailedPubPointEntityDeserializer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface PubPointEntity {
    @Nonnegative
    int grantedPoints();

    @Nonnegative
    int maxPoints();

    @JsonDeserialize(using = DetailedPubPointEntityDeserializer.class)
    record DetailedPubPointEntity(
            @Nonnull String id,
            @Nonnull String title,
            @Nonnegative int grantedPoints,
            @Nonnegative int maxPoints,
            @Nonnull PubPointStatus status
    ) implements PubPointEntity {
        public enum PubPointStatus {
            PASSED,
            PARTIAL,
            FAILED;
        }
    }
}
