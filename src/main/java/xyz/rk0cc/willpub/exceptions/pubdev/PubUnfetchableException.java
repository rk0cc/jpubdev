package xyz.rk0cc.willpub.exceptions.pubdev;

import xyz.rk0cc.willpub.pubdev.PubDevRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PubUnfetchableException extends RuntimeException {
    public final Class<?> fetchingClass;
    public final PubDevRepository repository;
    public final PubDevRepository.PubDevPackageResultRepository packageResultRepository;

    public PubUnfetchableException(
            @Nonnull Class<?> fetchingClass,
            @Nonnull PubDevRepository repository,
            @Nullable PubDevRepository.PubDevPackageResultRepository packageResultRepository
    ) {
        this.fetchingClass = fetchingClass;
        this.repository = repository;
        this.packageResultRepository = packageResultRepository;
    }
}
