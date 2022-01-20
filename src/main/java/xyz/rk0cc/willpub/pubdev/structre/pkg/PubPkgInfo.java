package xyz.rk0cc.willpub.pubdev.structre.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgInfoDeserializer;
import xyz.rk0cc.willpub.pubspec.data.PubspecSnapshot;

import javax.annotation.Nonnull;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

@JsonDeserialize(using = PubPkgInfoDeserializer.class)
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
