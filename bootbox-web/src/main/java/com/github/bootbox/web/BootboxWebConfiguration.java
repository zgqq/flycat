package com.github.bootbox.web;

import com.github.bootbox.web.api.ApiFactory;
import com.github.bootbox.web.api.ApiFactoryImpl;
import com.github.bootbox.web.api.ApiParameterResolver;
import com.github.bootbox.web.api.ParameterRequest;
import com.github.bootbox.web.filter.ContentCachingHandler;

import javax.servlet.http.HttpServletRequest;

public interface BootboxWebConfiguration {

    default ApiParameterResolver createParameterResolver() {
        return new ApiParameterResolver() {
            @Override
            public String resolveParameter(HttpServletRequest request, ParameterRequest parameterRequest) {
                return null;
            }
        };
    }

    default ApiFactory createApiFactory() {
        return new ApiFactoryImpl();
    }

    default ContentCachingHandler contentCachingHandler() {
        return new ContentCachingHandler() {
        };
    }
}
