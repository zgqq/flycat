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

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.flycat.context.ContextUtils;
import com.github.flycat.platform.springboot.web.SmoothTomcatWebServerCustomizer;
import com.github.flycat.util.StringUtils;
import com.github.flycat.web.WebConfigurationLoader;
import com.github.flycat.web.WebFactoryConfiguration;
import com.github.flycat.web.spring.*;
import com.github.flycat.web.spring.view.DynamicViewNameTranslator;
import com.google.common.collect.Sets;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.system.SystemProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Configuration
//@ConditionalOnClass(WebFactoryConfiguration.class)
public class WebConfiguration {

//    @Bean(name = DispatcherServlet.REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME)
//    public static DynamicViewNameTranslator requestToViewNameTranslator() {
//        return new DynamicViewNameTranslator();
//    }

    @Bean
    public FilterRegistrationBean cacheRequestFilter(HandlerMappingContext handlerMappingContext) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter customFilter = new ContentCachingFilter(new PostProcessExceptionHandler(handlerMappingContext));
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
    public FilterRegistrationBean webCorsFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter customFilter = new WebCorsFilter();
        registration.setFilter(customFilter);
        registration.addUrlPatterns("/*");
        return registration;
    }


    @Bean
    public HandlerMappingContext handlerMappingContext() {
        return new HandlerMappingContext();
    }

    @Bean
    public WebExceptionHandler webExceptionHandler(HandlerMappingContext handlerMappingContext) {
        return new WebExceptionHandler(handlerMappingContext);
    }


    @Configuration
    @ConditionalOnClass(FastJsonHttpMessageConverter.class)
    public static class FastJsonConfiguration {
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
    }

    @Configuration
    @ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
//    @ConditionalOnBean(Jackson2ObjectMapperBuilder.class)
    public static class JacksonConfiguration {

        @Bean
        public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(Jackson2ObjectMapperBuilder
                                                                                               build,
                                                                                       ObjectMapper objectMapper) {
            if (ContextUtils.isTestProfile()) {
                final ObjectMapper prettyMapper = build.createXmlMapper(false)
                        .featuresToEnable(SerializationFeature.INDENT_OUTPUT).build();
                return new MappingJackson2HttpMessageConverter(prettyMapper);
            }
            return new MappingJackson2HttpMessageConverter(objectMapper);
        }
    }


//    @Bean
//    @ConditionalOnMissingBean(WebFactoryConfiguration.class)
//    public WebFactoryConfiguration defaultFlycatWebConfiguration() {
//        return new WebFactoryConfiguration() {
//        };
//    }

    @Autowired
    WebFactoryConfiguration webFactoryConfiguration;

    @PostConstruct
    public void configureWeb() {
        WebConfigurationLoader.load(webFactoryConfiguration);
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


    @Configuration
    @ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
    @ConditionalOnProperty(value = "flycat.kill-after-started")
    public static class TomcatConfiguration {
        @Bean
        public SmoothTomcatWebServerCustomizer tomcatWebServer() {
            final String killAfterStartedConf = ContextUtils.createContextFreeConfiguration().getString("flycat.kill-after-started");
            boolean killAfterStarted = ContextUtils.isTestProfile();
            if (StringUtils.isNotBlank(killAfterStartedConf)) {
                killAfterStarted = "true".equalsIgnoreCase(killAfterStartedConf);
            }
            return new SmoothTomcatWebServerCustomizer(killAfterStarted);
        }
    }
}
