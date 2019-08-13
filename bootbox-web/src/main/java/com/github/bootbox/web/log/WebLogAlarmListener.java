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
package com.github.bootbox.web.log;

import ch.qos.logback.classic.Logger;
import com.github.bootbox.alarm.LogErrorListener;
import com.github.bootbox.web.exception.BusinessException;

public class WebLogAlarmListener extends LogErrorListener {

    @Override
    protected boolean shouldSendAlarm(Logger logger, String s, Throwable throwable, Object[] objects) {
        return !(throwable instanceof BusinessException
                ||
                (throwable != null && throwable.getCause() instanceof BusinessException));
    }
}
