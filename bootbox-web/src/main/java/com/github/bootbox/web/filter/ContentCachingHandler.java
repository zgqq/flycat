package com.github.bootbox.web.filter;

import com.github.bootbox.web.util.HttpRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public interface ContentCachingHandler {

    default boolean executeNextFilter(HttpRequestWrapper requestWrapper,
                                      ContentCachingResponseWrapper responseWrapper) {
        return true;
    }

    default String replaceResponse(String originResponse) {
        return originResponse;
    }

    default PostFilterAction postFilter(HttpRequestWrapper requestWrapper,
                                        ContentCachingResponseWrapper responseWrapper) {
        return new PostFilterAction(false, false);
    }
}
