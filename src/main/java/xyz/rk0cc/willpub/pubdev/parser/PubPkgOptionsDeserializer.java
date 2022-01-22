package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgOptions;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Deserialize handler for {@link PubPkgOptions}.
 *
 * @since 1.0.0
 */
public final class PubPkgOptionsDeserializer extends PubJacksonDeserializer<PubPkgOptions> {
    /**
     * Create new deserializer with specific class apply.
     */
    public PubPkgOptionsDeserializer() {
        super();
    }

    /**
     * Create new deserializer.
     *
     * @param vc Specify the target {@link Class}.
     */
    @Deprecated(since = "Redundant constructor, providing class is not required")
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
