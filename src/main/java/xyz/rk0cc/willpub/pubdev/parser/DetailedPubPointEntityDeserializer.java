package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPointEntity;

import javax.annotation.Nonnull;
import java.io.IOException;

public final class DetailedPubPointEntityDeserializer
        extends PubJacksonDeserializer<PubPointEntity.DetailedPubPointEntity> {
    public DetailedPubPointEntityDeserializer() {
        super();
    }

    public DetailedPubPointEntityDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPointEntity.DetailedPubPointEntity deserializeNode(
            @Nonnull ObjectNode node,
            DeserializationContext deserializationContext
    ) throws IOException {
        return new PubPointEntity.DetailedPubPointEntity(
                node.get("id").textValue(),
                node.get("title").textValue(),
                node.get("grantedPoints").intValue(),
                node.get("maxPoints").intValue(),
                PubPointEntity.DetailedPubPointEntity.PubPointStatus.valueOf(
                        PubPointEntity.DetailedPubPointEntity.PubPointStatus.class,
                        node.get("status").textValue().toUpperCase()
                )
        );
    }
}
