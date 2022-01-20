package xyz.rk0cc.willpub.pubdev.structre.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgOptionsDeserializer;

import javax.annotation.Nullable;

@JsonDeserialize(using = PubPkgOptionsDeserializer.class)
public record PubPkgOptions(boolean discontinues, @Nullable String replacedBy, boolean unlisted) {}
