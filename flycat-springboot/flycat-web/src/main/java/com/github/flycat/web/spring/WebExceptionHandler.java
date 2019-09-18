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
package com.github.flycat.web.spring;

import com.codahale.metrics.MetricRegistry;
import com.github.flycat.exception.BusinessException;
import com.github.flycat.web.api.ApiFactory;
import com.github.flycat.web.api.ApiFactoryHolder;
import com.github.flycat.web.api.ApiResponseCode;
import com.github.flycat.web.api.ApiResponseCodeUtils;
import com.github.flycat.web.util.HttpRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

@ControllerAdvice
@Component
public class WebExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebExceptionHandler.class);
    private static final MetricRegistry REGISTRY = new MetricRegistry();

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public Object handleBizException(ValidationException exception, HttpServletRequest request) {
        REGISTRY.meter(request.getRequestURI()).mark();
        String requestBody = ((HttpRequestWrapper) request).getRequestBody();
        LOGGER.error("Validation exception! uri:{}, body:{}",
                request.getRequestURL(),
                requestBody,
                exception);
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        Object validationExceptionResult = apiFactory.createValidationExceptionResult(exception);
        if (validationExceptionResult == null) {
            StringBuilder stringBuilder = new StringBuilder();
            if (exception instanceof ConstraintViolationException) {
                ConstraintViolationException exs = (ConstraintViolationException) exception;
                Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
                for (ConstraintViolation<?> item : violations) {
                    final String message = item.getMessage();
                    stringBuilder.append(message);
                    stringBuilder.append("\n");
                }
            }

            final int businessErrorCode =
                    ApiResponseCodeUtils.getBusinessErrorCode(ApiResponseCode.CLIENT_VALIDATION_ERROR);
            validationExceptionResult = apiFactory.createApiResult(businessErrorCode, stringBuilder.toString());
        }

        return validationExceptionResult;
    }


    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Object handleBizException(BusinessException exception, HttpServletRequest request) {
        REGISTRY.meter(request.getRequestURI()).mark();
        String requestBody = ((HttpRequestWrapper) request).getRequestBody();
        LOGGER.error("Business exception! uri:{}, body:{}",
                request.getRequestURL(),
                requestBody,
                exception);
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        return apiFactory.createApiResult(exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Object handleApiException(Throwable ex, HttpServletRequest request) {
        REGISTRY.meter(request.getRequestURI()).mark();
        String requestBody = ((HttpRequestWrapper) request).getRequestBody();
        LOGGER.error("Uncaught exception! uri:{}, body:{}, ", request.getRequestURL(), requestBody, ex);
        final Object unknownExceptionResult = ApiResponseCodeUtils.getUnknownExceptionResult(ex);
        return unknownExceptionResult;
    }
}
