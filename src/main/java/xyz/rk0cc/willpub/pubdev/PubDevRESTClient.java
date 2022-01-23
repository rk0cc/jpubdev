package xyz.rk0cc.willpub.pubdev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import xyz.rk0cc.willpub.exceptions.pubdev.*;
import xyz.rk0cc.willpub.pubdev.structure.*;
import xyz.rk0cc.willpub.pubdev.structure.pkg.*;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * Client for fetching package information in with given {@link PubDevRepository}.
 *
 * @since 1.0.0
 */
public final class PubDevRESTClient {
    /**
     * A {@link Boolean} field to make request in HTTP 1.1 instead of HTTP 2 if unsupported.
     * It is <code>false</code> by default.
     */
    public static boolean HTTP_1_1_REQUEST_MODE = false;
    private final PubDevRepository repository;
    private PubDevRepository.PubDevPackageResultRepository packageRepo;

    /**
     * Construct a client with given {@link PubDevRepository} to fetch pub repository API.
     *
     * @param repository Repository information for fetching REST result.
     */
    public PubDevRESTClient(@Nonnull PubDevRepository repository) {
        this.repository = repository;
        this.packageRepo = null;
    }

    /**
     * Assemble {@link PubSearchResult.PubSearchContext} in
     * {@link #search(String, long, PubDevRepository.SearchOrdering) searching}.
     *
     * @param search Completed {@link URI} of incoming search.
     *
     * @return {@link PubSearchResult#context() Search result context}.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
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

    /**
     * Give a search result of the given parameters.
     *
     * @param query Search query.
     * @param page Page number.
     * @param ordering Prefer searching order.
     *
     * @return {@link PubSearchResult Result} of this search. If no related package found in this search,
     *         {@link PubSearchResult.PubSearchContext#packages() the package list} will return with empty
     *         {@link java.util.List}.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nonnull
    public PubSearchResult search(
            @Nonnull String query,
            @Nonnegative long page,
            @Nonnull PubDevRepository.SearchOrdering ordering
    ) throws IOException, InterruptedException {
        return new PubSearchResult(query, page, ordering, requestSearchCtx(repository.search(query, page, ordering)));
    }

    /**
     * Give a search result of the given parameters.
     *
     * @param query Search query.
     * @param page Page number.
     *
     * @return {@link PubSearchResult Result} of this search. If no related package found in this search,
     *         {@link PubSearchResult.PubSearchContext#packages() the package list} will return with empty
     *         {@link java.util.List}.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nonnull
    public PubSearchResult search(@Nonnull String query, @Nonnegative long page)
            throws IOException, InterruptedException {
        return new PubSearchResult(
                query,page, PubDevRepository.SearchOrdering.TOP, requestSearchCtx(repository.search(query, page))
        );
    }

    /**
     * Give a search result of the given parameters.
     *
     * @param query Search query.
     * @param ordering Prefer searching order.
     *
     * @return {@link PubSearchResult Result} of this search. If no related package found in this search,
     *         {@link PubSearchResult.PubSearchContext#packages() the package list} will return with empty
     *         {@link java.util.List}.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nonnull
    public PubSearchResult search(@Nonnull String query, @Nonnull PubDevRepository.SearchOrdering ordering)
            throws IOException, InterruptedException {
        return new PubSearchResult(query, 1, ordering, requestSearchCtx(repository.search(query, ordering)));
    }

    /**
     * Give a search result of the given parameters.
     *
     * @param query Search query.
     *
     * @return {@link PubSearchResult Result} of this search. If no related package found in this search,
     *         {@link PubSearchResult.PubSearchContext#packages() the package list} will return with empty
     *         {@link java.util.List}.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nonnull
    public PubSearchResult search(@Nonnull String query) throws IOException, InterruptedException {
        return new PubSearchResult(
                query, 1, PubDevRepository.SearchOrdering.TOP, requestSearchCtx(repository.search(query))
        );
    }

    /**
     * Give the next page of existed {@link PubSearchResult search result}.
     *
     * @param result Existed result for fetching next page context.
     *
     * @return Next page of {@link PubSearchResult}. If no related package found in this search,
     *         {@link PubSearchResult.PubSearchContext#packages() the package list} will return with empty
     *         {@link java.util.List}.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     * @throws IndexOutOfBoundsException If reached the last page of search result.
     */
    @Nonnull
    public PubSearchResult searchNextPage(@Nonnull PubSearchResult result) throws IOException, InterruptedException {
        return new PubSearchResult(result.query(), result.page() + 1, result.ordering(), requestSearchCtx(
                result.nextPageURL(repository)
        ));
    }

