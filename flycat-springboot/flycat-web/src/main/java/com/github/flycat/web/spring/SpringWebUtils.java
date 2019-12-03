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
package com.github.flycat.web.spring;

import com.github.flycat.util.StringUtils;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

public class SpringWebUtils {

    public static String redirect(String url) {
        return "redirect:" + StringUtils.encodeURLExceptSlash(url);
    }

    public static ContentCachingHttpServletRequest getContentCachingHttpServletRequest(HttpServletRequestWrapper request) {
        HttpServletRequestWrapper requestWrapper = request;
        for (; ; ) {
            if (requestWrapper == null) {
                break;
            }
            if (requestWrapper instanceof ContentCachingHttpServletRequest) {
                break;
            } else {
                requestWrapper = (HttpServletRequestWrapper) requestWrapper.getRequest();
            }
        }
        return (ContentCachingHttpServletRequest) requestWrapper;
    }

    public static String extractVariablePath(HttpServletRequest request) {
        // /elements/CATEGORY1/CATEGORY1_1/ID
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // /elements/**
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // CATEGORY1/CATEGORY1_1/ID
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

    public static void attributeToModelIfNotNull(HttpSession session, Model model, String name) {
        final String value = (String) session.getAttribute(name);
        if (StringUtils.isNotBlank(value)) {
            model.addAttribute(name, value);
            session.removeAttribute(name);
        }
    }
}
