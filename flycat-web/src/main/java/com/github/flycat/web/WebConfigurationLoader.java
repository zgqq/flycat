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
package com.github.flycat.web;

import com.github.flycat.web.response.ResponseFactory;
import com.github.flycat.web.response.ResponseFactoryHolder;
import com.github.flycat.web.request.RequestParameterResolver;
import com.github.flycat.web.filter.ContentCachingHandler;

public class WebConfigurationLoader {
    private static volatile ContentCachingHandler contentCachingHandler;
    private static volatile RequestParameterResolver parameterResolver;

    public static void load(WebFactoryConfiguration configuration) {
        final ResponseFactory apiFactory = configuration.createResponseFactory();
        final int modulePlaceholderCode = apiFactory.getModulePlaceholderCode();
        if (modulePlaceholderCode > 99 || modulePlaceholderCode < 0) {
            throw new WebException("modulePlaceholderCode must be less than 100 and greater than 0," +
                    " code " + modulePlaceholderCode + " was configured");
        }

        final int businessErrorPlaceholderCode = apiFactory.getBusinessErrorPlaceholderCode();
        if (businessErrorPlaceholderCode > 9 || businessErrorPlaceholderCode < 1) {
            throw new WebException("businessErrorPlaceholderCode must be less " +
                    "than 10 and greater than 0");
        }

        final int systemErrorPlaceholderCode = apiFactory.getSystemErrorPlaceholderCode();
        if (systemErrorPlaceholderCode > 9 || systemErrorPlaceholderCode < 1) {
            throw new WebException("errorPlaceholderCode must be less " +
                    "than 10 and greater than 0");
        }

        ResponseFactoryHolder.setResponseFactory(apiFactory);

        contentCachingHandler = configuration.contentCachingHandler();
        parameterResolver = configuration.createParameterResolver();
//        EventManager.register(new LogAlarmListener(alarmSender));
    }

    public static ContentCachingHandler getContentCachingHandler() {
        return contentCachingHandler;
    }

    public static RequestParameterResolver getParameterResolver() {
        return parameterResolver;
    }

    public static void setParameterResolver(RequestParameterResolver apiParameterResolver) {
        WebConfigurationLoader.parameterResolver = apiParameterResolver;
    }

    public static void setContentCachingHandler(ContentCachingHandler contentCachingHandler) {
        WebConfigurationLoader.contentCachingHandler = contentCachingHandler;
    }
}
