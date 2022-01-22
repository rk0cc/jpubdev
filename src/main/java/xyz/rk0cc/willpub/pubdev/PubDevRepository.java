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
        return URI.create(apiRoot() + "/search?q=" + query + "&page=" + page + "&sort=" + ordering.name().toLowerCase());
    }

    @Nonnull
    public URI search(@Nonnull String query, @Nonnull SearchOrdering ordering) {
        return search(query, 1, ordering);
    }

    @Nonnull
    public URI search(@Nonnull String query, @Nonnegative long page) {
        return search(query, page, SearchOrdering.TOP);
    }

    /**
     * Get the <b>first page</b> of the search result from query.
     *
     * @param query Searching query.
     *
     * @return An {@link URI} of following search API.
     */
    @Nonnull
    public URI search(@Nonnull String query) {
        return search(query, SearchOrdering.TOP);
    }

    @Nonnull
    public PubDevPackageResultRepository fetchPackage(@Nonnull String packageName) {
        assert PubspecValueValidator.packageNaming(packageName);
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
        String defaultPubRepo = System.getenv("PUB_HOSTED_URL");
        return new PubDevRepository(URI.create(defaultPubRepo == null ? "https://pub.dev" : defaultPubRepo));
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static final class PubDevPackageResultRepository {
        private final PubDevRepository repository;
        private final String packageName;

        private PubDevPackageResultRepository(@Nonnull PubDevRepository repository, @Nonnull String packageName) {
            this.repository = repository;
            this.packageName = packageName;
        }

        @Nonnull
        public String packageName() {
            return packageName;
        }

        @Nonnull
        public URI info() {
            return URI.create(repository.apiRoot() + "/packages/" + packageName);
        }

        @Nonnull
        public URI score() {
            return URI.create(info() + "/score");
        }

        @Nonnull
        public URI metrics() {
            return URI.create(info() + "/metrics");
        }

        @Nonnull
        public URI options() {
            return URI.create(info() + "/options");
        }

        @Nonnull
        public URI publisher() {
            return URI.create(info() + "/publisher");
        }

        @Nonnull
        public URI docs() {
            return URI.create(repository.apiRoot() + "/documentation/" + packageName);
        }
    }

    public enum SearchOrdering {
        TOP,
        TEXT,
        CREATED,
        UPDATED,
        POPULARITY,
        LIKE,
        POINTS;

        @Nonnull
        public static SearchOrdering parse(@Nonnull String ordering) {
            Optional<SearchOrdering> optSO = Arrays.stream(SearchOrdering.values())
                    .filter(so -> so.name().equalsIgnoreCase(ordering))
                    .findFirst();

            assert optSO.isPresent();

            return optSO.get();
        }
    }
}