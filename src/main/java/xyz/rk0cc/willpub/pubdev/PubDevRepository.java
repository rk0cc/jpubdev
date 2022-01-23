package xyz.rk0cc.willpub.pubdev;

import xyz.rk0cc.willpub.pubspec.data.PubspecValueValidator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.net.URI;
import java.util.*;

/**
 * Locate the server of repository uses for fetching data in {@link xyz.rk0cc.willpub.pubdev}.
 * <br/>
 * It's referencing Dart package namely <a href="https://pub.dev/packages/pub_api_client">pub_api_client</a>. But
 * dropped authorization related action which means it perform GET request only.
 *
 * @since 1.0.0
 */
public record PubDevRepository(@Nonnull URI pubRoot) {
    /**
     * Get root location of API.
     *
     * @return An {@link URI} located to pub API page.
     */
    @Nonnull
    public URI apiRoot() {
        return URI.create(pubRoot + "/api");
    }

    /**
     * Get the search result from query.
     *
     * @param query Searching query.
     * @param page Page number of the search result.
     * @param ordering Specify ordering result.
     *
     * @return An {@link URI} of following search API.
     */
    @Nonnull
    public URI search(@Nonnull String query, @Nonnegative long page, @Nonnull SearchOrdering ordering) {
        /*
            By default, no 'sort' param needed if return result with default sorting.
            But actually work as TOP from SearchOrdering.
        */
        return URI.create(apiRoot() + "/search?q=" + query + "&page=" + page + "&sort=" + ordering.name().toLowerCase());
    }

    /**
     * Get the first page of the search result from query.
     *
     * @param query Searching query.
     * @param ordering Specify ordering result.
     *
     * @return An {@link URI} of following search API.
     */
    @Nonnull
    public URI search(@Nonnull String query, @Nonnull SearchOrdering ordering) {
        return search(query, 1, ordering);
    }

    /**
     * Get the search result from query with using {@link SearchOrdering#TOP} as default order.
     *
     * @param query Searching query.
     * @param page Page number of the search result.
     *
     * @return An {@link URI} of following search API.
     */
    @Nonnull
    public URI search(@Nonnull String query, @Nonnegative long page) {
        return search(query, page, SearchOrdering.TOP);
    }

    /**
     * Get the <b>first page</b> of the search result from query with using {@link SearchOrdering#TOP} as default order.
     *
     * @param query Searching query.
     *
     * @return An {@link URI} of following search API.
     */
    @Nonnull
    public URI search(@Nonnull String query) {
        return search(query, SearchOrdering.TOP);
    }

    /**
     * Generate a {@link PubDevPackageResultRepository} that contains all needs to fetch package information.
     * <br/>
     * The package name must be followed by {@link PubspecValueValidator#packageNaming(String) naming scheme}. And
     * it can not confirm the package is hosted to the {@link #pubRoot() pub server} already which will throw
     * {@link xyz.rk0cc.willpub.exceptions.pubdev.PubPkgErrorReportedException} as {@link java.io.IOException}.
     *
     * @param packageName Package name that decide to search.
     *
     * @return A {@link PubDevPackageResultRepository repository} for fetching package information.
     */
    @Nonnull
    public PubDevPackageResultRepository fetchPackage(@Nonnull String packageName) {
        assert PubspecValueValidator.packageNaming(packageName); // The package must be follow naming scheme.
        return new PubDevPackageResultRepository(this, packageName);
    }

    /**
     * Get an "instance" of {@link PubDevRepository} {@link Record} by inspecting <code>PUB_HOSTED_URL</code>.
     * <br/>
     * <code>PUB_HOSTED_URL</code> is an environment variable to override existing pub repository from
     * <a href="https://pub.dev"><code>pub.dev</code></a> to another due to inaccessibility of the URL.
     *
     * @return {@link PubDevRepository} with applied URL from <code>PUB_HOSTED_URL</code>.
     */
    @Nonnull
    public static PubDevRepository getInstance() {
        // Variable of pub repository when fetching pub.
        String defaultPubRepo = System.getenv("PUB_HOSTED_URL");
        return new PubDevRepository(URI.create(defaultPubRepo == null ? "https://pub.dev" : defaultPubRepo));
    }

