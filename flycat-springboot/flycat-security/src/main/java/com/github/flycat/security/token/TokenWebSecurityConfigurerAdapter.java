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
package com.github.flycat.security.token;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public abstract class TokenWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    public TokenWebSecurityConfigurerAdapter() {
        super(true);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final TokenAuthenticationTrustResolver trustResolver = new TokenAuthenticationTrustResolver();
        http.setSharedObject(AuthenticationTrustResolver.class,
                trustResolver);
        final TokenAuthenticationService tokenAuthenticationService = tokenAuthenticationService();
        http.apply(new TokenFilterConfigurer(tokenAuthenticationService));
        http
                .authenticationProvider(new TokenAuthenticationProvider(tokenAuthenticationService))
                .exceptionHandling()
                .accessDeniedHandler(new DefaultTokenAccessDeniedHandler(trustResolver))
                .and()
                .csrf().disable();
    }

    protected abstract TokenAuthenticationService tokenAuthenticationService();
}
