package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.*;
import xyz.rk0cc.willpub.pubdev.structre.PubSearchResult.PubSearchContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public final class PubSearchContextDeserializer extends StdDeserializer<PubSearchContext> {
    public PubSearchContextDeserializer() {
        this(null);
    }

    public PubSearchContextDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    public PubSearchContext deserialize(@Nonnull JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        ObjectNode pubSearch = jsonParser.getCodec().readTree(jsonParser);
        ArrayNode pkgs = (ArrayNode) pubSearch.get("packages");
        ArrayList<String> searchedPkg = new ArrayList<>();

        for (JsonNode pkgNode : pkgs)
            searchedPkg.add(pkgNode.get("package").textValue());

        return new PubSearchContext(Collections.unmodifiableList(searchedPkg), pubSearch.has("next"));
    }
}
