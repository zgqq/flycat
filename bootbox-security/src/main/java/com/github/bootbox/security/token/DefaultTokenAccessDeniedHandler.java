package com.github.bootbox.security.token;

import com.alibaba.fastjson.JSON;
import com.github.bootbox.web.api.ApiFactoryHolder;
import com.github.bootbox.web.util.HttpResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultTokenAccessDeniedHandler extends AbstractTokenAccessDeniedHandler {

    public DefaultTokenAccessDeniedHandler() {
    }

    @Override
    protected void handleInvalidToken(HttpServletRequest quest,
                                      HttpServletResponse response,
                                      AccessDeniedException accessDeniedException) {
        final Object invalidTokenResult = ApiFactoryHolder.getApiFactory().createInvalidTokenResult();
        if (invalidTokenResult != null) {
            HttpResponseUtils.writeJson(response, JSON.toJSONString(invalidTokenResult));
        } else {
            HttpResponseUtils.writeJson(response,
                    "请先登录", HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    protected void handleAccessDenied(HttpServletRequest request, HttpServletResponse response,
                                      AccessDeniedException accessDeniedException) {
        final Object accessDeniedResult = ApiFactoryHolder
                .getApiFactory()
                .createAccessDeniedResult(accessDeniedException);
        if (accessDeniedResult != null) {
            HttpResponseUtils.writeJson(response, JSON.toJSONString(accessDeniedResult));
        } else {
            HttpResponseUtils.writeJson(response,
                    "权限错误", HttpStatus.FORBIDDEN.value()
            );
        }
    }
}
