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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 保存request读取的流
 * Created by zcy on 17-4-18.
 */
public class HttpRequestWrapper extends HttpServletRequestWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestWrapper.class);

    private byte[] requestBody = new byte[0];
    private boolean bufferFilled = false;

    public HttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        try {
            final byte[] requestBodyBytes = getRequestBodyBytes();
            final String content = new String(requestBodyBytes, Charset.forName("UTF-8"));
            final String[] split = content.split("&");
            for (int i = 0; i < split.length; i++) {
                final String[] keyValue = split[i].split("=");
                if (name.equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        } catch (IOException e) {
            LOGGER.error("Unable to get parameter, name:{}", name, e);
            return null;
        }
        return null;
    }

    public byte[] getRequestBodyBytes() throws IOException {
        if (bufferFilled) {
            return Arrays.copyOf(requestBody, requestBody.length);
        }
        InputStream inputStream = super.getInputStream();
        byte[] buffer = new byte[102400]; // 100kb buffer
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            requestBody = Bytes.concat(this.requestBody, Arrays.copyOfRange(buffer, 0, bytesRead));
        }
        bufferFilled = true;
        return requestBody;
    }

    /**
     * 覆盖getInputStream() 返回保存的流数据
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CustomServletInputStream(getRequestBodyBytes());
    }

    private static class CustomServletInputStream extends ServletInputStream {

        private ByteArrayInputStream buffer;

        CustomServletInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public int read() throws IOException {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new RuntimeException("Not implemented");
        }
    }

    public static void main(String[] args) {
        byte[] requestBody = new byte[0];

        System.out.println(Arrays.copyOf(requestBody, requestBody.length));
    }

    public String getRequestBody() {
        try {
            return new String(getRequestBodyBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
