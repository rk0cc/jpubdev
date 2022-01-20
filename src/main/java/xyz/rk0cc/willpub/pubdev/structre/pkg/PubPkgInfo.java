package xyz.rk0cc.willpub.pubdev.structre.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgInfoDeserializer;
import xyz.rk0cc.willpub.pubspec.data.PubspecSnapshot;

import javax.annotation.Nonnull;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Giving basic information of the package under the {@link #name() name} with {@link #latest() latest} and
 * {@link #versions() all published} version information.
 *
 * @since 1.0.0
 */
@JsonDeserialize(using = PubPkgInfoDeserializer.class)
public record PubPkgInfo(
        @Nonnull String name,
        @Nonnull PubPkgVersion latest,
        @Nonnull List<PubPkgVersion> versions
) {
    /**
     * Information of specific version information from {@link PubPkgInfo}.
     * <br/>
     * It contains {@link #version() package version}, {@link #pubspec() JSON format of pubspec.yaml},
     * {@link #archiveURL() downloadable package archive} and {@link #published() published time in UTC}.
     *
     * @since 1.0.0
     */
    public record PubPkgVersion(
            @Nonnull SemVer version,
            @Nonnull PubspecSnapshot pubspec,
            @Nonnull URL archiveURL,
            @Nonnull ZonedDateTime published
    ) {}
}
