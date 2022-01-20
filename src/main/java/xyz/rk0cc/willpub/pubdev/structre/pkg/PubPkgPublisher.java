package xyz.rk0cc.willpub.pubdev.structre.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgPublisherDeserializer;

import javax.annotation.Nullable;

@JsonDeserialize(using = PubPkgPublisherDeserializer.class)
public record PubPkgPublisher(@Nullable String publisherId) {}
