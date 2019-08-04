package com.github.bootbox.web.api;

public final class ApiRequestHolder {
    private static final ThreadLocal<ApiHttpRequest> REQUEST_HOLDER = new ThreadLocal<>();

    private ApiRequestHolder() {
    }

    public static void setCurrentApiRequest(ApiHttpRequest apiHttpRequest) {
        REQUEST_HOLDER.set(apiHttpRequest);
    }

    public static ApiHttpRequest getCurrentApiRequest() {
        return REQUEST_HOLDER.get();
    }
}
