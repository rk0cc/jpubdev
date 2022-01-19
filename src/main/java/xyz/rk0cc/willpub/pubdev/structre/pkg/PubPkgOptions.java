package xyz.rk0cc.willpub.pubdev.structre.pkg;

import javax.annotation.Nullable;

public record PubPkgOptions(boolean discontinues, @Nullable String replacedBy, boolean unlisted) {}
