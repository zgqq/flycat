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
package com.github.flycat.starter.app.web.filter;

import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.starter.app.web.api.AppRequest;
import com.github.flycat.web.request.LocalRequestBody;
import com.github.flycat.web.request.RequestBodyHolder;
import com.github.flycat.web.spring.ContentCachingHttpServletRequest;
import com.github.flycat.web.util.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by zgq
 * Date: 17-8-29
 * Time: 上午11:08
 */
public class RequestHolderFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHolderFilter.class);

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public RequestHolderFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        ContentCachingHttpServletRequest httpRequestWrapper = (ContentCachingHttpServletRequest) request;
        String method = httpServletRequest.getMethod();

        if (method.equalsIgnoreCase("OPTIONS")) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        final String contentType = httpServletRequest.getHeader(HttpConstants.CONTENT_TYPE);
        boolean readAppRequest = false;
        if (contentType != null) {
            readAppRequest = contentType.startsWith(HttpConstants.APPLICATION_JSON_VALUE);
        }

        if (readAppRequest) {
            String jsonString = null;
            AppRequest jo = null;

            try {
                jsonString = httpRequestWrapper.getRequestBody();
                jo = JsonUtils.parseObject(jsonString, AppRequest.class);

            } catch (Throwable throwable) {
            }

            LocalRequestBody.setCurrentApiRequest(new RequestBodyHolder(httpServletRequest,
                    jo
            ));

            if (jo != null) {
                if (jo.getUid() != null) {
                    MDC.put("request.uid", jo.getUid() + "");
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOGGER.info("Closing token filter");
        executorService.shutdown();
    }
}
