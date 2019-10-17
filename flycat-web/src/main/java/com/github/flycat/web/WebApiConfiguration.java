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
package com.github.flycat.web;

import com.github.flycat.web.api.ApiFactory;
import com.github.flycat.web.api.ApiFactoryImpl;
import com.github.flycat.web.api.ApiParameterResolver;
import com.github.flycat.web.api.ParameterRequest;

import javax.servlet.http.HttpServletRequest;

public interface WebApiConfiguration extends WebConfiguration {

    default ApiParameterResolver createParameterResolver() {
        return new ApiParameterResolver() {
            @Override
            public String resolveParameter(HttpServletRequest request, ParameterRequest parameterRequest) {
                return null;
            }
        };
    }

    default ApiFactory createApiFactory() {
        return new ApiFactoryImpl();
    }
}
