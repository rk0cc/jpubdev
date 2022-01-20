package xyz.rk0cc.willpub.pubdev.structre.pkg;

import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubspec.data.PubspecSnapshot;

import javax.annotation.Nonnull;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

public record PubPkgInfo(
        @Nonnull String name,
        @Nonnull PubPkgVersion latest,
        @Nonnull List<PubPkgVersion> versions
) {
    public record PubPkgVersion(
            @Nonnull SemVer version,
            @Nonnull PubspecSnapshot latest,
            @Nonnull URL archiveURL,
            @Nonnull ZonedDateTime published
    ) {}
}
