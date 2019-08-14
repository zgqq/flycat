/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        final int modulePlaceholderCode = apiFactory.getModulePlaceholderCode();
        if (modulePlaceholderCode > 99 || modulePlaceholderCode < 0) {
            throw new BootboxWebException("modulePlaceholderCode must be less than 100 and greater than 0," +
                    " code " + modulePlaceholderCode + " was configured");
        }

        final int businessErrorPlaceholderCode = apiFactory.getBusinessErrorPlaceholderCode();
        if (businessErrorPlaceholderCode > 9 || businessErrorPlaceholderCode < 1) {
            throw new BootboxWebException("businessErrorPlaceholderCode must be less " +
                    "than 10 and greater than 0");
        }

        final int systemErrorPlaceholderCode = apiFactory.getSystemErrorPlaceholderCode();
        if (systemErrorPlaceholderCode > 9 || systemErrorPlaceholderCode < 1) {
            throw new BootboxWebException("errorPlaceholderCode must be less " +
                    "than 10 and greater than 0");
        }

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
