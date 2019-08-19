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

import com.github.flycat.util.io.IOUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;

public class ContentCachingHttpServletRequest extends ContentCachingRequestWrapper {

    public ContentCachingHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public String getRequestBody() {
        try {
            final byte[] contentAsByteArray = getContentAsByteArray();
            if (contentAsByteArray.length > 0) {
                return new String(contentAsByteArray);
            } else {
                final Enumeration<String> parameterNames = super.getParameterNames();
                if (!parameterNames.hasMoreElements()) {
                    final ServletInputStream inputStream = super.getInputStream();
                    IOUtils.read(inputStream, new byte[102400]);
                }
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
}

