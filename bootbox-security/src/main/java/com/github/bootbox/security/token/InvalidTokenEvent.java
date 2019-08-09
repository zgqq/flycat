package com.github.bootbox.security.token;

import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InvalidTokenEvent {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final AccessDeniedException accessDeniedException;

    public InvalidTokenEvent(HttpServletRequest request, HttpServletResponse response,
                             AccessDeniedException accessDeniedException) {
        this.request = request;
        this.response = response;
        this.accessDeniedException = accessDeniedException;
    }


    public HttpServletRequest getRequest() {
        return request;
    }


    public HttpServletResponse getResponse() {
        return response;
    }


    public AccessDeniedException getAccessDeniedException() {
        return accessDeniedException;
    }
}
