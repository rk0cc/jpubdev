package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.*;
import xyz.rk0cc.josev.NonStandardSemVerException;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgInfo;
import xyz.rk0cc.willpub.pubspec.data.Pubspec;
import xyz.rk0cc.willpub.pubspec.data.PubspecSnapshot;
import xyz.rk0cc.willpub.pubspec.parser.PubspecParser;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

public final class PubPkgInfoDeserializer extends PubJacksonDeserializer<PubPkgInfo> {
    public PubPkgInfoDeserializer() {
        super();
    }

    public PubPkgInfoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgInfo deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {
        ArrayNode allVer = (ArrayNode) node.get("versions");

        ArrayList<PubPkgInfo.PubPkgVersion> publishedVersions = new ArrayList<>();

        for (JsonNode sv : allVer)
            publishedVersions.add(deserializeVersion((ObjectNode) sv));

        return new PubPkgInfo(
                node.get("name").textValue(),
                deserializeVersion((ObjectNode) node.get("latest")),
                Collections.unmodifiableList(publishedVersions)
        );
    }


    private static PubPkgInfo.PubPkgVersion deserializeVersion(@Nonnull ObjectNode versionInfo)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new SimpleModule().addDeserializer(PubPkgInfo.PubPkgVersion.class, new PubPkgVersionDeserializer())
        );

        return mapper.treeToValue(versionInfo, PubPkgInfo.PubPkgVersion.class);
    }
}

final class PubPkgVersionDeserializer extends PubJacksonDeserializer<PubPkgInfo.PubPkgVersion> {
    PubPkgVersionDeserializer() {
        super();
    }

    PubPkgVersionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgInfo.PubPkgVersion deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {

       try {
           return new PubPkgInfo.PubPkgVersion(
                   SemVer.parse(node.get("version").textValue()),
                   PubspecSnapshot.getSnapshotOfCurrentPubspec(
                           PubspecParser.pubspecJsonMapper().treeToValue(node.get("pubspec"), Pubspec.class)
                   ),
                   new URL(node.get("archive_url").textValue()),
                   ZonedDateTime.parse(node.get("published").textValue())
           );
       } catch (MalformedURLException | NonStandardSemVerException e) {
           throw new IOException(e);
       }
    }


}
