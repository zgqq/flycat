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
package com.github.flycat.platform.springboot.web;

import com.github.flycat.web.spring.view.DynamicViewNameTranslator;
import com.github.flycat.web.spring.view.TemplateThemeInterceptor;
import com.github.flycat.web.spring.view.TemplateThemeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TemplateThemeConfiguration {

    @Bean
    public static WebMvcConfigurer templateThemeInterceptor() {
        final TemplateThemeInterceptor templateThemeInterceptor = new TemplateThemeInterceptor();
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(templateThemeInterceptor);
            }
        };
    }

    @Bean
    public TemplateThemeResolver templateThemeResolver(@Autowired DynamicViewNameTranslator dynamicViewNameTranslator) {
        return new TemplateThemeResolver(dynamicViewNameTranslator);
    }
}
