package xyz.rk0cc.willpub.pubdev.structre;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgDocDeserializer;

import javax.annotation.Nonnull;
import java.util.List;

@JsonDeserialize(using = PubPkgDocDeserializer.class)
public record PubPkgDoc(
        @Nonnull String packageName,
        @Nonnull SemVer latestStableVersion,
        @Nonnull List<PubPkgDocVersionInfo> versions
) {
    public record PubPkgDocVersionInfo(@Nonnull SemVer version, @Nonnull String status, boolean hasDoc) { }
}
