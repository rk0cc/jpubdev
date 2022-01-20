package xyz.rk0cc.willpub.pubdev.structre.pkg;

import org.apache.commons.io.FileUtils;
import xyz.rk0cc.josev.SemVer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public interface MetricsReport<E extends MetricsReportEntry> {
        @Nonnull
        ZonedDateTime reportTimestamp();

        @Nonnull
        String status();

        @Nullable
        E entry();
    }

    public interface MetricsReportEntry {
        @Nonnull
        SemVer sdkVersion();

        @Nonnull
        SemVer flutterVersion();
    }

    public record DartDocReport(
            @Nonnull ZonedDateTime reportTimestamp,
            @Nonnull String status,
            @Nullable DartDocEntry entry
    ) implements MetricsReport<DartDocReport.DartDocEntry> {
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
            @Nonnull
            public String archiveSizeDisplay() {
                return FileUtils.byteCountToDisplaySize(archiveSize);
            }

            @Nonnull
            public String totalSizeDisplay() {
                return FileUtils.byteCountToDisplaySize(totalSize);
            }

            @Nonnull
            public String blobSizeDisplay() {
                return FileUtils.byteCountToDisplaySize(blobSize);
            }

            @Nonnull
            public String blobIndexSizeDisplay() {
                return FileUtils.byteCountToDisplaySize(blobIndexSize);
            }
        }
    }

    public record PanaReport(
            @Nonnull ZonedDateTime reportTimestamp,
            @Nonnull String status,
            @Nullable PanaEntry entry
    ) implements MetricsReport<PanaReport.PanaEntry> {

        public record PanaEntry(
                @Nonnull SemVer panaVersion,
                @Nonnull SemVer sdkVersion,
                @Nonnull SemVer flutterVersion,
                @Nonnull Set<DerivedTags<?>> derivedTags,
                @Nonnull List<String> allDependencies,
                @Nonnull String licenseName,
                @Nonnull List<PubPointEntity.DetailedPubPointEntity> reportSections
        ) implements MetricsReportEntry {
            @Nonnull
            public PubPointEntity.DetailedPubPointEntity getSectionById(@Nonnull String id) {
                Optional<PubPointEntity.DetailedPubPointEntity> rso = reportSections.stream()
                        .filter(rsi -> rsi.id().equals(id))
                        .findFirst();

                if (rso.isEmpty()) throw new NullPointerException("'" + id + "' is not defined");

                return rso.get();
            }

            public sealed interface DerivedTags<E extends Enum<? extends DerivedTags<E>>>
                    permits IsSupportedTags, SupportedPlatform, SupportedRuntime, SupportedSDK {
                @Nonnull
                String suffix();

                @Nonnull
                String prefix();

                default String tags() {
                    return suffix() + ':' + prefix();
                }

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

                @Nonnull
                static Set<DerivedTags<? extends Enum<?>>> parseTagsList(@Nonnull List<String> tagsList) {
                    return tagsList.stream()
                            .map(DerivedTags::parseTags)
                            .filter(dte -> !Objects.isNull(dte))
                            .collect(Collectors.toUnmodifiableSet());
                }
            }

            public enum SupportedSDK implements DerivedTags<SupportedSDK> {
                DART,
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
                    return getClass().getName() + '.' + super.toString();
                }
            }

            public enum SupportedPlatform implements DerivedTags<SupportedPlatform> {
                ANDROID,
                IOS,
                FUCHSIA, // Not in the API now but supported for Dart.
                WINDOWS,
                MACOS,
                LINUX,
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
                    return getClass().getName() + '.' + super.toString();
                }
            }

            public enum SupportedRuntime implements DerivedTags<SupportedRuntime> {
                WEB,
                NATIVE_AOT,
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
                    return getClass().getName() + '.' + super.toString();
                }
            }

            public enum IsSupportedTags implements DerivedTags<IsSupportedTags> {
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
                    return getClass().getName() + '.' + super.toString();
                }
            }
        }
    }
}
