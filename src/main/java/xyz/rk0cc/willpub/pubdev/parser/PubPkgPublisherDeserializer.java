package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgPublisher;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Deserialize handler for {@link PubPkgPublisher}.
 *
 * @since 1.0.0
 */
public final class PubPkgPublisherDeserializer extends PubJacksonDeserializer<PubPkgPublisher> {
    /**
     * Create new deserializer without specific class applied.
     */
    public PubPkgPublisherDeserializer() {
        super();
    }

    /**
     * Create new deserializer.
     *
     * @param vc Specify the target {@link Class}.
     */
    @Deprecated(since = "Redundant constructor, providing class is not required")
    public PubPkgPublisherDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgPublisher deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode pubId = node.get("publisherId");
        return new PubPkgPublisher(pubId.isNull() ? null : pubId.textValue());
    }
}
