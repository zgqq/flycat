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
package com.github.flycat.starter.app.web.exception;

import com.github.flycat.exception.BaseException;

public class BusinessException extends BaseException {

    public BusinessException() {
        super();
    }

    public BusinessException(int errorCode) {
        this(errorCode, "");
    }

    public BusinessException(int errorCode, String defaultMessage) {
        super(errorCode, defaultMessage);
    }

    public BusinessException(int errorCode, String internalCode, String defaultMessage) {
        super(errorCode, internalCode, defaultMessage);
    }

    public BusinessException(Throwable cause, int errorCode, String defaultMessage) {
        super(cause, errorCode, defaultMessage);
    }

    public BusinessException(Throwable cause, int errorCode) {
        super(cause, errorCode);
    }

    public BusinessException(int errorCode, String[] args) {
        super(errorCode, args);
    }

}
