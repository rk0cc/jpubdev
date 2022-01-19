package xyz.rk0cc.willpub.pubdev.structre.pkg;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.time.ZonedDateTime;

public record PubPkgScore(
        @Nonnegative int grantedPoints,
        @Nonnegative int maxPoints,
        @Nonnegative long likeCount,
        @Nonnegative double popularityScore,
        @Nonnull ZonedDateTime lastUpdated
) implements PubPointEntity {}
