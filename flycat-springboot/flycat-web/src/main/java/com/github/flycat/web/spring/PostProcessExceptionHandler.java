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

import com.github.flycat.context.ContextUtils;
import com.github.flycat.exception.BusinessException;
import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.util.ExceptionUtils;
import com.github.flycat.util.io.IOUtils;
import com.github.flycat.web.WebException;
import com.github.flycat.web.context.ExceptionContext;
import com.github.flycat.web.response.ResponseBodyUtils;
import com.github.flycat.web.response.ResponseFactory;
import com.github.flycat.web.response.ResponseFactoryHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.NestedServletException;

import java.io.IOException;

public class PostProcessExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostProcessExceptionHandler.class);

    private final HandlerMappingContext handlerMappingContext;
    private final String errorHtml;

    public PostProcessExceptionHandler(HandlerMappingContext handlerMappingContext) {
        this.handlerMappingContext = handlerMappingContext;
        try {
            this.errorHtml = IOUtils.getFileContentByClasspath("500.html");
        } catch (IOException e) {
            throw new WebException("Unable to load 500 html", e);
        }
    }

    public void handle(ContentCachingHttpServletRequest requestWrapper,
                       ContentCachingResponseWrapper responseWrapper,
                       Throwable e) {
        try {
            final String requestURI = requestWrapper.getDecodedRequestURI();
            String method = requestWrapper.getMethod();
            Throwable cause = e;
            if (e instanceof NestedServletException) {
                cause = e.getCause();
            }
            final boolean responseBody = handlerMappingContext.isResponseBody(requestWrapper);
            if (responseBody) {
                final String requestBody = requestWrapper.getDecodedRequestBody();
                if (cause instanceof BusinessException) {
                    LOGGER.error("BusinessException! uri {}, params {}, method {}", requestURI, requestBody, method, e);
                    BusinessException e1 = (BusinessException) cause;
                    final ResponseFactory factoryResponse = ResponseFactoryHolder.getResponseFactory();
                    String output = JsonUtils.toJsonString(factoryResponse.createResponse(e1.getErrorCode(), e1.getMessage()));
                    responseWrapper.getWriter().write(output);
                } else {
                    LOGGER.error("System error! uri {}, params {}, method {}", requestURI, requestBody, method, e);
                    final Object unknownExceptionResult = ResponseBodyUtils.getUnknownExceptionResult(
                            new ExceptionContext(cause, true));
                    String output = JsonUtils.toJsonString(unknownExceptionResult);
                    responseWrapper.getWriter().write(output);
                }
            } else {
                final String requestBody = requestWrapper.getDecodedRequestBody();
                LOGGER.error("System error! uri {}, params {}, method {}", requestURI, requestBody, method, e);
                if (ContextUtils.isTestProfile()) {
                    final String stackTrace = ExceptionUtils.getStackTrace(cause);
                    final StringBuilder stringBuilder = new StringBuilder();
                    String[] lines = stackTrace.split("\\r?\\n");
                    for (String line : lines) {
                        stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        stringBuilder.append(line);
                        stringBuilder.append("\n");
                    }
                    final String htmlStackTrace = stringBuilder.toString().replace("\n", "<br>")
                            .replace("\t", "    ");
                    responseWrapper.getWriter().write(htmlStackTrace);
                } else {
                    responseWrapper.getWriter().write(errorHtml);
                }
            }
        } catch (Throwable ex) {
            throw new WebException("Unable to handle exception", ex);
        }
    }
}
