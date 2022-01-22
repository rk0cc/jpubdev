package xyz.rk0cc.willpub.exceptions.pubdev;

import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Indicate the package response with error JSON under HTTP error.
 *
 * @since 1.0.0
 */
public class PubPkgErrorReportedException extends IOException {
    private final String code, message;

    /**
     * Construct an exception when received response error during deserialization.
     *
     * @param errReturnNode An {@link ObjectNode} that contains context of the error.
     */
    public PubPkgErrorReportedException(@Nonnull ObjectNode errReturnNode) {
        super("Pub repository server response error with summaries");
        assert errReturnNode.has("error");
        this.code = errReturnNode.get("code").textValue();
        this.message = errReturnNode.get("message").textValue();
    }

    /**
     * Print the information of this exception with {@link #getMessage() message}, stack trace and error response.
     *
     * @return A formatted exception with message, stack trace and responded error information.
     */
    @Override
    public String toString() {
        return super.toString() + "\n\n" +
                "Error code: " + code +
                "Error message: " + message +
                "\n\n";
    }
}
