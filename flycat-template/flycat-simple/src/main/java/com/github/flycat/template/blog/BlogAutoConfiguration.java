package com.github.flycat.template.blog;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class BlogAutoConfiguration {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        return paginationInterceptor;
    }

    @Bean
    public WebSecurityConfigurerAdapter webSecurityConfigurationAdapter() {
        return new WebSecurityConfigurerAdapter() {
            @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                super.configure(auth);
            }

            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http
                        .authorizeRequests()
                        .anyRequest().permitAll()
                        .and()
                        .formLogin().and()
                        .httpBasic();
            }
        };
    }
}
