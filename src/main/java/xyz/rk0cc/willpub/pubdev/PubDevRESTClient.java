package xyz.rk0cc.willpub.pubdev;

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.rk0cc.willpub.exceptions.pubdev.MetricsPendingAnalysisException;
import xyz.rk0cc.willpub.exceptions.pubdev.PubUnfetchableException;
import xyz.rk0cc.willpub.pubdev.structure.PubPkgDoc;
import xyz.rk0cc.willpub.pubdev.structure.PubSearchResult;
import xyz.rk0cc.willpub.pubdev.structure.pkg.*;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;

public final class PubDevRESTClient {
    private static final String JPUBDEV_VERSION = "1.0.0-SNAPSHOT";
    private final PubDevRepository repository;
    private PubDevRepository.PubDevPackageResultRepository packageRepo;

    public PubDevRESTClient(@Nullable PubDevRepository repository) {
        this.repository = repository == null ? PubDevRepository.getInstance() : repository;
        this.packageRepo = null;
    }

    private PubSearchResult.PubSearchContext requestSearchCtx(@Nonnull URI search)
            throws IOException, InterruptedException {
        System.out.println(search);
        return HttpClient.newHttpClient()
                .send(
                        buildRequest(search),
                        new DeserializedHttpResponseBody<>(PubSearchResult.PubSearchContext.class)
                )
                .body();
    }

    @Nonnull
    public PubSearchResult search(
            @Nonnull String query,
            @Nonnegative long page,
            @Nonnull PubDevRepository.SearchOrdering ordering
    ) throws IOException, InterruptedException {
        return new PubSearchResult(query, page, ordering, requestSearchCtx(repository.search(query, page, ordering)));
    }

    @Nonnull
    public PubSearchResult search(@Nonnull String query, @Nonnegative long page)
            throws IOException, InterruptedException {
        return new PubSearchResult(
                query,page, PubDevRepository.SearchOrdering.TOP, requestSearchCtx(repository.search(query, page))
        );
    }

    @Nonnull
    public PubSearchResult search(@Nonnull String query, @Nonnull PubDevRepository.SearchOrdering ordering)
            throws IOException, InterruptedException {
        return new PubSearchResult(query, 1, ordering, requestSearchCtx(repository.search(query, ordering)));
    }

    @Nonnull
    public PubSearchResult search(@Nonnull String query) throws IOException, InterruptedException {
        return new PubSearchResult(
                query, 1, PubDevRepository.SearchOrdering.TOP, requestSearchCtx(repository.search(query))
        );
    }

    @Nonnull
    public PubSearchResult searchNextPage(@Nonnull PubSearchResult result) throws IOException, InterruptedException {
        return new PubSearchResult(result.query(), result.page() + 1, result.ordering(), requestSearchCtx(
                result.nextPageURL(repository)
        ));
    }

    @Nonnull
    public PubSearchResult searchPreviousPage(@Nonnull PubSearchResult result)
            throws IOException, InterruptedException {
        return new PubSearchResult(result.query(), result.page() - 1, result.ordering(), requestSearchCtx(
                result.previousPageURL(repository)
        ));
    }


    private void validatePackageApplied() {
        if (packageRepo == null)
            throw new PubUnfetchableException(
                    getClass(),
                    repository,
                    null,
                    "Fetching package information without package applied."
            );
    }

    @Nonnull
    public PubDevRESTClient applyPackage(PubDevRepository.PubDevPackageResultRepository packageRepo) {
        this.packageRepo = packageRepo;
        return this;
    }

    @Nonnull
    public PubPkgInfo packageInfo() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.info()), new DeserializedHttpResponseBody<>(PubPkgInfo.class))
                .body();
    }

    @Nonnull
    public PubPkgOptions packageOptions() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.options()), new DeserializedHttpResponseBody<>(PubPkgOptions.class))
                .body();
    }

    @Nullable
    public PubPkgMetrics packageMetrics() throws IOException, InterruptedException {
        try {
            return HttpClient.newHttpClient()
                    .send(buildRequest(packageRepo.metrics()), new DeserializedHttpResponseBody<>(PubPkgMetrics.class))
                    .body();
        } catch (UncheckedIOException e) {
            if (
                    e.getCause() instanceof MetricsPendingAnalysisException
                    || Arrays.stream(e.getSuppressed()).anyMatch(st -> st instanceof MetricsPendingAnalysisException)
            ) {
                return null;
            }
            throw e;
        }
    }

    @Nonnull
    public PubPkgScore packageScore() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.score()), new DeserializedHttpResponseBody<>(PubPkgScore.class))
                .body();
    }

    @Nonnull
    public PubPkgPublisher packagePublisher() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.publisher()), new DeserializedHttpResponseBody<>(PubPkgPublisher.class))
                .body();
    }

    @Nonnull
    public PubPkgDoc packageDocumentations() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.docs()), new DeserializedHttpResponseBody<>(PubPkgDoc.class))
                .body();
    }

    @Nonnull
    private static HttpRequest buildRequest(@Nonnull URI url) {
        return HttpRequest.newBuilder()
                .uri(url)
                .header(
                        "User-Agent",
                        "jpubdev/" + JPUBDEV_VERSION + " (JRE " + System.getProperty("java.version") + ")"
                )
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_2)
                .timeout(Duration.ofMinutes(1))
                .GET()
                .build();
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static final class DeserializedHttpResponseBody<T> implements HttpResponse.BodyHandler<T> {
        private final Class<T> targetClass;

        DeserializedHttpResponseBody(@Nonnull Class<T> targetClass) {
            this.targetClass = targetClass;
        }

        @Nonnull
        @Override
        public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
            return HttpResponse.BodySubscribers.mapping(
                    HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
                    body -> {
                        try {
                            return new ObjectMapper().readValue(body, targetClass);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
            );
        }
    }
}
