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

import com.github.flycat.event.EventManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public abstract class AbstractTokenAccessDeniedHandler implements AccessDeniedHandler {
    private final AuthenticationTrustResolver trustResolver;

    protected AbstractTokenAccessDeniedHandler(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (trustResolver.isAnonymous(authentication)) {
            EventManager.post(new InvalidTokenEvent(request, response, accessDeniedException));
            handleInvalidToken(request, response, accessDeniedException);
        } else {
            EventManager.post(new AccessDeniedEvent(request, response, accessDeniedException));
            handleAccessDenied(request, response, accessDeniedException);
        }
    }

    protected abstract void handleInvalidToken(HttpServletRequest request, HttpServletResponse response,
                                               AccessDeniedException accessDeniedException);

    protected abstract void handleAccessDenied(HttpServletRequest request, HttpServletResponse response,
                                               AccessDeniedException accessDeniedException);
}
