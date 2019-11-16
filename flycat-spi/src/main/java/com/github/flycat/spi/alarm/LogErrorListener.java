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
package com.github.flycat.spi.alarm;

import com.github.flycat.log.LogErrorEvent;
import com.github.flycat.util.ExceptionUtils;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;

public class LogErrorListener {

//    private static String applicationName;
//    private static String packageName;
//
//    static {
//        applicationName = ServerEnvUtils.getProperty("spring.application.name");
//        packageName = ServerEnvUtils.getProperty("flycat.alarm.log.package");
//        System.out.println("Alarm log package name " + packageName);
//    }


    private final String applicationName;
    private final String packageName;

    public LogErrorListener(String applicationName, String packageName) {
        this.applicationName = applicationName;
        this.packageName = packageName;
    }


    @Subscribe
    public void listen(LogErrorEvent logErrorEvent) {
        sendAlarm(logErrorEvent);
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

    protected void sendAlarm(Logger logger, String message, Throwable throwable, Object... objects) {
        final String name = logger.getName();
        if (packageName != null && name.startsWith(packageName)) {
            AlarmUtils.sendNotify(
                    message
            );
        }
    }

    private String getAlarmMessage(Logger logger, String message, Throwable throwable) {
        return " Logger name: " + logger.getName() + "\n"
                + "Log message: " + message + " \nException: "
                + ExceptionUtils.getStackTrace(throwable);
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getPackageName() {
        return packageName;
    }
}
