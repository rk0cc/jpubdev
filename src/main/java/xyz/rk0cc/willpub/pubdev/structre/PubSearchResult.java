package xyz.rk0cc.willpub.pubdev.structre;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.pubdev.PubDevRepository;
import xyz.rk0cc.willpub.pubdev.parser.PubSearchContextDeserializer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.net.URL;
import java.util.List;

public record PubSearchResult(
        @Nonnull String query,
        @Nonnegative int page,
        @Nonnull PubDevRepository.SearchOrdering ordering,
        @Nonnull PubSearchContext context
) {
    @Nonnull
    public URL nextPageURL(@Nonnull PubDevRepository repository) {
        assert context().hasNextPage;
        return repository.search(query, page + 1, ordering);
    }

    @Nonnull
    public URL previousPageURL(@Nonnull PubDevRepository repository) {
        assert page > 1;
        return repository.search(query, context.packages().isEmpty() ? 1 : page - 1, ordering);
    }

    @Nonnull
    public PubDevRepository.PubDevPackageResultRepository packageResultRepository(
            @Nonnull PubDevRepository repository,
            @Nonnegative int idx
    ) {
        return repository.fetchPackage(context.packages().get(idx));
    }

    @JsonDeserialize(using = PubSearchContextDeserializer.class)
    public record PubSearchContext(@Nonnull List<String> packages, boolean hasNextPage) {}
}
