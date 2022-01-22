package xyz.rk0cc.willpub.pubdev.structure.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgOptionsDeserializer;

import javax.annotation.Nullable;

/**
 * List the package {@link #discontinues() is discontinued}, {@link #replacedBy() replaced package (if discontinued)}
 * and {@link #unlisted() hidden when search on pub.dev}.
 *
 * @since 1.0.0
 */
@JsonDeserialize(using = PubPkgOptionsDeserializer.class)
public record PubPkgOptions(boolean discontinues, @Nullable String replacedBy, boolean unlisted) {}
