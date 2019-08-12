package com.github.bootbox.starter.app.web;

import com.github.bootbox.starter.app.config.AppConf;
import com.github.bootbox.starter.app.web.api.AppRequest;
import com.github.bootbox.starter.app.web.api.Result;
import com.github.bootbox.starter.app.web.filter.RequestHolderFilter;
import com.github.bootbox.util.StringReplacer;
import com.github.bootbox.web.BootboxWebConfiguration;
import com.github.bootbox.web.api.ApiFactory;
import com.github.bootbox.web.api.ApiHttpRequest;
import com.github.bootbox.web.api.ApiRequestHolder;
import com.github.bootbox.web.filter.ContentCachingHandler;
import com.github.bootbox.web.filter.FilterOrder;
import com.github.bootbox.web.filter.PostFilterAction;
import com.github.bootbox.web.util.HttpRequestWrapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.Filter;
import java.util.List;
import java.util.Map;

@Configuration
public class WebAutoConfiguration {

    @Bean
    public BootboxWebConfiguration bootboxWebConfiguration() {
        return new BootboxWebConfiguration() {
            @Override
            public ApiFactory createApiFactory() {
                return new ApiFactory() {
                    @Override
                    public Object createApiResult(int code, String message) {
                        return new Result<>(code, message);
                    }
                };
            }

            @Override
            public ContentCachingHandler contentCachingHandler() {
                return new ContentCachingHandler() {
                    @Override
                    public PostFilterAction postFilter(HttpRequestWrapper requestWrapper,
                                                       ContentCachingResponseWrapper responseWrapper) {
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
                    public boolean executeNextFilter(HttpRequestWrapper requestWrapper,
                                                     ContentCachingResponseWrapper responseWrapper) {
                        return true;
                    }
                };
            }
        };
    }


    @Bean
    public FilterRegistrationBean requestHolderFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter customFilter = new RequestHolderFilter();
        registration.setFilter(customFilter);
        registration.setOrder(FilterOrder.CONTENT_CACHING_FILTER + 1);
        registration.addUrlPatterns("/*");
        return registration;
    }

}
