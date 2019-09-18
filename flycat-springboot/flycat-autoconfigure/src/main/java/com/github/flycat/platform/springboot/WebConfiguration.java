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
package com.github.flycat.platform.springboot;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.flycat.web.FlycatWebConfiguration;
import com.github.flycat.web.FlycatWebHolder;
import com.github.flycat.web.spring.ContentCachingFilter;
import com.github.flycat.web.spring.FilterOrder;
import com.github.flycat.web.spring.WebCorsFilter;
import com.github.flycat.web.spring.WebExceptionHandler;
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
@ConditionalOnClass(FlycatWebConfiguration.class)
public class WebConfiguration {

    @Bean
    public FilterRegistrationBean cacheRequestFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter customFilter = new ContentCachingFilter();
        registration.setFilter(customFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(FilterOrder.CONTENT_CACHING_FILTER);
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
    @ConditionalOnMissingBean(FlycatWebConfiguration.class)
    public FlycatWebConfiguration defaultFlycatWebConfiguration() {
        return new FlycatWebConfiguration() {
        };
    }

    @Autowired
    FlycatWebConfiguration flycatWebConfiguration;

    @PostConstruct
    public void configureFlycatWeb() {
        FlycatWebHolder.load(flycatWebConfiguration);
    }
}