    /**
     * Give the previous page of existed {@link PubSearchResult search result}.
     *
     * @param result Existed result for fetching previous page context.
     *
     * @return Previous page of {@link PubSearchResult}. If no related package found in this search,
     *         {@link PubSearchResult.PubSearchContext#packages() the package list} will return with empty
     *         {@link java.util.List}.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     * @throws IndexOutOfBoundsException If reached the first page of search result.
     */
    @Nonnull
    public PubSearchResult searchPreviousPage(@Nonnull PubSearchResult result)
            throws IOException, InterruptedException {
        return new PubSearchResult(result.query(), result.page() - 1, result.ordering(), requestSearchCtx(
                result.previousPageURL(repository)
        ));
    }

    /**
     * To validate the {@link #packageRepo} is applied already if fetching data of the package.
     * <br/>
     * If {@link #packageRepo} is <code>null</code>, it throws {@link PubUnfetchableException}.
     */
    private void validatePackageApplied() {
        if (packageRepo == null)
            throw new PubUnfetchableException(
                    getClass(),
                    repository,
                    null,
                    "Fetching package information without package applied."
            );
    }

    /**
     * Assign package repository for fetching information in {@link PubDevRepository.PubDevPackageResultRepository}.
     * <br/>
     * The {@link PubDevRepository.PubDevPackageResultRepository} can be assigned directly or indirectly from
     * {@link PubDevRepository#fetchPackage(String)}. However, direct assign will have higher chance to throw an
     * {@link PubPkgErrorReportedException exception} if the package has not been hosted yet.
     * <br/>
     * Indirect apply is lower risk than direct one but vary depending on how to apply package name from
     * {@link PubSearchResult.PubSearchContext#packages()} which is an unmodifiable {@link java.util.List}.
     * <br/>
     * The best way to get package name (assume user don't know {@link java.util.List List's} items) is getting
     * {@link java.util.List#indexOf(Object) index of the item} first, then call {@link java.util.List#get(int)}.
     *
     * @param packageRepo Package repository will be uses for fetching package information.
     *
     * @return Same object of {@link PubDevRESTClient} allows to chain the method to finish a request.
     *
     * @throws PubUnfetchableException When calculated hashcode of
     *                                 {@link PubDevRepository.PubDevPackageResultRepository} is different with the
     *                                 current applied repository in {@link PubDevRESTClient}.
     *
     * @see #unapplyPackage()
     */
    @Nonnull
    public PubDevRESTClient applyPackage(@Nonnull PubDevRepository.PubDevPackageResultRepository packageRepo) {
        if (packageRepo.repository().hashCode() != repository.hashCode())
            throw new PubUnfetchableException(
                    getClass(),
                    repository,
                    packageRepo,
                    "The package repository is not generated from the same source of the PubDevRepository"
            );
        this.packageRepo = packageRepo;
        return this;
    }

    /**
     * Clear existed package repository in {@link PubDevRESTClient}.
     * <br/>
     * This method will remove {@link PubDevRepository.PubDevPackageResultRepository} with no other repository applied
     * which no package request allowed after this method invoked.
     *
     * @return Same object of {@link PubDevRESTClient} allows to chain the method to finish a request.
     *
     * @see #applyPackage(PubDevRepository.PubDevPackageResultRepository)
     */
    @Nonnull
    public PubDevRESTClient unapplyPackage() {
        if (this.packageRepo != null)
            this.packageRepo = null;
        return this;
    }

