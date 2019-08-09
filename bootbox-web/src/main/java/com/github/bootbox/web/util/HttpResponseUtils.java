package com.github.bootbox.web.util;

import com.alibaba.fastjson.JSON;
import com.github.bootbox.web.api.ApiFactory;
import com.github.bootbox.web.api.ApiFactoryHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public final class HttpResponseUtils {
    private HttpResponseUtils() {
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
        String output = JSON.toJSONString(apiFactory.createApiResult(errorCode, message));
        writeJson(response, output);
    }

//    public static void writeSuccess(HttpServletResponse response, String message) {
//        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
//        String output = JSON.toJSONString(apiFactory.createSuccessResult(message));
//        writeMessage(response, output);
//    }
}
