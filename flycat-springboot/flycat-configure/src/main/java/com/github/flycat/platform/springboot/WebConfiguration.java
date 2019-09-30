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
package com.github.flycat.platform.springboot;

import com.github.flycat.web.FlycatWebConfiguration;
import com.github.flycat.web.FlycatWebHolder;
import com.github.flycat.web.spring.*;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

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
                CustomFastJsonHttpMessageConverter converter = new CustomFastJsonHttpMessageConverter();
                converter.afterPropertiesSet();
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


    @Bean
    @ConditionalOnProperty(value = "flycat.web.rest-prefix")
    public WebMvcRegistrations webMvcRegistrationsHandlerMapping(@Value("${flycat.web.rest-prefix}")
                                                                         String restPrefix,
                                                                 @Value("${flycat.web.rest-prefix-required:false}")
                                                                         boolean required) {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new RequestMappingHandlerMapping() {

                    @Override
                    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
                        Class<?> beanType = method.getDeclaringClass();
                        if (AnnotationUtils.findAnnotation(beanType, RestController.class) != null) {
                            PatternsRequestCondition apiPattern;
                            apiPattern = new PatternsRequestCondition(restPrefix)
                                    .combine(mapping.getPatternsCondition());
                            if (!required) {
                                Set<String> allPatterns = Sets.newHashSet(mapping.getPatternsCondition().getPatterns());
                                allPatterns.addAll(apiPattern.getPatterns());
                                apiPattern = new PatternsRequestCondition(allPatterns.toArray(new String[]{}));
                            }

                            mapping = new RequestMappingInfo(mapping.getName(), apiPattern,
                                    mapping.getMethodsCondition(), mapping.getParamsCondition(),
                                    mapping.getHeadersCondition(), mapping.getConsumesCondition(),
                                    mapping.getProducesCondition(), mapping.getCustomCondition());
                        }

                        super.registerHandlerMethod(handler, method, mapping);
                    }
                };
            }
        };
    }


}
