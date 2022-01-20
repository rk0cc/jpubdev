package xyz.rk0cc.willpub.pubdev.structre.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgScoreDeserializer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.time.ZonedDateTime;

/**
 * Giving package's scores that affecting the display order in
 * {@link xyz.rk0cc.willpub.pubdev.PubDevRepository.SearchOrdering#POINTS} and
 * {@link xyz.rk0cc.willpub.pubdev.PubDevRepository.SearchOrdering#TOP}.
 *
 * @since 1.0.0
 */
@JsonDeserialize(using = PubPkgScoreDeserializer.class)
public record PubPkgScore(
        @Nonnegative int grantedPoints,
        @Nonnegative int maxPoints,
        @Nonnegative long likeCount,
        @Nonnegative double popularityScore,
        @Nonnull ZonedDateTime lastUpdated
) implements PubPointEntity {}
