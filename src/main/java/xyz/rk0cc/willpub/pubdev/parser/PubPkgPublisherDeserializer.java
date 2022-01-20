package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import xyz.rk0cc.willpub.pubdev.structre.pkg.PubPkgPublisher;

import javax.annotation.Nonnull;
import java.io.IOException;

public final class PubPkgPublisherDeserializer extends PubJacksonDeserializer<PubPkgPublisher> {
    public PubPkgPublisherDeserializer() {
        super();
    }

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
