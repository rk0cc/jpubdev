package xyz.rk0cc.willpub.exceptions.pubdev;

import xyz.rk0cc.willpub.pubdev.PubDevRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An exception that encountered issue when fetching REST API result.
 *
 * @since 1.0.0
 */
public class PubUnfetchableException extends RuntimeException {
    /**
     * A class currently fetching the information.
     */
    public final Class<?> fetchingClass;
    private final PubDevRepository repository;
    private final PubDevRepository.PubDevPackageResultRepository packageResultRepository;

    /**
     * Construct exception when resolving REST failed.
     *
     * @param fetchingClass Class that currently fetching data.
     * @param repository Repository of pub.
     * @param packageResultRepository Package repository currently uses for resolving data.
     * @param message Message for display this exception.
     * @param throwable Any {@link Throwable} to handle this exception.
     */
    public PubUnfetchableException(
            @Nonnull Class<?> fetchingClass,
            @Nonnull PubDevRepository repository,
            @Nullable PubDevRepository.PubDevPackageResultRepository packageResultRepository,
            @Nullable String message,
            @Nullable Throwable throwable
    ) {
        super(message, throwable);
        this.fetchingClass = fetchingClass;
        this.repository = repository;
        this.packageResultRepository = packageResultRepository;
    }

    /**
     * Construct exception when resolving REST failed.
     *
     * @param fetchingClass Class that currently fetching data.
     * @param repository Repository of pub.
     * @param packageResultRepository Package repository currently uses for resolving data.
     * @param message Message for display this exception.
     */
    public PubUnfetchableException(
            @Nonnull Class<?> fetchingClass,
            @Nonnull PubDevRepository repository,
            @Nullable PubDevRepository.PubDevPackageResultRepository packageResultRepository,
            @Nullable String message
    ) {
        this(fetchingClass, repository, packageResultRepository, message, null);
    }

    /**
     * Construct exception when resolving REST failed.
     *
     * @param fetchingClass Class that currently fetching data.
     * @param repository Repository of pub.
     * @param packageResultRepository Package repository currently uses for resolving data.
     */
    public PubUnfetchableException(
            @Nonnull Class<?> fetchingClass,
            @Nonnull PubDevRepository repository,
            @Nullable PubDevRepository.PubDevPackageResultRepository packageResultRepository
    ) {
        this(fetchingClass, repository, packageResultRepository, "Unable to resolve the response now.");
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(super.toString());
        b.append("\n\n");
        b.append("Fetching class: ");
        b.append(fetchingClass.getName());
        b.append("\nRepository: ");
        b.append(repository.pubRoot());

        if (packageResultRepository != null) {
            b.append("\nPackage name: ");
            b.append(packageResultRepository.packageName());
        }

        b.append("\n");

        return b.toString();
    }
}
