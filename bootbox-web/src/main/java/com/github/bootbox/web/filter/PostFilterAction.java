package com.github.bootbox.web.filter;

public class PostFilterAction {
    private final boolean readResponse;
    private final boolean logResponse;

    public PostFilterAction(boolean readResponse, boolean logResponse) {
        this.readResponse = readResponse;
        this.logResponse = logResponse;
    }

    public boolean isLogResponse() {
        return logResponse;
    }

    public boolean isReadResponse() {
        return readResponse;
    }
}
