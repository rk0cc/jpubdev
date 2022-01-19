package xyz.rk0cc.willpub.pubdev.structre.pkg;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public record PubPkgInfo(
        @Nonnull String name,
        boolean discontinued,
        @Nullable String replacedBy,
        @Nonnull PubPkgVersion latest,
        @Nonnull List<PubPkgVersion> versions
) {}
