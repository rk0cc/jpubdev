package xyz.rk0cc.willpub.pubdev.structre;

import xyz.rk0cc.josev.SemVer;

import javax.annotation.Nonnull;
import java.util.List;

public record PubPkgDoc(
        @Nonnull String packageName,
        @Nonnull SemVer latestStableVersion,
        @Nonnull List<PubPkgDocVersionInfo> versions
) {
    public record PubPkgDocVersionInfo(@Nonnull SemVer version, @Nonnull String status, boolean hasDoc) { }
}
