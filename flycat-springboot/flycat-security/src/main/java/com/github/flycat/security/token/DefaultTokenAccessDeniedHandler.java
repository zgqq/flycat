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
package com.github.flycat.security.token;

import com.github.flycat.web.response.ResponseFactoryHolder;
import com.github.flycat.web.util.HttpResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DefaultTokenAccessDeniedHandler extends AbstractTokenAccessDeniedHandler {


    protected DefaultTokenAccessDeniedHandler(AuthenticationTrustResolver trustResolver) {
        super(trustResolver);
    }

    @Override
    protected void handleInvalidToken(HttpServletRequest quest,
                                      HttpServletResponse response,
                                      AccessDeniedException accessDeniedException) {
        final Object invalidTokenResult = ResponseFactoryHolder.getResponseFactory().createInvalidTokenResponse();
        if (invalidTokenResult != null) {
            HttpResponseUtils.writeJson(response, invalidTokenResult);
        } else {
            HttpResponseUtils.writeJson(response,
                    "请先登录", HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    protected void handleAccessDenied(HttpServletRequest request, HttpServletResponse response,
                                      AccessDeniedException accessDeniedException) {
        final Object accessDeniedResult = ResponseFactoryHolder
                .getResponseFactory()
                .createAccessDeniedResponse(accessDeniedException);
        if (accessDeniedResult != null) {
            HttpResponseUtils.writeJson(response, accessDeniedResult);
        } else {
            HttpResponseUtils.writeJson(response,
                    "权限错误", HttpStatus.FORBIDDEN.value()
            );
        }
    }
}
