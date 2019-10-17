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
package com.github.flycat.starter.app.web;

import com.github.flycat.starter.app.config.AppConf;
import com.github.flycat.starter.app.web.api.AppRequest;
import com.github.flycat.util.StringReplacer;
import com.github.flycat.web.WebApiConfiguration;
import com.github.flycat.web.api.ApiFactory;
import com.github.flycat.web.api.ApiHttpRequest;
import com.github.flycat.web.api.ApiRequestHolder;
import com.github.flycat.web.filter.ContentCachingHandler;
import com.github.flycat.web.filter.PostFilterAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class WebApiConfigurationAdapter implements WebApiConfiguration {

    @Override
    public ApiFactory createApiFactory() {
        return new FlycatApiFactoryAdapter();
    }

    @Override
    public ContentCachingHandler contentCachingHandler() {
        return new ContentCachingHandler() {
            @Override
            public PostFilterAction postFilter(HttpServletRequest httpServletRequest,
                                               HttpServletResponse httpServletResponse) {
                final ApiHttpRequest currentApiRequest = ApiRequestHolder.getCurrentApiRequest();

                boolean isDebugUid = false;
                if (currentApiRequest != null && currentApiRequest.getApiRequest() != null) {
                    AppRequest apiRequest = (AppRequest) currentApiRequest.getApiRequest();
                    if (AppConf.getDebugUids().contains(apiRequest.getUid() + "")) {
                        isDebugUid = true;
                    }
                }

                Map<String, List<String>> replaceContentsMap = AppConf.getContentFilterMap();
                boolean hasReplaceConf = false;
                if (replaceContentsMap != null && replaceContentsMap.size() > 0) {
                    hasReplaceConf = true;
                }

                boolean needReadResponse = isDebugUid || hasReplaceConf;

                return new PostFilterAction(needReadResponse,
                        isDebugUid
                );
            }

            @Override
            public String replaceResponse(String originResponse) {
                Map<String, List<String>> replaceContentsMap = AppConf.getContentFilterMap();
                return StringReplacer.replace(replaceContentsMap, originResponse);
            }

            @Override
            public boolean executeNextFilter(HttpServletRequest httpServletRequest,
                                             HttpServletResponse httpServletResponse) {
                return true;
            }
        };
    }
}
