package xyz.rk0cc.willpub.pubdev;

import xyz.rk0cc.willpub.pubspec.data.PubspecValueValidator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Locate the server of repository uses for fetching data in {@link xyz.rk0cc.willpub.pubdev}.
 * <br/>
 * It's referencing Dart package namely <a href="https://pub.dev/packages/pub_api_client">pub_api_client</a>. But
 * dropped authorization related action which means it perform GET request only.
 *
 * @since 1.0.0
 */
public record PubDevRepository(@Nonnull URL pubRoot) {
    /**
     * Get root location of API.
     *
     * @return An {@link URL} located to pub API page.
     */
    @Nonnull
    public URL apiRoot() {
        try {
            return new URL(pubRoot, "/api");
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Get the search result from query.
     *
     * @param query Searching query.
     * @param page Page number of the search result.
     * @param ordering Specify ordering result.
     *
     * @return An {@link URL} of following search API.
     */
    @Nonnull
    public URL search(@Nonnull String query, @Nonnegative int page, @Nonnull SearchOrdering ordering) {
        try {
            return new URL(apiRoot(), "/search?q=" + query + "&page=" + page + "&sort=" + ordering.name().toLowerCase());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    @Nonnull
    public URL search(@Nonnull String query, @Nonnull SearchOrdering ordering) {
        return search(query, 1, ordering);
    }

    @Nonnull
    public URL search(@Nonnull String query, @Nonnegative int page) {
        return search(query, page, SearchOrdering.TOP);
    }

    /**
     * Get the <b>first page</b> of the search result from query.
     *
     * @param query Searching query.
     *
     * @return An {@link URL} of following search API.
     */
    @Nonnull
    public URL search(@Nonnull String query) {
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

        try {
            return new PubDevRepository(new URL(defaultPubRepo == null ? "https://pub.dev" : defaultPubRepo));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
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
        public URL summary() {
            try {
                return new URL(repository.apiRoot(), "/packages/" + packageName);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }

        @Nonnull
        public URL score() {
            try {
                return new URL(summary(), "/score");
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }

        @Nonnull
        public URL metrics() {
            try {
                return new URL(summary(), "/metrics");
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }

        @Nonnull
        public URL options() {
            try {
                return new URL(summary(), "/options");
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }

        @Nonnull
        public URL publisher() {
            try {
                return new URL(summary(), "/publisher");
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }

        @Nonnull
        public URL docs() {
            try {
                return new URL(repository.apiRoot(), "/documentation/" + packageName);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
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