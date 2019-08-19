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
package com.github.flycat.web.response;

import com.github.flycat.web.context.ExceptionContext;

import javax.validation.ValidationException;

public interface ResponseFactory {

    Object createResponse(int code, String message);

    default Object createUnknownExceptionResponse(ExceptionContext exceptionContext) {
        return null;
    }

    default Object createValidationExceptionResponse(ValidationException exception) {
        return null;
    }

    default Object createInvalidTokenResponse() {
        return null;
    }

    default Object createAccessDeniedResponse(Exception exception) {
        return null;
    }

    default int getBusinessErrorPlaceholderCode() {
        return 1;
    }

    default int getSystemErrorPlaceholderCode() {
        return 2;
    }

    default int getModulePlaceholderCode() {
        return 1;
    }
}
