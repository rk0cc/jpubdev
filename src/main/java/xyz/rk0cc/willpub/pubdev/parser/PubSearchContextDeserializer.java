package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import xyz.rk0cc.willpub.pubdev.structure.PubSearchResult.PubSearchContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public final class PubSearchContextDeserializer extends PubJacksonDeserializer<PubSearchContext> {
    public PubSearchContextDeserializer() {
        super();
    }

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
