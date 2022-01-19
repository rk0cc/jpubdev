package xyz.rk0cc.willpub.pubdev.structre.pkg;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface PubPointEntity {
    @Nonnegative
    int grantedPoints();

    @Nonnegative
    int maxPoints();

    record DetailedPubPointEntity(
            @Nonnull String id,
            @Nonnull String title,
            @Nonnegative int grantedPoints,
            @Nonnegative int maxPoints,
            @Nonnull PubPointStatus status
    ) implements PubPointEntity {
        enum PubPointStatus {
            PASSED,
            FAILED;
        }
    }
}
