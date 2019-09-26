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
package com.github.flycat.web.api;

import com.github.flycat.context.ContextUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ApiResponseCodeUtils {

    public static int getSystemErrorCode(int code) {
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        final int systemErrorPlaceholderCode = apiFactory.getSystemErrorPlaceholderCode();
        return getApiResponseCode(systemErrorPlaceholderCode, code);
    }

    public static int getBusinessErrorCode(int code) {
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        final int businessErrorPlaceholderCode = apiFactory.getBusinessErrorPlaceholderCode();
        return getApiResponseCode(businessErrorPlaceholderCode, code);
    }

    private static int getApiResponseCode(int levelCode, int code) {
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        final int modulePlaceholderCode = apiFactory.getModulePlaceholderCode();
        final StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(levelCode);
        if (modulePlaceholderCode < 10) {
            codeBuilder.append("0");
        }
        codeBuilder.append(modulePlaceholderCode);

        if (code < 10) {
            codeBuilder.append("00");
        } else if (code < 100) {
            codeBuilder.append("0");
        }
        codeBuilder.append(code);
        return Integer.parseInt(codeBuilder.toString());
    }

    public static Object getUnknownExceptionResult(Throwable e) {
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        Object unknownExceptionResult = apiFactory.createUnknownExceptionResult(e);
        if (unknownExceptionResult == null) {
            if (ContextUtils.isTestProfile()) {
                final String stackTrace = ExceptionUtils.getStackTrace(e);
                unknownExceptionResult = apiFactory.createApiResult(ApiResponseCodeUtils
                        .getSystemErrorCode(ApiResponseCode.SERVER_UNKNOWN_ERROR), stackTrace);
            } else {
                unknownExceptionResult = apiFactory.createApiResult(ApiResponseCodeUtils
                        .getSystemErrorCode(ApiResponseCode.SERVER_UNKNOWN_ERROR), "服务器傲娇了!");
            }
        }
        return unknownExceptionResult;
    }
}