    /**
     * A repository that fetching package information under {@link PubDevRepository}.
     *
     * @since 1.0.0
     */
    @SuppressWarnings("ClassCanBeRecord")
    public static final class PubDevPackageResultRepository {
        private final PubDevRepository repository;
        private final String packageName;

        /**
         * Construct information of the package result repository.
         *
         * @param repository Applied repository.
         * @param packageName Specify the package name decide to make request.
         */
        private PubDevPackageResultRepository(@Nonnull PubDevRepository repository, @Nonnull String packageName) {
            this.repository = repository;
            this.packageName = packageName;
        }

        /**
         * Repository currently uses to generate this repository.
         *
         * @return {@link PubDevRepository} which constructed this.
         */
        @Nonnull
        PubDevRepository repository() {
            return repository;
        }

        /**
         * Package name currently uses to fetch package information.
         *
         * @return Package name.
         */
        @Nonnull
        public String packageName() {
            return packageName;
        }

        /**
         * Giving an {@link URI} to generate package information and additional data under this path.
         *
         * @return Package information REST URL.
         */
        @Nonnull
        public URI info() {
            return URI.create(repository.apiRoot() + "/packages/" + packageName);
        }

        /**
         * Get a link of package's score which as a summary analysis result of {@link #metrics()}.
         *
         * @return URL of package's score URL.
         */
        @Nonnull
        public URI score() {
            return URI.create(info() + "/score");
        }

        /**
         * Get completed result of package analysis link.
         *
         * @return URL of the package metrics.
         */
        @Nonnull
        public URI metrics() {
            return URI.create(info() + "/metrics");
        }

        /**
         * Get a link of package option currently applied.
         *
         * @return URL of the package option.
         */
        @Nonnull
        public URI options() {
            return URI.create(info() + "/options");
        }

        /**
         * Get a link of package's publisher information.
         *
         * @return URL of package publisher.
         */
        @Nonnull
        public URI publisher() {
            return URI.create(info() + "/publisher");
        }

        /**
         * Get a link of documentation status of this package.
         *
         * @return URL of documentation of this package.
         */
        @Nonnull
        public URI docs() {
            return URI.create(repository.apiRoot() + "/documentation/" + packageName);
        }
    }

    /**
     * Define search result ordering when perform {@link #search(String, long, SearchOrdering) searching}.
     *
     * @since 1.0.0
     */
    public enum SearchOrdering {
        /**
         * Order result by overall score package.
         */
        TOP,
        /**
         * Order result by the text as relative as possible.
         */
        TEXT,
        /**
         * Order the package's first published date from the newest to oldest.
         */
        CREATED,
        /**
         * Order the package's recent update published date from the newest to oldest.
         */
        UPDATED,
        /**
         * Order the package's popularity from high to low.
         */
        POPULARITY,
        /**
         * Order the package's like count.
         */
        LIKE,
        /**
         * Order the package's pub point.
         */
        POINTS;

        /**
         * Resolving {@link Enum} value of {@link SearchOrdering} by {@link String} with case ignore.
         *
         * @param ordering Applied ordering in {@link String}.
         *
         * @return {@link SearchOrdering} refer to the ordering.
         */
        @Nonnull
        public static SearchOrdering parse(@Nonnull String ordering) {
            Optional<SearchOrdering> optSO = Arrays.stream(SearchOrdering.values())
                    // Ignore case since query param is lowercase
                    .filter(so -> so.name().equalsIgnoreCase(ordering))
                    .findFirst();

            assert optSO.isPresent();

            return optSO.get();
        }
    }
}