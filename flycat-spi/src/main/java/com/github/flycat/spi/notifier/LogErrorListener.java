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
package com.github.flycat.spi.notifier;

import com.github.flycat.log.LogErrorEvent;
import com.github.flycat.util.ExceptionUtils;
import com.github.flycat.util.StringUtils;
import com.google.common.base.Splitter;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;

public class LogErrorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogErrorListener.class);

    private final List<String> excludePackageNames;

    public LogErrorListener(String packages) {
        if (StringUtils.isNotBlank(packages)) {
            this.excludePackageNames = Splitter.on(",").omitEmptyStrings().splitToList(packages);
        } else {
            this.excludePackageNames = new ArrayList<>();
        }
    }


    @Subscribe
    public void listen(LogErrorEvent logErrorEvent) {
        try {
            sendAlarm(logErrorEvent);
        } catch (Exception e) {
            LOGGER.warn("Unable to send alarm", e);
        }
    }

    private void sendAlarm(LogErrorEvent logErrorEvent) {
        Logger logger = logErrorEvent.getLogger();
        String message = logErrorEvent.getFormattedMessage();
        Throwable throwable = logErrorEvent.getThrowable();
        Object[] args = logErrorEvent.getArgs();
        if (!shouldSendAlarm(logger, message, throwable, args)) {
            return;
        }
        String alarmMessage = getAlarmMessage(logger, message, throwable);
        sendAlarm(logger, alarmMessage, throwable, args);
    }

    protected boolean shouldSendAlarm(Logger logger, String message, Throwable throwable, Object[] objects) {
        return true;
    }

    protected final void sendAlarm(Logger logger, String message, Throwable throwable, Object... objects) {
        final String name = logger.getName();
        if (excludePackageNames != null && !excludePackageNames.isEmpty()) {
            for (String excludePackageName : excludePackageNames) {
                if (name.startsWith(excludePackageName)) {
                    return;
                }
            }
        }
        doSendAlarm(logger, message, throwable, objects);
    }

    protected void doSendAlarm(Logger logger, String message, Throwable throwable, Object... objects) {
        NotifierUtils.sendNotification(
                message
        );
    }

    protected String getAlarmMessage(Logger logger, String message, Throwable throwable) {
        return " Logger name: " + logger.getName() + "\n"
                + "Log message: " + message + " \nException: "
                + ExceptionUtils.getStackTrace(throwable);
    }
}
