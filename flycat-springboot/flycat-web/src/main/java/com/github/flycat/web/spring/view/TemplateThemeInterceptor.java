package com.github.flycat.web.spring.view;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplateThemeInterceptor extends TemplateThemeHandler implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!(handler instanceof ResourceHttpRequestHandler)) {
            modelAndView.setViewName(theme + modelAndView.getViewName());
        }
    }
}
