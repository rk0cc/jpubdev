package xyz.rk0cc.willpub.pubdev.structure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgDocDeserializer;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Fetching documentation information of the package {@link #packageName() name}. With given
 * {@link #latestStableVersion() latesst release of the version} and {@link #versions() all published}
 * package's documentation status.
 *
 * @since 1.0.0
 */
@JsonDeserialize(using = PubPkgDocDeserializer.class)
public record PubPkgDoc(
        @Nonnull String packageName,
        @Nonnull SemVer latestStableVersion,
        @Nonnull List<PubPkgDocVersionInfo> versions
) {
    /**
     * Indicate the specific {@link #version()} of the {@link PubPkgDoc}'s {@link #status()} and provided
     * {@link #hasDoc() documentation} already or not.
     *
     * @since 1.0.0
     */
    public record PubPkgDocVersionInfo(@Nonnull SemVer version, @Nonnull String status, boolean hasDoc) { }
}
