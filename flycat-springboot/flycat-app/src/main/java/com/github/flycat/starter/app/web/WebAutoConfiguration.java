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
package com.github.flycat.starter.app.web;

import com.github.flycat.starter.app.web.filter.RequestHolderFilter;
import com.github.flycat.web.WebFactoryConfiguration;
import com.github.flycat.web.spring.FilterOrder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.Filter;

@Configuration
public class WebAutoConfiguration {

    @Bean
    public WebFactoryConfiguration webConfigurationAdapter() {
        return new WebFactoryConfigurationAdapter();
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
