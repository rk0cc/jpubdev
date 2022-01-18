package xyz.rk0cc.willpub.pubdev;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Locate the server of repository uses for fetching data in {@link xyz.rk0cc.willpub.pubdev}.
 *
 * @since 1.0.0
 */
public record PubDevRepository(@Nonnull URL pubRoot) {
    @Nonnull
    private URL resolveURL(@Nonnull String[] path, @Nonnull Map<String, String> query) {
        StringBuilder refPath = new StringBuilder();
        refPath.append('/').append(String.join("/", Arrays.asList(path)));

        if (query.size() > 0) {
            refPath.append('?');

            Iterator<Map.Entry<String, String>> qmei = query.entrySet().iterator();
            while (qmei.hasNext()) {
                Map.Entry<String, String> qme = qmei.next();

                refPath.append(qme.getKey());
                if (qme.getValue() != null) refPath.append('=').append(qme.getValue());
                if (qmei.hasNext()) refPath.append('&');
            }
        }

        try {
            return new URL(pubRoot, refPath.toString());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nonnull
    private URL resolveURL(@Nonnull String... path) {
        return resolveURL(path, new HashMap<>());
    }

    @Nonnull
    public URL apiRoot() {
        return resolveURL("api");
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

    public enum SearchOrdering {
        TOP;
    }
}
