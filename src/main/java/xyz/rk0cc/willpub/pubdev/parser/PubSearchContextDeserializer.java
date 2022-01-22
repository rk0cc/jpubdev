package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import xyz.rk0cc.willpub.pubdev.structure.PubSearchResult.PubSearchContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Deserialize handler for {@link PubSearchContext}.
 *
 * @since 1.0.0
 */
public final class PubSearchContextDeserializer extends PubJacksonDeserializer<PubSearchContext> {
    /**
     * Create new deserializer without class applied.
     */
    public PubSearchContextDeserializer() {
        super();
    }

    /**
     * Create new deserializer.
     *
     * @param vc Specify the target {@link Class}.
     */
    @Deprecated(since = "Redundant constructor, providing class is not required")
    public PubSearchContextDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubSearchContext deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {
        ArrayNode pkgs = (ArrayNode) node.get("packages");
        ArrayList<String> searchedPkg = new ArrayList<>();

        for (JsonNode pkgNode : pkgs)
            searchedPkg.add(pkgNode.get("package").textValue());

        return new PubSearchContext(Collections.unmodifiableList(searchedPkg), node.has("next"));
    }
}
