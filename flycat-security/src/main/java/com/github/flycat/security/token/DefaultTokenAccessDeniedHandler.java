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
package com.github.flycat.security.token;

import com.alibaba.fastjson.JSON;
import com.github.flycat.web.api.ApiFactoryHolder;
import com.github.flycat.web.util.HttpResponseUtils;
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
