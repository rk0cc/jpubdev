package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import xyz.rk0cc.willpub.pubdev.structre.pkg.PubPkgOptions;

import javax.annotation.Nonnull;
import java.io.IOException;

public final class PubPkgOptionsDeserializer extends PubJacksonDeserializer<PubPkgOptions> {
    public PubPkgOptionsDeserializer() {
        super();
    }

    public PubPkgOptionsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgOptions deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode replace = node.get("replacedBy");

        return new PubPkgOptions(
                node.get("isDiscontinued").booleanValue(),
                replace.isNull() ? null : replace.textValue(),
                node.get("isUnlisted").booleanValue()
        );
    }
}
