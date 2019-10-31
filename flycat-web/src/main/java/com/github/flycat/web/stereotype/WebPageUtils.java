package com.github.flycat.web.stereotype;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebPageUtils {

    public static boolean hasVisited(HttpServletRequest request, String flag) {
        final HttpSession session = request.getSession();
        final Object attribute = session.getAttribute(flag);
        if (attribute == null) {
            session.setAttribute(flag, "1");
        }
        return attribute != null;
    }

    public static boolean isNeedPassword(HttpServletRequest request, HttpSession session,
                                         String correctPassword,
                                         String flag) {
        final String passwordParam = request.getParameter("password");
        boolean needPassword = false;
        String password = passwordParam;
        if (correctPassword != null) {
            if (password == null) {
                password = (String) session.getAttribute("user_password_" + flag);
            }
            needPassword = (password == null || !password.trim().equalsIgnoreCase(correctPassword.trim()));
            if (passwordParam != null) {
                session.setAttribute("user_password_" + flag, passwordParam);
            }
        }
        return needPassword;
    }
}

