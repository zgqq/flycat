package com.github.flycat.web;

import com.github.flycat.web.filter.ContentCachingHandler;

public interface WebConfiguration {
    default ContentCachingHandler contentCachingHandler() {
        return new ContentCachingHandler() {
        };
    }
}
