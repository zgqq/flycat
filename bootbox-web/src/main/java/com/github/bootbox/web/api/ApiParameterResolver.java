package com.github.bootbox.web.api;

import javax.servlet.http.HttpServletRequest;

public interface ApiParameterResolver {
    String resolveParameter(HttpServletRequest request, ParameterRequest parameterRequest);
}