    /**
     * Crawl {@link PubPkgInfo package information} with some basic data provided.
     *
     * @return {@link PubPkgInfo} of related package.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nonnull
    public PubPkgInfo packageInfo() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.info()), new DeserializedHttpResponseBody<>(PubPkgInfo.class))
                .body();
    }

    /**
     * Crawl {@link PubPkgOptions package option} which contains package preference in pub repository.
     *
     * @return {@link PubPkgInfo} of related package.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nonnull
    public PubPkgOptions packageOptions() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.options()), new DeserializedHttpResponseBody<>(PubPkgOptions.class))
                .body();
    }

    /**
     * Crawl {@link PubPkgMetrics package analysis} result if analysed already.
     *
     * @return {@link PubPkgMetrics Analyzed package result} or <code>null</code> if pending analysis.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nullable
    public PubPkgMetrics packageMetrics() throws IOException, InterruptedException {
        try {
            return HttpClient.newHttpClient()
                    .send(buildRequest(packageRepo.metrics()), new DeserializedHttpResponseBody<>(PubPkgMetrics.class))
                    .body();
        } catch (UncheckedIOException e) {
            if (
                    // Crawl the exception is thrown with related exception which indicating pending analysis
                    e.getCause() instanceof MetricsPendingAnalysisException
                    || Arrays.stream(e.getSuppressed()).anyMatch(st -> st instanceof MetricsPendingAnalysisException)
            ) return null;
            throw e;
        }
    }

    /**
     * Get a summarized {@link PubPkgScore package's score} data.
     *
     * @return {@link PubPkgScore Score} of
     *         {@link #applyPackage(PubDevRepository.PubDevPackageResultRepository) applied package}.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nonnull
    public PubPkgScore packageScore() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.score()), new DeserializedHttpResponseBody<>(PubPkgScore.class))
                .body();
    }

    /**
     * Get {@link PubPkgPublisher publisher information} of applied package.
     *
     * @return {@link PubPkgPublisher} information.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nonnull
    public PubPkgPublisher packagePublisher() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.publisher()), new DeserializedHttpResponseBody<>(PubPkgPublisher.class))
                .body();
    }

    /**
     * Get a {@link PubPkgDoc documentation status} of applied package.
     *
     * @return {@link PubPkgDoc} with all documented package versions' status.
     *
     * @throws IOException When some IO error occurred during
     *                     {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) sending state}.
     * @throws InterruptedException When interrupted when requesting data.
     */
    @Nonnull
    public PubPkgDoc packageDocumentations() throws IOException, InterruptedException {
        validatePackageApplied();
        return HttpClient.newHttpClient()
                .send(buildRequest(packageRepo.docs()), new DeserializedHttpResponseBody<>(PubPkgDoc.class))
                .body();
    }

    /**
     * Build a {@link HttpRequest} for making REST request of pub repository server.
     *
     * @param url Assembled URL to get the context of the response.
     *
     * @return Built {@link HttpRequest} that ready to make request.
     */
    @Nonnull
    private HttpRequest buildRequest(@Nonnull URI url) {
        String version;

        try (InputStream i = getClass().getResourceAsStream("pubdev.properties")) {
            // Get current version of JPubDev for identification in user agent.
            Properties pdprop = new Properties();

            pdprop.load(i);

            version = pdprop.getProperty("pubdev.version");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // Build request
        return HttpRequest.newBuilder()
                .uri(url)
                .header(
                        "User-Agent",
                        "jpubdev/" + version + " (JRE " + System.getProperty("java.version") + ")"
                ) // User agent value scheme: jpubdev/[jpubdev's version] (JRE [Java version])
                .header("Accept", "application/json")
                .version(HTTP_1_1_REQUEST_MODE ? HttpClient.Version.HTTP_1_1 : HttpClient.Version.HTTP_2)
                .timeout(Duration.ofSeconds(30)) // Maybe shorter if OK
                .GET() // Get only, no other request method for this package.
                .build();
    }

    /**
     * Deserialize {@link HttpResponse}'s context to a Java object with related object.
     *
     * @param <T> Targeted {@link Class} which annotated {@link JsonDeserialize} for deserialize context.
     */
    @SuppressWarnings("ClassCanBeRecord")
    private static final class DeserializedHttpResponseBody<T> implements HttpResponse.BodyHandler<T> {
        private final Class<T> targetClass;

        /**
         * Construct a handler for parsing response's body to a Java object.
         *
         * @param targetClass A {@link Class} that targeted to deserialize.
         */
        DeserializedHttpResponseBody(@Nonnull Class<T> targetClass) {
            this.targetClass = targetClass;
            assert targetClass.getAnnotation(JsonDeserialize.class) != null;
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
