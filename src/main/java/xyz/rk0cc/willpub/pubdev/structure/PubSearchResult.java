package xyz.rk0cc.willpub.pubdev.structure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.pubdev.PubDevRepository;
import xyz.rk0cc.willpub.pubdev.parser.PubSearchContextDeserializer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.net.URI;
import java.util.List;

/**
 * Provides search result of the related package from API with given {@link #query()}, {@link #page()},
 * {@link #ordering() ordering search result} and most importing thing - {@link #context() search result}.
 */
public record PubSearchResult(
        @Nonnull String query,
        @Nonnegative long page,
        @Nonnull PubDevRepository.SearchOrdering ordering,
        @Nonnull PubSearchContext context
) {
    /**
     * Get a next page {@link URI} for {@link PubSearchResult current result} if returned result provides
     * {@link PubSearchContext#hasNextPage() next page}.
     *
     * @param repository The {@link PubDevRepository} currently uses.
     *
     * @return {@link URI} to next page of the {@link PubSearchResult search result}.
     *
     * @throws IndexOutOfBoundsException When it reached the last page of the search result already.
     */
    @Nonnull
    public URI nextPageURL(@Nonnull PubDevRepository repository) {
        if (!context().hasNextPage)
            throw new IndexOutOfBoundsException("This is the last page of the search result");
        return repository.search(query, page + 1, ordering);
    }

    /**
     * Get a previous page {@link URI} for {@link PubSearchResult current result} which the {@link #page() page number}
     * is greater than 1.
     *
     * @param repository The {@link PubDevRepository} currently uses.
     *
     * @return {@link URI} to previous page of the {@link PubSearchResult search result}.
     *
     * @throws IndexOutOfBoundsException When accessing previous page on page 1.
     */
    @Nonnull
    public URI previousPageURL(@Nonnull PubDevRepository repository) {
        if (page <= 1)
            throw new IndexOutOfBoundsException("This is the first page of search result.");
        return repository.search(query, context.packages().isEmpty() ? 1 : page - 1, ordering);
    }

    /**
     * Get {@link PubDevRepository.PubDevPackageResultRepository repository of the package's name} by
     * given {@link List}'s index number in {@link PubSearchContext#packages()}.
     *
     * @param repository The {@link PubDevRepository} currently uses.
     * @param idx Index number of {@link PubSearchContext#packages() package results}.
     *
     * @return A {@link PubDevRepository.PubDevPackageResultRepository repository} of the selected package.
     */
    @Nonnull
    public PubDevRepository.PubDevPackageResultRepository packageResultRepository(
            @Nonnull PubDevRepository repository,
            @Nonnegative int idx
    ) {
        return repository.fetchPackage(context.packages().get(idx));
    }

    /**
     * A context of the {@link PubSearchResult search result}.
     *
     * @since 1.0.0
     */
    @JsonDeserialize(using = PubSearchContextDeserializer.class)
    public record PubSearchContext(@Nonnull List<String> packages, boolean hasNextPage) {}
}
