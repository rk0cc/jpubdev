package xyz.rk0cc.willpub.pubdev.structure.pkg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.io.FileUtils;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.willpub.pubdev.parser.PubPkgMetricsDeserializer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Analysing package's documentations (<a href="https://pub.dev/packages/dartdoc"><code>dartdoc</code></a>) and
 * validating given metadata files with <a href="https://pub.dev/packages/pana"><code>pana</code></a>.
 *
 * @since 1.0.0
 */
@JsonDeserialize(using = PubPkgMetricsDeserializer.class)
public record PubPkgMetrics(
        @Nonnull String packageName,
        @Nonnull SemVer packageVersion,
        @Nonnull String runtimeVersion,
        @Nonnull ZonedDateTime updated,
        @Nonnull ZonedDateTime packageCreated,
        @Nonnull ZonedDateTime packageVersionCreated,
        @Nonnull List<String> flags,
        @Nullable DartDocReport dartDocReport,
        @Nullable PanaReport panaReport
) {
    private interface MetricsReport {
        @Nonnull
        ZonedDateTime reportTimestamp();

        @Nonnull
        String status();

        @Nullable
        MetricsReportEntry entry();
    }

    private interface MetricsReportEntry {
        @Nonnull
        SemVer sdkVersion();

        @Nonnull
        SemVer flutterVersion();
    }

    /**
     * Analyse report on package's documentation.
     *
     * @since 1.0.0
     */
    public record DartDocReport(
            @Nonnull ZonedDateTime reportTimestamp,
            @Nonnull String status,
            @Nullable DartDocEntry entry
    ) implements MetricsReport {
        /**
         * List analysed result under {@link DartDocReport} if succeeded.
         *
         * @since 1.0.0
         */
        public record DartDocEntry (
                boolean latest,
                boolean obsolete,
                boolean usesFlutter,
                @Nonnull SemVer sdkVersion,
                @Nonnull SemVer dartDocVersion,
                @Nonnull SemVer flutterVersion,
                @Nonnull ZonedDateTime entryTimestamp,
                @Nonnegative long runDuration,
                boolean dependenciesResolved,
                boolean hasContent,
                @Nonnegative long archiveSize,
                @Nonnegative long totalSize,
                @Nonnegative long blobSize,
                @Nonnegative long blobIndexSize
        ) implements MetricsReportEntry {
            /**
             * Display package document's archive size with human-readable {@link String}.
             *
             * @return File size of {@link #archiveSize()} with file size unit.
             */
            @Nonnull
            public String archiveSizeDisplay() {
                return FileUtils.byteCountToDisplaySize(archiveSize);
            }

            /**
             * Display package document's total size with human-readable {@link String}.
             *
             * @return File size of {@link #totalSize()} with file size unit.
             */
            @Nonnull
            public String totalSizeDisplay() {
                return FileUtils.byteCountToDisplaySize(totalSize);
            }

            /**
             * Display package document's BLOB size with human-readable {@link String}.
             *
             * @return File size of {@link #blobSize()} with file size unit.
             */
            @Nonnull
            public String blobSizeDisplay() {
                return FileUtils.byteCountToDisplaySize(blobSize);
            }

            /**
             * Display package document index's BLOB size with human-readable {@link String}.
             *
             * @return File size of {@link #blobIndexSize()} with file size unit.
             */
            @Nonnull
            public String blobIndexSizeDisplay() {
                return FileUtils.byteCountToDisplaySize(blobIndexSize);
            }
        }
    }

    /**
     * Analyse health and quality of the package.
     *
     * @since 1.0.0
     */
    public record PanaReport(
            @Nonnull ZonedDateTime reportTimestamp,
            @Nonnull String status,
            @Nullable PanaEntry entry
    ) implements MetricsReport {
        /**
         * List analysed result under {@link PanaReport} if succeeded.
         *
         * @since 1.0.0
         */
        public record PanaEntry(
                @Nonnull SemVer panaVersion,
                @Nonnull SemVer sdkVersion,
                @Nonnull SemVer flutterVersion,
                @Nonnull Set<DerivedTags<? extends Enum<?>>> derivedTags,
                @Nonnull List<String> allDependencies,
                @Nonnull String licenseName,
                @Nonnull List<PubPointEntity.DetailedPubPointEntity> reportSections
        ) implements MetricsReportEntry {
            /**
             * Get {@link xyz.rk0cc.willpub.pubdev.structure.pkg.PubPointEntity.DetailedPubPointEntity} with
             * given ID.
             *
             * @param id ID name of the entity.
             *
             * @return A point entity of the ID.
             *
             * @throws NullPointerException When given ID does not define in the {@link List}.
             */
            @Nonnull
            public PubPointEntity.DetailedPubPointEntity getSectionById(@Nonnull String id) {
                Optional<PubPointEntity.DetailedPubPointEntity> rso = reportSections.stream()
                        .filter(rsi -> rsi.id().equals(id))
                        .findFirst();

                if (rso.isEmpty()) throw new NullPointerException("'" + id + "' is not defined");

                return rso.get();
            }

            /**
             * An interface that representing implemented {@link Enum} as a value of {@link PanaEntry#derivedTags()}.
             *
             * @param <E> Implemented {@link Enum} itself with {@link DerivedTags}.
             */
            public sealed interface DerivedTags<E extends Enum<? extends DerivedTags<E>>>
                    permits IsSupportedTags, SupportedPlatform, SupportedRuntime, SupportedSDK {
                /**
                 * Tag's group as the suffix of {@link DerivedTags}.
                 *
                 * @return Tag group name.
                 */
                @Nonnull
                String suffix();

                /**
                 * Tag's value as the prefix of {@link DerivedTags}.
                 *
                 * @return Tag's value, which usually {@link Enum#name()} in {@link String#toLowerCase() lower case}.
                 */
                @Nonnull
                String prefix();

                /**
                 * Assemble completed {@link String} of the tag which listed in REST API result.
                 *
                 * @return {@link DerivedTags} in REST API list's value.
                 */
                default String tags() {
                    return suffix() + ':' + prefix();
                }

                /**
                 * Resolve {@link DerivedTags} value by matching {@link #tags()}.
                 *
                 * @param tags Tags value in {@link String}.
                 *
                 * @return {@link Enum} of {@link DerivedTags} with matched {@link String} or <code>null</code> if not
                 *         found.
                 */
                @SuppressWarnings("unchecked")
                @Nullable
                static DerivedTags<? extends Enum<?>> parseTags(@Nonnull String tags) {
                    for (Class<?> dte : DerivedTags.class.getPermittedSubclasses()) {
                        for (
                                DerivedTags<? extends Enum<?>> dteo
                                : ((Class<? extends DerivedTags<? extends Enum<?>>>) dte).getEnumConstants()
                        ) {
                            if (dteo.tags().equals(tags)) return dteo;
                        }
                    }
                    return null;
                }

                /**
                 * Resolve a {@link List} of {@link String} to a {@link Set} of {@link DerivedTags}.
                 *
                 * @param tagsList Tags value.
                 *
                 * @return Resolved tags under a {@link Set}.
                 */
                @Nonnull
                static Set<DerivedTags<? extends Enum<?>>> parseTagsList(@Nonnull List<String> tagsList) {
                    return tagsList.stream()
                            .map(DerivedTags::parseTags)
                            .filter(dte -> !Objects.isNull(dte))
                            .collect(Collectors.toUnmodifiableSet());
                }
            }

            /**
             * A {@link DerivedTags} for listing supported SDK of the package.
             *
             * @since 1.0.0
             */
            public enum SupportedSDK implements DerivedTags<SupportedSDK> {
                /**
                 * Supported Dart.
                 */
                DART,
                /**
                 * Supported Flutter.
                 */
                FLUTTER;

                @Nonnull
                @Override
                public String suffix() {
                    return "sdk";
                }

                @Nonnull
                @Override
                public String prefix() {
                    return name().toLowerCase();
                }

                @Nonnull
                @Override
                public String toString() {
                    return getClass().getSimpleName() + '.' + super.toString();
                }
            }

            /**
             * A {@link DerivedTags} for listing support platform of the package.
             *
             * @since 1.0.0
             */
            public enum SupportedPlatform implements DerivedTags<SupportedPlatform> {
                /**
                 * Support running in Android.
                 */
                ANDROID,
                /**
                 * Support running in iOS.
                 */
                IOS,
                /**
                 * Support running in Fuchsia. (Not in the API result right now).
                 */
                FUCHSIA, // Not in the API now but supported for Dart.
                /**
                 * Support running in Windows.
                 */
                WINDOWS,
                /**
                 * Support running in macOS.
                 */
                MACOS,
                /**
                 * Support running in Linux.
                 */
                LINUX,
                /**
                 * Support running in web under JavaScript.
                 */
                WEB;

                @Nonnull
                @Override
                public String suffix() {
                    return "platform";
                }

                @Nonnull
                @Override
                public String prefix() {
                    return name().toLowerCase();
                }

                @Nonnull
                @Override
                public String toString() {
                    return getClass().getSimpleName() + '.' + super.toString();
                }
            }

            /**
             * A {@link DerivedTags} for listing supported runtime for the package.
             *
             * @since 1.0.0
             */
            public enum SupportedRuntime implements DerivedTags<SupportedRuntime> {
                /**
                 * Support running in web under JavaScript.
                 */
                WEB,
                /**
                 * Support running ahead-of-time in native compiled.
                 */
                NATIVE_AOT,
                /**
                 * Support running just-in-time in native compile.
                 */
                NATIVE_JIT;

                @Nonnull
                @Override
                public String suffix() {
                    return "runtime";
                }

                @Nonnull
                @Override
                public String prefix() {
                    return name().toLowerCase().replace("_", "-");
                }

                @Nonnull
                @Override
                public String toString() {
                    return getClass().getSimpleName() + '.' + super.toString();
                }
            }

            /**
             * A {@link DerivedTags} which has '<code>is</code>' as {@link #prefix()}.
             *
             * @since 1.0.0
             */
            public enum IsSupportedTags implements DerivedTags<IsSupportedTags> {
                /**
                 * The package is null-safety.
                 */
                NULL_SAFE;

                @Nonnull
                @Override
                public String suffix() {
                    return "is";
                }

                @Nonnull
                @Override
                public String prefix() {
                    return name().toLowerCase().replace("_", "-");
                }

                @Nonnull
                @Override
                public String toString() {
                    return getClass().getSimpleName() + '.' + super.toString();
                }
            }
        }
    }
}
