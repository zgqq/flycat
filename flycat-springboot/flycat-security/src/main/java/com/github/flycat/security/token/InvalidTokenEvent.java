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

import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InvalidTokenEvent {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final AccessDeniedException accessDeniedException;

    public InvalidTokenEvent(HttpServletRequest request, HttpServletResponse response,
                             AccessDeniedException accessDeniedException) {
        this.request = request;
        this.response = response;
        this.accessDeniedException = accessDeniedException;
    }


    public HttpServletRequest getRequest() {
        return request;
    }


    public HttpServletResponse getResponse() {
        return response;
    }


    public AccessDeniedException getAccessDeniedException() {
        return accessDeniedException;
    }
}
