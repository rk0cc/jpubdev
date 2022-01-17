package xyz.rk0cc.willpub.pubdev;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

public record PubDevRepository(@Nonnull URL pubRoot) {
    @Nonnull
    public static PubDevRepository getInstance() {
        String defaultPubRepo = System.getenv("PUB_HOSTED_URL");

        try {
            return new PubDevRepository(new URL(defaultPubRepo == null ? "https://pub.dev" : defaultPubRepo));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
