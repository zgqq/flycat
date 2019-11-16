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
package com.github.flycat.spi.impl.alarm;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.context.ContextFreeConfiguration;
import com.github.flycat.context.ContextUtils;
import com.github.flycat.exception.BusinessException;
import com.github.flycat.spi.alarm.AlarmSender;
import com.github.flycat.spi.alarm.LogErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class LogAlarmListener extends LogErrorListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAlarmListener.class);
    private final AlarmSender alarmSender;

    @Inject
    public LogAlarmListener(AlarmSender alarmSender, ApplicationConfiguration applicationConfiguration) {
        super(applicationConfiguration.getString("app.name"),
                applicationConfiguration.getString("flycat.alarm.log.package"));
        this.alarmSender = alarmSender;
    }

    @Override
    protected boolean shouldSendAlarm(Logger logger, String message, Throwable throwable, Object[] objects) {
        boolean shouldSend = !(throwable instanceof BusinessException
                ||
                (throwable != null && throwable.getCause() instanceof BusinessException));
        if (!shouldSend) {
            return shouldSend;
        }
        ContextFreeConfiguration contextFreeConfiguration = ContextUtils.createContextFreeConfiguration();
        boolean alarmEnabled = contextFreeConfiguration
                .getBooleanValue("flycat.alarm.enabled", false);
        return (!ContextUtils.isTestProfile()) || alarmEnabled;
    }

    @Override
    protected void sendAlarm(Logger logger, String message, Throwable throwable, Object... objects) {
        this.alarmSender.sendNotify(message);
    }
}
