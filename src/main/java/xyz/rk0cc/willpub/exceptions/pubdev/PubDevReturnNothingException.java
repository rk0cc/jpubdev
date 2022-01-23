package xyz.rk0cc.willpub.exceptions.pubdev;

import com.fasterxml.jackson.databind.node.ContainerNode;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Optional;

/**
 * An exception which the pub API return {@link ContainerNode#isEmpty() empty context}.
 * <br/>
 * All exception related with {@link PubDevReturnNothingException} can not be constructed from outside the
 * specified triggered {@link Class}.
 *
 * @since 1.0.0
 */
public abstract class PubDevReturnNothingException extends IOException {
    /**
     * Default message if no message applied.
     */
    private static final String DEFAULT_MESSAGE = "Responded with no context found.";

    /**
     * Construct exception which no context returned.
     *
     * @param node An empty {@link ContainerNode}.
     */
    PubDevReturnNothingException(@Nonnull ContainerNode<?> node) {
        super(DEFAULT_MESSAGE);
        assert isEmptyObjectNode(node);
        assert isMatchedTriggerClass();
    }

    /**
     * Construct exception which no context returned.
     *
     * @param node An empty {@link ContainerNode}.
     * @param message Custom message displayed in the list.
     */
    PubDevReturnNothingException(@Nonnull ContainerNode<?> node, @Nonnull String message) {
        super(message);
        assert isEmptyObjectNode(node);
        assert isMatchedTriggerClass();
    }

    /**
     * Construct exception which no context returned.
     *
     * @param node An empty {@link ContainerNode}.
     * @param throwable Applied throwable.
     */
    PubDevReturnNothingException(@Nonnull ContainerNode<?> node, @Nonnull Throwable throwable) {
        super(DEFAULT_MESSAGE, throwable);
        assert isEmptyObjectNode(node);
        assert isMatchedTriggerClass();
    }

    /**
     * Construct exception which no context returned.
     *
     * @param node An empty {@link ContainerNode}.
     * @param message Custom message displayed in the list.
     * @param throwable Applied throwable.
     */
    PubDevReturnNothingException(
            @Nonnull ContainerNode<?> node,
            @Nonnull String message,
            @Nonnull Throwable throwable
    ) {
        super(message, throwable);
        assert isEmptyObjectNode(node);
        assert isMatchedTriggerClass();
    }

    /**
     * Specify which {@link Class} permit to throw the related exception by inspecting {@link StackWalker}.
     *
     * @return The {@link Class} that allows this exception thrown from.
     */
    @Nonnull
    abstract Class<?> triggeredClass();

    /**
     * Check is applied empty container node.
     *
     * @param node Applied node.
     *
     * @return Result from {@link ContainerNode#isEmpty()}.
     */
    private boolean isEmptyObjectNode(@Nonnull ContainerNode<?> node) {
        return node.isEmpty();
    }

    /**
     * Matching the {@link Class} is triggered from.
     *
     * @return <code>true</code> if matched triggered class
     */
    private boolean isMatchedTriggerClass() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(sfs -> sfs.anyMatch(sf -> sf.getDeclaringClass().equals(triggeredClass())));
    }
}
