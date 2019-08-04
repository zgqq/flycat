package com.github.bootbox.web.filter;

import com.alibaba.fastjson.JSON;
import com.github.bootbox.web.BootboxWebHolder;
import com.github.bootbox.web.api.ApiFactory;
import com.github.bootbox.web.api.ApiFactoryHolder;
import com.github.bootbox.web.api.ApiHttpRequest;
import com.github.bootbox.web.api.ApiRequestHolder;
import com.github.bootbox.web.exception.BusinessException;
import com.github.bootbox.web.util.HttpRequestWrapper;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.NestedServletException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ContentCachingFilter implements Filter {
    private static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("request");

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentCachingFilter.class);


    public ContentCachingFilter() {

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

        HttpRequestWrapper requestWrapper = new HttpRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper((HttpServletResponse) response);

        String uri = requestWrapper.getRequestURI();
        String method = requestWrapper.getMethod();
//        MDC.put("HttpUri", uri);
//        MDC.put("HttpMethod", method);

        final ContentCachingHandler contentCachingHandler = BootboxWebHolder.getContentCachingHandler();
        try {
            if (contentCachingHandler.executeNextFilter(requestWrapper, responseWrapper)) {
                chain.doFilter(requestWrapper, responseWrapper);
            }

//            if (AppConf.isResponseMaintaining()) {
//                HttpResponseUtils.writeError(responseWrapper, AppConf.getMaintainConfig().getResponseMsg(),
//                        ResultCode.BIZ_MAINTAINING);
//            } else {
//                chain.doFilter(requestWrapper, responseWrapper);
//            }
        } catch (Throwable e) {
            Throwable cause = e;
            if (e instanceof NestedServletException) {
                cause = e.getCause();
            }
            final String requestBody = requestWrapper.getRequestBody();
            if (cause instanceof BusinessException) {
                LOGGER.error("BusinessException! uri {}, params {}, method {}", uri, requestBody, method, e);
                BusinessException e1 = (BusinessException) cause;
                final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
                String output = JSON.toJSONString(apiFactory.createApiResult(e1.getErrorCode(), e1.getMessage()));
                response.getWriter().write(output);
            } else {
                LOGGER.error("System error! uri {}, params {}, method {}", uri, requestBody, method, e);
                final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
                Object unknownExceptionResult = apiFactory.createUnknownExceptionResult();
                if (unknownExceptionResult == null) {
                    unknownExceptionResult = apiFactory.createApiResult(500, "服务器傲娇了~");
                }
                String output = JSON.toJSONString(unknownExceptionResult);
                response.getWriter().write(output);
            }
        } finally {
//            MDC.clear();

            started.stop();

            final ApiHttpRequest currentApiRequest = ApiRequestHolder.getCurrentApiRequest();

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
            String requestBody = requestWrapper.getRequestBody();

            if (postFilterAction.isLogResponse()) {
                LOGGER.info("Debug info, uri {}, request:{}, validate status {}, " +
                                " execute time {}, request params {}, method {},"
                                + " response:{}",
                        requestWrapper.getRequestURI(),
                        currentApiRequest.getApiRequest(),
                        needValidate ? "validated" : "ignored",
                        started, requestBody, method, responseContent);
            }


            REQUEST_LOGGER.info("Request uri {}, validate status {},  execute time {}, request params {}, method {}",
                    requestWrapper.getRequestURI(),
                    needValidate ? "validated" : "ignored",
                    started, requestBody, method);
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
