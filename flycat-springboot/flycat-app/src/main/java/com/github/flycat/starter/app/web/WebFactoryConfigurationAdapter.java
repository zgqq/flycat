/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import com.github.flycat.web.WebFactoryConfiguration;
import com.github.flycat.web.response.ResponseFactory;
import com.github.flycat.web.request.RequestBodyHolder;
import com.github.flycat.web.request.LocalRequestBody;
import com.github.flycat.web.filter.ContentCachingHandler;
import com.github.flycat.web.filter.PostFilterAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class WebFactoryConfigurationAdapter implements WebFactoryConfiguration {

    @Override
    public ResponseFactory createResponseFactory() {
        return new ResponseFactoryAdapter();
    }

    @Override
    public ContentCachingHandler contentCachingHandler() {
        return new ContentCachingHandler() {
            @Override
            public PostFilterAction postFilter(HttpServletRequest httpServletRequest,
                                               HttpServletResponse httpServletResponse) {
                final RequestBodyHolder currentApiRequest = LocalRequestBody.getCurrentRequest();

                boolean isDebugUid = false;
                if (currentApiRequest != null && currentApiRequest.getRequestBody() != null) {
                    AppRequest apiRequest = (AppRequest) currentApiRequest.getRequestBody();
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
