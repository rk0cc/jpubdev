package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import xyz.rk0cc.willpub.exceptions.pubdev.PubPkgErrorReportedException;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Handling all deserialization of REST response from pub repository server.
 *
 * @param <T> Deserialized object type.
 *
 * @since 1.0.0
 */
public abstract class PubJacksonDeserializer<T> extends StdDeserializer<T> {
    /**
     * Create new deserializer with no {@link Class} applied.
     */
    PubJacksonDeserializer() {
        this(null);
    }

    /**
     * Create new deserializer.
     *
     * @param vc Specify the target {@link Class}.
     *
     * @deprecated Just use {@link PubJacksonDeserializer#PubJacksonDeserializer()} can be worked.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated(since = "Redundant constructor, providing class is not required")
    PubJacksonDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Deserialize node context to {@link T} when no error returned from
     * {@link #deserialize(JsonParser, DeserializationContext)}.
     *
     * @param node Object of REST response.
     * @param deserializationContext Context that can be used to access information about this deserialization activity.
     *
     * @return Resolved context.
     *
     * @throws IOException Some data resolve failed during conversion.
     */
    @Nonnull
    abstract T deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException;

    /**
     * Deserialize context of the {@link T targeted class}.
     *
     * @param jsonParser Parser of
     * @param deserializationContext Context that can be used to access information about this deserialization activity.
     *
     * @return Deserialized object from JSON.
     *
     * @throws IOException If REST returned {@link PubPkgErrorReportedException error} or having issue during data
     *                     conversion.
     */
    @Nonnull
    @Override
    public final T deserialize(@Nonnull JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        ObjectNode objectNode = jsonParser.getCodec().readTree(jsonParser);

        if (objectNode.has("error")) throw new PubPkgErrorReportedException(objectNode);

        return deserializeNode(objectNode, deserializationContext);
    }
}
