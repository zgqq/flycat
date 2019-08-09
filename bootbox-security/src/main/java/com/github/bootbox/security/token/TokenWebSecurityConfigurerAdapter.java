package com.github.bootbox.security.token;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.SessionManagementFilter;

public abstract class TokenWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    public TokenWebSecurityConfigurerAdapter(boolean disableDefaults) {
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
                .accessDeniedHandler(new DefaultTokenAccessDeniedHandler());
    }

    protected abstract TokenAuthenticationService tokenAuthenticationService();
}
