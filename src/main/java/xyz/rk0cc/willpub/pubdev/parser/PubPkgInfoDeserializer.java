package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.*;
import xyz.rk0cc.josev.NonStandardSemVerException;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubdev.structre.pkg.PubPkgInfo;
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

public final class PubPkgInfoDeserializer extends StdDeserializer<PubPkgInfo> {
    public PubPkgInfoDeserializer() {
        this(null);
    }

    public PubPkgInfoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    public PubPkgInfo deserialize(@Nonnull JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        ObjectNode info = jsonParser.getCodec().readTree(jsonParser);
        String name = info.get("name").textValue();

        PubPkgInfo.PubPkgVersion latest = deserializeVersion((ObjectNode) info.get("latest"));

        ArrayNode allVer = (ArrayNode) info.get("versions");

        ArrayList<PubPkgInfo.PubPkgVersion> publishedVersions = new ArrayList<>();

        for (JsonNode sv : allVer)
            publishedVersions.add(deserializeVersion((ObjectNode) sv));

        return new PubPkgInfo(name, latest, Collections.unmodifiableList(publishedVersions));
    }

    private static PubPkgInfo.PubPkgVersion deserializeVersion(@Nonnull ObjectNode versionInfo)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new SimpleModule().addDeserializer(PubPkgInfo.PubPkgVersion.class, new PubPkgVersionDeserializer())
        );

        return mapper.treeToValue(versionInfo, PubPkgInfo.PubPkgVersion.class);
    }
}

final class PubPkgVersionDeserializer extends StdDeserializer<PubPkgInfo.PubPkgVersion> {
    PubPkgVersionDeserializer() {
        this(null);
    }

    PubPkgVersionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    public PubPkgInfo.PubPkgVersion deserialize(
            @Nonnull JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException, JacksonException {
        ObjectNode verInfo = jsonParser.getCodec().readTree(jsonParser);
        SemVer version;

        try {
            version = SemVer.parse(verInfo.get("version").textValue());
        } catch (NonStandardSemVerException e) {
            throw new IOException(e);
        }

        PubspecSnapshot pubspec = PubspecSnapshot.getSnapshotOfCurrentPubspec(
                PubspecParser.pubspecJsonMapper().treeToValue(verInfo.get("pubspec"), Pubspec.class)
        );

        URL archive;

        try {
            archive = new URL(verInfo.get("archive_url").textValue());
        } catch (MalformedURLException e) {
            throw new IOException(e);
        }

        ZonedDateTime publishedAt = ZonedDateTime.parse(verInfo.get("published").textValue());

        return new PubPkgInfo.PubPkgVersion(version, pubspec, archive, publishedAt);
    }
}
