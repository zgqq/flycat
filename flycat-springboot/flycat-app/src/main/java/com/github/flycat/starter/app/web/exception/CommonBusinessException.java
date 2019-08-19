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

import com.github.flycat.starter.app.web.api.ResultCode;

/**
 * Created by zgq
 * Date: 2018-07-25
 * Time: 6:30 PM
 *
 * @author zgq
 */
public class CommonBusinessException extends BusinessException {

    public CommonBusinessException(String defaultMessage) {
        this(ResultCode.CLIENT_UNKNOWN_ERROR, defaultMessage);
    }

    public CommonBusinessException(int errorCode) {
        super(errorCode);
    }

    public CommonBusinessException(int errorCode, String defaultMessage) {
        super(errorCode, defaultMessage);
    }

    public CommonBusinessException(int errorCode, String internalCode, String defaultMessage) {
        super(errorCode, internalCode, defaultMessage);
    }

    public CommonBusinessException(Throwable cause, int errorCode, String defaultMessage) {
        super(cause, errorCode, defaultMessage);
    }

    public CommonBusinessException(Throwable cause, int errorCode) {
        super(cause, errorCode);
    }

    public CommonBusinessException(int errorCode, String[] args) {
        super(errorCode, args);
    }
}
