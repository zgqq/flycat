package com.github.bootbox.web.api;


import com.github.bootbox.web.util.HttpRequestWrapper;

public class ApiHttpRequest {

    private final HttpRequestWrapper httpServletRequest;
    private final Object apiRequest;

    public ApiHttpRequest(HttpRequestWrapper httpServletRequest,
                          Object apiRequest) {
        this.httpServletRequest = httpServletRequest;
        this.apiRequest = apiRequest;
    }

    public Object getApiRequest() {
        return apiRequest;
    }

    public HttpRequestWrapper getHttpServletRequest() {
        return httpServletRequest;
    }
}
