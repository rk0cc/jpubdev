package xyz.rk0cc.willpub.pubdev.structre.pkg;

import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubspec.data.PubspecSnapshot;

import javax.annotation.Nonnull;
import java.net.URL;
import java.time.ZonedDateTime;

public record PubPkgVersion(
        @Nonnull SemVer version,
        @Nonnull PubspecSnapshot latest,
        @Nonnull URL archiveURL,
        @Nonnull ZonedDateTime published
) { }
