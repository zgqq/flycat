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
package com.github.flycat.security.token;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.SessionManagementFilter;

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
        final TokenAuthenticationService tokenAuthenticationService = tokenAuthenticationService();
        http.addFilterBefore(new RequireTokenAuthenticationFilter(tokenAuthenticationService),
                SessionManagementFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(new DefaultTokenAccessDeniedHandler()).and()
                .csrf().disable();
    }

    protected abstract TokenAuthenticationService tokenAuthenticationService();
}
