package com.github.flycat.web.spring;

import com.github.flycat.util.io.IOUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
}

