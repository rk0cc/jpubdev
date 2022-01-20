package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import xyz.rk0cc.willpub.exceptions.pubdev.PubPkgErrorReportedException;

import javax.annotation.Nonnull;
import java.io.IOException;

abstract class PubJacksonDeserializer<T> extends StdDeserializer<T> {
    public PubJacksonDeserializer() {
        this(null);
    }

    public PubJacksonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    abstract T deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException;

    @Nonnull
    @Override
    public final T deserialize(@Nonnull JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        ObjectNode objectNode = jsonParser.getCodec().readTree(jsonParser);

        if (objectNode.has("error")) throw new PubPkgErrorReportedException(objectNode);

        return deserializeNode(objectNode, deserializationContext);
    }
}
