package com.github.bootbox.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAlarmSender implements AlarmSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAlarmSender.class);

    @Override
    public void sendNotify(String message) {
        LOGGER.info("send nothing, message:{}", message);
    }

//    @Override
//    public void sendNotify(String type, String token, String message) {
//        LOGGER.info("Nothing send notify");
//    }
}
