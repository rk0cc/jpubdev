package xyz.rk0cc.willpub.pubdev.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.*;
import xyz.rk0cc.josev.NonStandardSemVerException;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.exceptions.pubdev.MetricsPendingAnalysisException;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPkgMetrics;
import xyz.rk0cc.willpub.pubdev.structure.pkg.PubPointEntity;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Deserialize context of {@link PubPkgMetrics}.
 *
 * @since 1.0.0
 */
public final class PubPkgMetricsDeserializer extends PubJacksonDeserializer<PubPkgMetrics> {
    /**
     * Create new deserializer without specific class apply.
     */
    public PubPkgMetricsDeserializer() {
        super();
    }

    /**
     * Create new deserializer.
     *
     * @param vc Specify the target {@link Class}.
     */
    @Deprecated(since = "Redundant constructor, providing class is not required")
    public PubPkgMetricsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgMetrics deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {
        if (!node.has("scorecard")) throw new MetricsPendingAnalysisException();

        ObjectNode scoreCard = (ObjectNode) node.get("scorecard");

        ArrayList<String> f = new ArrayList<>();
        ArrayNode flags = (ArrayNode) scoreCard.get("flags");
        for (JsonNode fi : flags)
            f.add(fi.textValue());

        JsonNode dartdoc = scoreCard.get("dartdocReport"), pana = scoreCard.get("panaReport");

        try {
            return new PubPkgMetrics(
                    scoreCard.get("packageName").textValue(),
                    SemVer.parse(scoreCard.get("packageVersion").textValue()),
                    scoreCard.get("runtimeVersion").textValue(),
                    ZonedDateTime.parse(scoreCard.get("updated").textValue()),
                    ZonedDateTime.parse(scoreCard.get("packageCreated").textValue()),
                    ZonedDateTime.parse(scoreCard.get("packageVersionCreated").textValue()),
                    Collections.unmodifiableList(f),
                    dartdoc.isNull() ? null : deserializeDartDocReport((ObjectNode) dartdoc),
                    pana.isNull() ? null : deserializePanaReport((ObjectNode) pana)
            );
        } catch (NonStandardSemVerException e) {
            throw new IOException(e);
        }
    }

    private static PubPkgMetrics.DartDocReport deserializeDartDocReport(@Nonnull ObjectNode node)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new SimpleModule().addDeserializer(
                        PubPkgMetrics.DartDocReport.class,
                        new PubPkgDartDocReportDeserializer()
                )
        );

        return mapper.treeToValue(node, PubPkgMetrics.DartDocReport.class);
    }

    private static PubPkgMetrics.PanaReport deserializePanaReport(@Nonnull ObjectNode node)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new SimpleModule().addDeserializer(
                        PubPkgMetrics.PanaReport.class,
                        new PubPkgPanaReportDeserializer()
                )
        );

        return mapper.treeToValue(node, PubPkgMetrics.PanaReport.class);
    }
}

final class PubPkgDartDocReportDeserializer extends PubJacksonDeserializer<PubPkgMetrics.DartDocReport> {
    public PubPkgDartDocReportDeserializer() {
        super();
    }

    public PubPkgDartDocReportDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgMetrics.DartDocReport deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode dde = node.get("dartdocEntry");

        return new PubPkgMetrics.DartDocReport(
                ZonedDateTime.parse(node.get("timestamp").textValue()),
                node.get("reportStatus").textValue(),
                dde != null && dde.isObject()
                        ? resolveDartDocEntry((ObjectNode) dde)
                        : null
        );
    }

    private static PubPkgMetrics.DartDocReport.DartDocEntry resolveDartDocEntry(@Nonnull ObjectNode node)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new SimpleModule().addDeserializer(
                        PubPkgMetrics.DartDocReport.DartDocEntry.class,
                        new PubPkgDartDocEntryDeserializer()
                )
        );

        return mapper.treeToValue(node, PubPkgMetrics.DartDocReport.DartDocEntry.class);
    }
}

final class PubPkgDartDocEntryDeserializer extends PubJacksonDeserializer<PubPkgMetrics.DartDocReport.DartDocEntry> {
    public PubPkgDartDocEntryDeserializer() {
        super();
    }

    public PubPkgDartDocEntryDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgMetrics.DartDocReport.DartDocEntry deserializeNode(
            @Nonnull ObjectNode node,
            DeserializationContext deserializationContext
    ) throws IOException {
        try {
            return new PubPkgMetrics.DartDocReport.DartDocEntry(
                    node.get("isLatest").booleanValue(),
                    node.get("isObsolete").booleanValue(),
                    node.get("usesFlutter").booleanValue(),
                    SemVer.parse(node.get("sdkVersion").textValue()),
                    SemVer.parse(node.get("dartdocVersion").textValue()),
                    SemVer.parse(node.get("flutterVersion").textValue()),
                    ZonedDateTime.parse(node.get("timestamp").textValue()),
                    node.get("runDuration").longValue(),
                    node.get("depsResolved").booleanValue(),
                    node.get("hasContent").booleanValue(),
                    node.get("archiveSize").longValue(),
                    node.get("totalSize").longValue(),
                    node.get("blobSize").longValue(),
                    node.get("blobIndexSize").longValue()
            );
        } catch (NonStandardSemVerException e) {
            throw new IOException(e);
        }
    }
}

final class PubPkgPanaReportDeserializer extends PubJacksonDeserializer<PubPkgMetrics.PanaReport> {
    public PubPkgPanaReportDeserializer() {
        super();
    }

    public PubPkgPanaReportDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgMetrics.PanaReport deserializeNode(@Nonnull ObjectNode node, DeserializationContext deserializationContext)
            throws IOException {
        String s = node.get("reportStatus").textValue();
        boolean reportSuccess = s.equals("success");
        ObjectNode entry = node.deepCopy();

        /*
            Actually, it does not contain "entry" field for pana.

            But these field will not exist when report status is not 'success', so I create a nested class that allowing
            return null if report unsuccessfully.
        */
        if (reportSuccess) {
            entry.remove("reportStatus");
            entry.remove("timestamp");
        }

        return new PubPkgMetrics.PanaReport(
                ZonedDateTime.parse(node.get("timestamp").textValue()),
                s,
                reportSuccess ? resolvePanaEntry(entry) : null
        );
    }

    private static PubPkgMetrics.PanaReport.PanaEntry resolvePanaEntry(@Nonnull ObjectNode node)
        throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new SimpleModule().addDeserializer(
                        PubPkgMetrics.PanaReport.PanaEntry.class,
                        new PubPkgPanaEntryDeserializer()
                )
        );

        return mapper.treeToValue(node, PubPkgMetrics.PanaReport.PanaEntry.class);
    }
}

final class PubPkgPanaEntryDeserializer extends PubJacksonDeserializer<PubPkgMetrics.PanaReport.PanaEntry> {
    public PubPkgPanaEntryDeserializer() {
        super();
    }

    public PubPkgPanaEntryDeserializer(Class<?> vc) {
        super(vc);
    }

    @Nonnull
    @Override
    PubPkgMetrics.PanaReport.PanaEntry deserializeNode(
            @Nonnull ObjectNode node,
            DeserializationContext deserializationContext
    ) throws IOException {
        ArrayList<String> dt = new ArrayList<>();
        ArrayNode derivedTags = (ArrayNode) node.get("derivedTags");
        for (JsonNode dti : derivedTags)
            dt.add(dti.textValue());

        ArrayList<String> allDeps = new ArrayList<>();
        ArrayNode allDependencies = (ArrayNode) node.get("allDependencies");
        for (JsonNode aldi : allDependencies)
            allDeps.add(aldi.textValue());

        String license = "No license information provided";
        ObjectNode licenseFile = (ObjectNode) node.get("licenseFile");
        if (licenseFile != null && licenseFile.isObject()) {
            TextNode ln = (TextNode) licenseFile.get("name");
            if (ln != null && ln.isTextual()) license = ln.textValue();
        }

        ArrayList<PubPointEntity.DetailedPubPointEntity> dppe = new ArrayList<>();
        ArrayNode reportSec = (ArrayNode) node.get("report").get("sections");
        for (JsonNode rs : reportSec)
            dppe.add(new ObjectMapper().treeToValue(rs, PubPointEntity.DetailedPubPointEntity.class));

        try {
            return new PubPkgMetrics.PanaReport.PanaEntry(
                    SemVer.parse(node.get("panaRuntimeInfo").get("panaVersion").textValue()),
                    SemVer.parse(node.get("panaRuntimeInfo").get("sdkVersion").textValue()),
                    SemVer.parse(node.get("panaRuntimeInfo").get("flutterVersions").get("frameworkVersion").textValue()),
                    PubPkgMetrics.PanaReport.PanaEntry.DerivedTags.parseTagsList(dt),
                    Collections.unmodifiableList(allDeps),
                    license,
                    Collections.unmodifiableList(dppe)
            );
        } catch (NonStandardSemVerException e) {
            throw new IOException(e);
        }
    }
}
