package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.*;
import xyz.rk0cc.josev.NonStandardSemVerException;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubdev.structure.PubPkgDoc;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Deserialize context of {@link PubPkgDoc}.
 *
 * @since 1.0.0
 */
public final class PubPkgDocDeserializer extends PubJacksonDeserializer<PubPkgDoc> {
    /**
     * Create new deserializer without specific class.
     */
    public PubPkgDocDeserializer() {
        super();
    }

    /**
     * Create new deserializer.
     *
     * @param vc Specify the target {@link Class}.
     */
    @Deprecated(since = "Redundant constructor, providing class is not required")
    public PubPkgDocDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgDoc deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {
        ArrayNode vers = (ArrayNode) node.get("versions");
        ArrayList<PubPkgDoc.PubPkgDocVersionInfo> versionInfos = new ArrayList<>();

        for (JsonNode ppdvi : vers)
            versionInfos.add(deserializeInfo((ObjectNode) ppdvi));

        try {
            return new PubPkgDoc(
                    node.get("name").textValue(),
                    SemVer.parse(node.get("latestStableVersion").textValue()),
                    Collections.unmodifiableList(versionInfos)
            );
        } catch (NonStandardSemVerException e) {
            throw new IOException(e);
        }
    }

    private static PubPkgDoc.PubPkgDocVersionInfo deserializeInfo(@Nonnull ObjectNode node)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new SimpleModule().addDeserializer(
                        PubPkgDoc.PubPkgDocVersionInfo.class,
                        new PubPkgDocVersionInfoDeserializer()
                )
        );

        return mapper.treeToValue(node, PubPkgDoc.PubPkgDocVersionInfo.class);
    }
}

final class PubPkgDocVersionInfoDeserializer extends PubJacksonDeserializer<PubPkgDoc.PubPkgDocVersionInfo> {
    public PubPkgDocVersionInfoDeserializer() {
        super();
    }

    public PubPkgDocVersionInfoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgDoc.PubPkgDocVersionInfo deserializeNode(
            @Nonnull ObjectNode node,
            DeserializationContext deserializationContext
    ) throws IOException {
        try {
            return new PubPkgDoc.PubPkgDocVersionInfo(
                    SemVer.parse(node.get("version").textValue()),
                    node.get("status").textValue(),
                    node.get("hasDocumentation").booleanValue()
            );
        } catch (NonStandardSemVerException e) {
            throw new IOException(e);
        }
    }
}
