package com.github.bootbox.security.token;

import com.github.bootbox.server.event.EventManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public abstract class AbstractTokenAccessDeniedHandler implements AccessDeniedHandler {
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

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
