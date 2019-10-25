package com.github.flycat.web.util;

import javax.servlet.http.HttpServletRequest;

public class HttpSessionUtils {

    public static void setAttribute(
            HttpServletRequest request,
            String name, Throwable throwable) {
        final String message = throwable.getMessage();
        request.getSession().setAttribute(name, message);
    }
}
