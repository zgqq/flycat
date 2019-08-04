package com.github.bootbox.web.spring;

import com.github.bootbox.web.api.ApiFactory;
import com.github.bootbox.web.api.ApiFactoryHolder;
import com.github.bootbox.web.exception.BusinessException;
import com.github.bootbox.web.util.HttpRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class WebExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Object handleBizException(BusinessException biz, HttpServletRequest request) {
        String requestBody = ((HttpRequestWrapper) request).getRequestBody();
        LOGGER.error("Business exception! uri:{}, body:{}",
                request.getRequestURL(),
                requestBody,
                biz);
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        return apiFactory.createApiResult(biz.getErrorCode(), biz.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Object handleApiException(Throwable ex, HttpServletRequest request) {
        String requestBody = ((HttpRequestWrapper) request).getRequestBody();
        LOGGER.error("Uncaught exception! uri:{}, body:{}, ", request.getRequestURL(), requestBody, ex);
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        final Object unknownExceptionResult = apiFactory.createUnknownExceptionResult();
        if (unknownExceptionResult != null) {
            return unknownExceptionResult;
        }
        return apiFactory.createApiResult(500, "服务器傲娇了!");
    }
}
