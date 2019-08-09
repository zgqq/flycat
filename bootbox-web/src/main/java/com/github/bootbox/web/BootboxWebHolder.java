package com.github.bootbox.web;

import com.github.bootbox.server.event.EventManager;
import com.github.bootbox.web.api.ApiFactory;
import com.github.bootbox.web.api.ApiFactoryHolder;
import com.github.bootbox.web.api.ApiParameterResolver;
import com.github.bootbox.web.filter.ContentCachingHandler;
import com.github.bootbox.web.log.WebLogAlarmListener;

public class BootboxWebHolder {
    private static volatile ContentCachingHandler contentCachingHandler;
    private static volatile ApiParameterResolver apiParameterResolver;

    public static void load(BootboxWebConfiguration configuration) {
        final ApiFactory apiFactory = configuration.createApiFactory();
        ApiFactoryHolder.setApiFactory(apiFactory);

        contentCachingHandler = configuration.contentCachingHandler();
        apiParameterResolver = configuration.createParameterResolver();
        EventManager.register(new WebLogAlarmListener());
    }

    public static ContentCachingHandler getContentCachingHandler() {
        return contentCachingHandler;
    }

    public static ApiParameterResolver getApiParameterResolver() {
        return apiParameterResolver;
    }

    public static void setApiParameterResolver(ApiParameterResolver apiParameterResolver) {
        BootboxWebHolder.apiParameterResolver = apiParameterResolver;
    }

    public static void setContentCachingHandler(ContentCachingHandler contentCachingHandler) {
        BootboxWebHolder.contentCachingHandler = contentCachingHandler;
    }
}
