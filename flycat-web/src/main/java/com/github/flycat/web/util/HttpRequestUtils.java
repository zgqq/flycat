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
