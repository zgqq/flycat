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
package com.github.flycat.spi.alarm;

import ch.qos.logback.classic.Logger;
import com.github.flycat.util.log.LogErrorEvent;
import com.google.common.eventbus.Subscribe;

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
        sendAlarm(logErrorEvent.getLogger(), logErrorEvent.getMessage(),
                logErrorEvent.getThrowable());
    }

    private void sendAlarm(Logger logger, String s, Throwable throwable, Object... objects) {
        if (!shouldSendAlarm(logger, s, throwable, objects)) {
            return;
        }
        sendAlarm(logger, s, objects);
    }

    protected boolean shouldSendAlarm(Logger logger, String s, Throwable throwable, Object[] objects) {
        return true;
    }

    protected void sendAlarm(Logger logger, String s, Object... objects) {
        final String name = logger.getName();
        if (packageName != null && name.startsWith(packageName)) {
            AlarmUtils.sendNotify("app:" + applicationName
                    + " logger name:" + logger.getName() + " message:" + s);
        }
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getPackageName() {
        return packageName;
    }
}
