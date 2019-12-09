package com.github.flycat.web.spring.view;

import com.github.flycat.web.spring.SpringWebUtils;

public class TemplateThemePrefixResolver {

    public static String resolve(String theme, String viewName) {
        if (!SpringWebUtils.isRedirect(viewName)) {
            return theme + viewName;
        }
        return viewName;
    }
}
