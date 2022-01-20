package xyz.rk0cc.willpub.exceptions.pubdev;

import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.Nonnull;
import java.io.IOException;

public class PubPkgErrorReportedException extends IOException {
    private final String code, message;

    public PubPkgErrorReportedException(@Nonnull ObjectNode errReturnNode) {
        super("Pub repository server response error with summaries");
        assert errReturnNode.has("error");
        this.code = errReturnNode.get("code").textValue();
        this.message = errReturnNode.get("message").textValue();
    }

    @Override
    public String toString() {
        return super.toString() + "\n\n" +
                "Error code: " + code +
                "Error message: " + message +
                "\n\n";
    }
}
