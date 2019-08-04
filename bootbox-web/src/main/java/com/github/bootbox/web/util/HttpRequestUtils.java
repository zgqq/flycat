package com.github.bootbox.web.util;

import com.google.common.primitives.Bytes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class HttpRequestUtils {

    private HttpRequestUtils() {
    }

    public static String getRequestBodyAsString(HttpServletRequest request) throws IOException {
        return new String(getRequestBody(request));
    }

    public static byte[] getRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        byte[] buffer = new byte[102400]; // 100kb buffer
        int bytesRead;
        byte[] requestBody = new byte[0];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            requestBody = Bytes.concat(requestBody, Arrays.copyOfRange(buffer, 0, bytesRead));
        }
        return requestBody;
    }
}
