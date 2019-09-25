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
