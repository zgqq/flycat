package com.github.bootbox.alarm;

import ch.qos.logback.classic.Logger;
import com.github.bootbox.server.config.ServerEnvUtils;
import com.github.bootbox.util.log.LogInterceptor;

public class AlarmLogInterceptor extends LogInterceptor  {

    private static String applicationName;
    private static String packageName;

    static {
        applicationName = ServerEnvUtils.getProperty("spring.application.name");
        packageName = ServerEnvUtils.getProperty("bootbox.alarm.log.package");
        System.out.println("Alarm log package name " + packageName);
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

    private void sendAlarm(Logger logger, String s, Object... objects) {
        final String name = logger.getName();
        if (packageName != null && name.startsWith(packageName)) {
            AlarmUtils.sendNotify("app:" + applicationName
                    + " logger name:" + logger.getName() + " message:" + s);
        }
    }
}
