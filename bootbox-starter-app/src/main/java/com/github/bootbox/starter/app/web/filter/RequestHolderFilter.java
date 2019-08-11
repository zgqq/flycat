package com.github.bootbox.starter.app.web.filter;

import com.alibaba.fastjson.JSONObject;
import com.github.bootbox.web.api.ApiHttpRequest;
import com.github.bootbox.web.api.ApiRequestHolder;
import com.github.bootbox.web.util.HttpRequestWrapper;
import com.github.bootbox.starter.app.web.api.AppRequest;
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

    private static final String TOKEN = "whosyourdaddy";
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
        HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper) request;
        String method = httpServletRequest.getMethod();

        if (method.equalsIgnoreCase("OPTIONS")) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String jsonString = null;
        AppRequest jo = null;

        try {
            jsonString = httpRequestWrapper.getRequestBody();
            jo = JSONObject.parseObject(jsonString, AppRequest.class);

        } catch (Throwable throwable) {
        }

        ApiRequestHolder.setCurrentApiRequest(new ApiHttpRequest((HttpRequestWrapper) httpServletRequest,
                jo
        ));

        if (jo != null) {
            if (jo.getUid() != null) {
                MDC.put("request.uid", jo.getUid() + "");
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
