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

import com.codahale.metrics.MetricRegistry;
import com.github.flycat.exception.BusinessException;
import com.github.flycat.web.context.ExceptionContext;
import com.github.flycat.web.response.ResponseBodyUtils;
import com.github.flycat.web.response.ResponseCode;
import com.github.flycat.web.response.ResponseFactory;
import com.github.flycat.web.response.ResponseFactoryHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

@ControllerAdvice
@Component
public class WebExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebExceptionHandler.class);
    private static final MetricRegistry REGISTRY = new MetricRegistry();

    private final HandlerMappingContext handlerMappingContext;

    @Autowired
    public WebExceptionHandler(HandlerMappingContext handlerMappingContext) {
        this.handlerMappingContext = handlerMappingContext;
    }

    @ExceptionHandler(ValidationException.class)
    public Object handleValidationException(ValidationException exception, HttpServletRequest request) {
        REGISTRY.meter(request.getRequestURI()).mark();

        HttpServletRequestWrapper requestWrapper = getHttpServletRequestWrapper((HttpServletRequestWrapper) request);
        String requestBody = getRequestBody(requestWrapper);


        LOGGER.error("Validation exception! uri:{}, body:{}",
                request.getRequestURL(),
                requestBody,
                exception);

        final boolean responseBody = this.handlerMappingContext.isResponseBody(request);
        if (responseBody) {
            final ResponseFactory apiFactory = ResponseFactoryHolder.getResponseFactory();
            Object validationExceptionResult = apiFactory.createValidationExceptionResponse(exception);
            if (validationExceptionResult == null) {
                StringBuilder stringBuilder = getMessage(exception);
                final int businessErrorCode =
                        ResponseBodyUtils.getBusinessErrorCode(ResponseCode.CLIENT_VALIDATION_ERROR);
                validationExceptionResult = apiFactory.createResponse(businessErrorCode, stringBuilder.toString());
            }
            return newResponseEntity(validationExceptionResult);
        } else {
            StringBuilder message = getMessage(exception);
            return getModelAndView(request, message.toString());
        }
    }

    private Object getModelAndView(HttpServletRequest request, String message) {
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(handlerMappingContext.getViewName(request));
        modelAndView.addObject("errorMsg", message);
        return modelAndView;
    }

    private StringBuilder getMessage(ValidationException exception) {
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
        return stringBuilder;
    }

    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException exception, HttpServletRequest request) {
        REGISTRY.meter(request.getRequestURI()).mark();

        HttpServletRequestWrapper requestWrapper = getHttpServletRequestWrapper((HttpServletRequestWrapper) request);
        String requestBody = getRequestBody(requestWrapper);

        LOGGER.error("Business exception! uri:{}, body:{}",
                request.getRequestURL(),
                requestBody,
                exception);

        final boolean responseBody = this.handlerMappingContext.isResponseBody(request);
        if (responseBody) {
            final ResponseFactory apiFactory = ResponseFactoryHolder.getResponseFactory();
            return newResponseEntity(apiFactory.createResponse(exception.getErrorCode(), exception.getMessage()));
        } else {
            return getModelAndView(request, exception.getMessage());
        }
    }

    @ExceptionHandler(Throwable.class)
    public Object handleApiException(Throwable ex, HttpServletRequest request) {
        REGISTRY.meter(request.getRequestURI()).mark();

        HttpServletRequestWrapper requestWrapper = getHttpServletRequestWrapper((HttpServletRequestWrapper) request);
        String requestBody = getRequestBody(requestWrapper);

        LOGGER.error("Uncaught exception! uri:{}, body:{}, ", request.getRequestURL(), requestBody, ex);
        final boolean responseBody = handlerMappingContext.isResponseBody(requestWrapper);
        final Object unknownExceptionResult = ResponseBodyUtils.getUnknownExceptionResult(
                new ExceptionContext(ex, responseBody)
        );
        if (responseBody) {
            return newResponseEntity(unknownExceptionResult);
        } else {
            final ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("error");
            modelAndView.addObject("error", unknownExceptionResult);
            return modelAndView;
        }
    }

    private String getRequestBody(HttpServletRequestWrapper requestWrapper) {
        String requestBody = null;
        if (requestWrapper instanceof ContentCachingHttpServletRequest) {
            requestBody = ((ContentCachingHttpServletRequest) requestWrapper).getRequestBody();
        }
        return requestBody;
    }

    private HttpServletRequestWrapper getHttpServletRequestWrapper(HttpServletRequestWrapper request) {
        HttpServletRequestWrapper requestWrapper = request;
        for (; ; ) {
            if (requestWrapper instanceof ContentCachingHttpServletRequest) {
                break;
            } else {
                requestWrapper = (HttpServletRequestWrapper) requestWrapper.getRequest();
            }
        }
        return requestWrapper;
    }

    private Object newResponseEntity(Object result) {
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
