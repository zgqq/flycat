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
package com.github.flycat.spi.impl.notifier;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.context.ContextFreeConfiguration;
import com.github.flycat.context.ContextUtils;
import com.github.flycat.exception.BusinessException;
import com.github.flycat.spi.cache.InMemoryCacheService;
import com.github.flycat.spi.notifier.LogErrorListener;
import com.github.flycat.spi.notifier.NotificationSender;
import com.github.flycat.util.ExceptionUtils;
import com.github.flycat.util.StringUtils;
import org.slf4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named
public class LogAlarmListener extends LogErrorListener {
    private final NotificationSender notificationSender;
    private final InMemoryCacheService inMemoryCacheService;

    @Inject
    public LogAlarmListener(NotificationSender notificationSender, ApplicationConfiguration applicationConfiguration, InMemoryCacheService inMemoryCacheService) {
        super(applicationConfiguration.getString("flycat.alarm.exclude.packages"));
        this.notificationSender = notificationSender;
        this.inMemoryCacheService = inMemoryCacheService;
    }

    @Override
    protected boolean shouldSendAlarm(Logger logger, String message, Throwable throwable, Object[] objects) {

        ContextFreeConfiguration contextFreeConfiguration = ContextUtils.createContextFreeConfiguration();
        boolean alarmEnabled = contextFreeConfiguration
                .getBooleanValue("flycat.alarm.enabled", false);
        if (!alarmEnabled) {
            return false;
        }

        if (ContextUtils.isTestProfile()) {
            return false;
        }
        boolean isBusinessException = (throwable instanceof BusinessException
                ||
                (throwable != null && throwable.getCause() instanceof BusinessException));
        if (isBusinessException) {
            String stackTrace = ExceptionUtils.getStackTrace(throwable);
            String exceptionId = StringUtils.md5(stackTrace);
            long count = inMemoryCacheService.increaseCount("", exceptionId);
            int businessExceptionCount = contextFreeConfiguration.getIntValue("flycat.alarm.business-exception",
                    5);
            if (businessExceptionCount == 1 || count > businessExceptionCount) {
                return true;
            }
        } else {
            String stackTrace = ExceptionUtils.getStackTrace(throwable);
            String exceptionId = StringUtils.md5(stackTrace);
            return inMemoryCacheService.isValueRefreshed("shouldSendAlarm", exceptionId, 3600 * 24);
        }
        return true;
    }

    @Override
    protected void doSendAlarm(Logger logger, String message, Throwable throwable, Object... objects) {
        this.notificationSender.send(message);
    }
}
