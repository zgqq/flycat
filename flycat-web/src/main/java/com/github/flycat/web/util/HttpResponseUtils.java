/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.web.util;

import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.web.api.ApiFactory;
import com.github.flycat.web.api.ApiFactoryHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public final class HttpResponseUtils {
    private HttpResponseUtils() {
    }

    public static void writeJson(HttpServletResponse response, Object result) {
        writeJson(response, JsonUtils.toJsonString(result));
    }

    public static void writeJson(HttpServletResponse response, String message) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void writeError(HttpServletResponse response, Throwable e) throws IOException {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/text; charset=utf-8");
//        e.printStackTrace(response.getWriter());
//    }

    public static void writeJson(HttpServletResponse response, String message, int errorCode) {
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        final String output = JsonUtils.toJsonString(apiFactory.createApiResult(errorCode, message));
        writeJson(response, output);
    }

//    public static void writeSuccess(HttpServletResponse response, String message) {
//        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
//        String output = JSON.toJSONString(apiFactory.createSuccessResult(message));
//        writeMessage(response, output);
//    }
}
