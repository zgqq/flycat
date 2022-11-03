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

