package xyz.rk0cc.willpub.pubdev.structre.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgPublisherDeserializer;

import javax.annotation.Nullable;

/**
 * Get an information of package's publisher with {@link #publisherId() ID} if applied.
 *
 * @since 1.0.0
 */
@JsonDeserialize(using = PubPkgPublisherDeserializer.class)
public record PubPkgPublisher(@Nullable String publisherId) {}
