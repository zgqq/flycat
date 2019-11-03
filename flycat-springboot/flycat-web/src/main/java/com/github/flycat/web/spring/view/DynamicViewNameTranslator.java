package com.github.flycat.web.spring.view;

import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

import javax.servlet.http.HttpServletRequest;

public class DynamicViewNameTranslator implements RequestToViewNameTranslator {
    private volatile DefaultRequestToViewNameTranslator viewNameTranslator = new DefaultRequestToViewNameTranslator();

    public void setPrefix(String prefix) {
        final DefaultRequestToViewNameTranslator translator
                = new DefaultRequestToViewNameTranslator();
        translator.setPrefix(prefix);
        viewNameTranslator = translator;
    }

    @Override
    public String getViewName(HttpServletRequest request) throws Exception {
        return viewNameTranslator.getViewName(request);
    }
}
