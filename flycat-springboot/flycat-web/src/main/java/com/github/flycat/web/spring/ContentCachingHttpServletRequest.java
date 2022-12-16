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

import com.github.flycat.util.ArrayUtils;
import com.github.flycat.util.CollectionUtils;
import com.github.flycat.util.StringUtils;
import com.github.flycat.util.io.IOUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ContentCachingHttpServletRequest extends ContentCachingRequestWrapper {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private Map<String, String[]> valueMap;

    public ContentCachingHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    private boolean tryParsePostBody() {
        boolean formPost = isFormPost();
        if (formPost && valueMap == null) {
            String str = getRequestBody();
            valueMap = new HashMap();
            String[] valueKey = str.split("&");
            for (String temp : valueKey) {
                if (temp != null) {
                    int idx = temp.indexOf('=');
                    int len = temp.length();
                    if (idx != -1) {
                        String key = temp.substring(0, idx);
                        String value = idx + 1 < len ? temp.substring(idx + 1) : "";
                        value = StringUtils.decodeOrReturn(value);
                        String[] prevValue = valueMap.get(key);
                        if (prevValue == null) {
                            valueMap.put(key, new String[]{value});
                        } else {
                            valueMap.put(key, ArrayUtils.add(prevValue, value));
                        }
                    }
                }
            }
        }
        return formPost;
    }


    @Override
    public String getParameter(String name) {
        if (tryParsePostBody()) {
            return valueMap.get(name)[0];
        }
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (tryParsePostBody()) {
            return valueMap;
        }
        return super.getParameterMap();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (tryParsePostBody()) {
            return CollectionUtils.asEnumeration(valueMap.keySet().iterator());
        }
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        if (tryParsePostBody()) {
            return valueMap.get(name);
        }
        return super.getParameterValues(name);
    }

    private boolean isFormPost() {
        String contentType = getContentType();
        return (contentType != null && contentType.contains(FORM_CONTENT_TYPE) &&
                HttpMethod.POST.matches(getMethod()));
    }

    public String getRequestBody() {
        try {
            final byte[] contentAsByteArray = getContentAsByteArray();
            if (contentAsByteArray.length > 0) {
                return new String(contentAsByteArray);
            } else {
                final ServletInputStream inputStream = super.getInputStream();
                IOUtils.read(inputStream, new byte[102400]);
                return new String(getContentAsByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDecodedRequestBody() {
        final String requestBody = getRequestBody();
        try {
            return URLDecoder.decode(requestBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return requestBody;
        }
    }

    public String getDecodedRequestURI() {
        final String requestURI = getRequestURI();
        try {
            return URLDecoder.decode(requestURI, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return requestURI;
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream inputStream = super.getInputStream();
        return new RepeatContentCachingInputStream(inputStream, getContentAsByteArray());
    }

    public static class RepeatContentCachingInputStream extends ServletInputStream {

        private final ServletInputStream is;
        private final ByteArrayInputStream cachedInputStream;

        public RepeatContentCachingInputStream(ServletInputStream is,
                                               byte[] cachedBytes) {
            this.is = is;
            if (cachedBytes.length > 0) {
                this.cachedInputStream = new ByteArrayInputStream(cachedBytes);
            } else {
                this.cachedInputStream = null;
            }
        }

        @Override
        public int read() throws IOException {
            if (cachedInputStream != null) {
                return cachedInputStream.read();
            }
            return is.read();
        }

        @Override
        public boolean isFinished() {
            return is.isFinished();
        }

        @Override
        public boolean isReady() {
            return is.isReady();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            is.setReadListener(readListener);
        }
    }
}

