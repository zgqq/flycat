package com.github.flycat.web.spring;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static String extractVariablePath(HttpServletRequest request) {
        // /elements/CATEGORY1/CATEGORY1_1/ID
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // /elements/**
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // CATEGORY1/CATEGORY1_1/ID
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }
}
