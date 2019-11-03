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
