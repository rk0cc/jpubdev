package xyz.rk0cc.willpub.pubdev.structre.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.pubdev.parser.DetailedPubPointEntityDeserializer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * An entity of representing point in the package.
 *
 * @since 1.0.0
 */
public interface PubPointEntity {
    /**
     * A point granted for the package.
     *
     * @return Point granted for the package.
     */
    @Nonnegative
    int grantedPoints();

    /**
     * The maximum point for {@link #grantedPoints()}.
     *
     * @return The maximum point of the package.
     */
    @Nonnegative
    int maxPoints();

    /**
     * A detailed {@link PubPointEntity} which contains with {@link #title()}, {@link #id()} and
     * {@link #status() status} with the points.
     *
     * @since 1.0.0
     */
    @JsonDeserialize(using = DetailedPubPointEntityDeserializer.class)
    record DetailedPubPointEntity(
            @Nonnull String id,
            @Nonnull String title,
            @Nonnegative int grantedPoints,
            @Nonnegative int maxPoints,
            @Nonnull PubPointStatus status
    ) implements PubPointEntity {
        /**
         * An {@link Enum} representing {@link #status()} in {@link DetailedPubPointEntity}.
         */
        public enum PubPointStatus {
            /**
             * The package gained {@link #maxPoints() maximum point} in {@link DetailedPubPointEntity}.
             */
            PASSED,
            /**
             * The package gained half or higher of {@link #maxPoints() maximum point}.
             */
            PARTIAL,
            /**
             * The package gained lower half of {@link #maxPoints() maximum point}.
             */
            FAILED
        }
    }
}
