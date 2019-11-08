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
package com.github.flycat.web.spring;

import com.github.flycat.log.MDCUtils;
import com.github.flycat.util.CommonUtils;
import com.github.flycat.web.WebConfigurationLoader;
import com.github.flycat.web.filter.ContentCachingHandler;
import com.github.flycat.web.filter.PostFilterAction;
import com.github.flycat.web.request.LocalRequestBody;
import com.github.flycat.web.request.RequestBodyHolder;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class ContentCachingFilter implements Filter {
    private static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("request");

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentCachingFilter.class);
    private final PostProcessExceptionHandler postProcessExceptionHandler;

    public ContentCachingFilter(PostProcessExceptionHandler postProcessExceptionHandler) {
        this.postProcessExceptionHandler = postProcessExceptionHandler;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException {

        Stopwatch started = Stopwatch.createStarted();
        boolean needValidate = false;

        ContentCachingHttpServletRequest requestWrapper = new ContentCachingHttpServletRequest((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper((HttpServletResponse) response);

        final String requestURI = requestWrapper.getDecodedRequestURI();
        String method = requestWrapper.getMethod();
        MDC.put(MDCUtils.REQ_ID, CommonUtils.getUUIDWithoutHyphen());

        final ContentCachingHandler contentCachingHandler = WebConfigurationLoader.getContentCachingHandler();
        try {
            if (contentCachingHandler.executeNextFilter(requestWrapper, responseWrapper)) {
                chain.doFilter(requestWrapper, responseWrapper);
            }
        } catch (Throwable e) {
            postProcessExceptionHandler.handle(requestWrapper, responseWrapper, e);
        } finally {

            started.stop();

            final RequestBodyHolder currentApiRequest = LocalRequestBody.getCurrentRequest();

            String responseContent = null;

            final PostFilterAction postFilterAction = contentCachingHandler.postFilter(requestWrapper, responseWrapper);

            if (postFilterAction.isReadResponse()) {
                responseContent = getPayLoad(responseWrapper.getContentAsByteArray(), responseWrapper
                        .getCharacterEncoding());
                responseContent = contentCachingHandler.replaceResponse(responseContent);
//                responseContent = ConfUtils.filterContent(responseContent);
                responseWrapper.resetBuffer();
                responseWrapper.getWriter().write(responseContent);
            }

            responseWrapper.copyBodyToResponse();
            String requestBody = requestWrapper.getDecodedRequestBody();

            if (postFilterAction.isLogResponse()) {
                LOGGER.info("Debug info, uri {}, request:{}, validate status {}, " +
                                " execute time {}, request params {}, method {},"
                                + " response:{}",
                        requestURI,
                        currentApiRequest.getRequestBody(),
                        needValidate ? "validated" : "ignored",
                        started, requestBody, method, responseContent);
            }

            String responseSpeed;
            long elapsed = started.elapsed(TimeUnit.MILLISECONDS);
            if (elapsed > 1000) {
                responseSpeed = "slow";
            } else if (elapsed > 500) {
                responseSpeed = "delay";
            } else {
                responseSpeed = "normal";
            }

            REQUEST_LOGGER.info("Request uri {}, validate status {}, execute time {}, request params {}, method {}, " +
                            "response speed:{}",
                    requestURI,
                    needValidate ? "validated" : "ignored",
                    started, requestBody, method, responseSpeed);
            // Write request and response body, headers, timestamps etc. to log files
        }
    }

    private String getPayLoad(byte[] buf, String characterEncoding) {
        String payload = "";
        if (buf == null) {
            return payload;
        }
        if (buf.length > 0) {
            try {
                payload = new String(buf, characterEncoding);
            } catch (UnsupportedEncodingException ex) {
                payload = "[unknown]";
            }
        }
        return payload;
    }
}
