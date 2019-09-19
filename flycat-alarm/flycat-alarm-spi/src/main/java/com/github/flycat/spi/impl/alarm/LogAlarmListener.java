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
package com.github.flycat.spi.impl.alarm;

import com.github.flycat.exception.BusinessException;
import com.github.flycat.spi.alarm.AlarmSender;
import com.github.flycat.spi.alarm.LogErrorListener;
import com.github.flycat.context.ApplicationConfiguration;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class LogAlarmListener extends LogErrorListener {
    private final AlarmSender alarmSender;

    @Inject
    public LogAlarmListener(AlarmSender alarmSender, ApplicationConfiguration applicationConfiguration) {
        super(applicationConfiguration.getString("app.name"), applicationConfiguration.getString("flycat.alarm.log.package"));
        this.alarmSender = alarmSender;
    }

    @Override
    protected boolean shouldSendAlarm(Logger logger, String s, Throwable throwable, Object[] objects) {
        return !(throwable instanceof BusinessException
                ||
                (throwable != null && throwable.getCause() instanceof BusinessException));
    }

    @Override
    protected void sendAlarm(Logger logger, String s, Object... objects) {
        this.alarmSender.sendNotify(s);
    }
}
