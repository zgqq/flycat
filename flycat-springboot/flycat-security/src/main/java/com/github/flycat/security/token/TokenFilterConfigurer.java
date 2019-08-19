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

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class TokenFilterConfigurer extends AbstractHttpConfigurer<TokenFilterConfigurer, HttpSecurity> {

    private final TokenAuthenticationService tokenAuthenticationService;

    public TokenFilterConfigurer(TokenAuthenticationService tokenAuthenticationService) {
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        final AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        final RequireTokenAuthenticationFilter authenticationFilter = new RequireTokenAuthenticationFilter(tokenAuthenticationService);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        builder.addFilter(authenticationFilter);
    }
}
