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

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequireTokenAuthenticationFilter extends GenericFilterBean {

    private final TokenAuthenticationService tokenAuthenticationService;

    protected RequireTokenAuthenticationFilter(TokenAuthenticationService tokenAuthenticationService) {
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        TokenInformation tokenInformation = this.tokenAuthenticationService
                .authenticateToken((HttpServletRequest) request);
        if (tokenInformation == null) {
            throw new AuthenticationServiceException("Token authenticateToken should not be null!");
        }
        TokenAuthentication tokenAuthentication = new TokenAuthentication(
                tokenInformation.getUserId(), tokenInformation.getToken(),
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                tokenInformation.isValid()
        );
        Authentication authentication = tokenAuthentication;
        if (!tokenAuthentication.isValid()) {
            if (tokenAuthentication.getPrincipal() == null) {
                authentication =
                        new AnonymousAuthenticationToken(RequireTokenAuthenticationFilter.class.getName(),
                                "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
            } else {
                authentication =
                        new AnonymousAuthenticationToken(RequireTokenAuthenticationFilter.class.getName(),
                                "invalidUser", AuthorityUtils.createAuthorityList("ROLE_INVALID_USER"));
            }
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
