package com.github.bootbox.web;

import com.github.bootbox.web.api.ApiFactory;
import com.github.bootbox.web.api.ApiFactoryImpl;
import com.github.bootbox.web.filter.ContentCachingHandler;

public interface BootboxWebConfiguration {
    default ApiFactory createApiFactory() {
        return new ApiFactoryImpl();
    }

    default ContentCachingHandler contentCachingHandler() {
        return new ContentCachingHandler() {
        };
    }
}
