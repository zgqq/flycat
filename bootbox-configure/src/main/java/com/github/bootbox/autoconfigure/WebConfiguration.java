package com.github.bootbox.autoconfigure;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.bootbox.web.BootboxWebConfiguration;
import com.github.bootbox.web.BootboxWebHolder;
import com.github.bootbox.web.filter.ContentCachingFilter;
import com.github.bootbox.web.filter.WebCorsFilter;
import com.github.bootbox.web.spring.WebExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.List;

@Configuration
@ConditionalOnClass(BootboxWebConfiguration.class)
public class WebConfiguration {

    @Bean
    public FilterRegistrationBean cacheRequestFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter customFilter = new ContentCachingFilter();
        registration.setFilter(customFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean logbackFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter customFilter = new ch.qos.logback.classic.helpers.MDCInsertingServletFilter();
        registration.setFilter(customFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter customFilter = new WebCorsFilter();
        registration.setFilter(customFilter);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public WebExceptionHandler webExceptionHandler() {
        return new WebExceptionHandler();
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
                // 会优先加载 jackson，所以设置第一位
                converters.add(0, converter);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(BootboxWebConfiguration.class)
    public BootboxWebConfiguration bootboxWebConfiguration() {
        return new BootboxWebConfiguration() {
        };
    }

    @Autowired
    BootboxWebConfiguration bootboxWebConfiguration;

    @PostConstruct
    public void configureBootboxWeb() {
        BootboxWebHolder.load(bootboxWebConfiguration);
    }
}
