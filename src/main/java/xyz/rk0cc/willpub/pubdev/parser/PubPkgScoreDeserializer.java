package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgScore;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.ZonedDateTime;

public final class PubPkgScoreDeserializer extends PubJacksonDeserializer<PubPkgScore> {
    public PubPkgScoreDeserializer() {
        super();
    }

    public PubPkgScoreDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgScore deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {
        return new PubPkgScore(
                node.get("grantedPoints").intValue(),
                node.get("maxPoints").intValue(),
                node.get("likeCount").longValue(),
                node.get("popularityScore").doubleValue(),
                ZonedDateTime.parse(node.get("lastUpdated").textValue())
        );
    }

}
