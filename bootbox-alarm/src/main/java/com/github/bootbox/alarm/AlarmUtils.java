package com.github.bootbox.alarm;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class AlarmUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmUtils.class);
    private static final Map<String, AlarmSender> SENDERS = new ConcurrentHashMap<>();
    private static AlarmSender defaultSender;

    static {
        final ServiceLoader<AlarmSender> load = ServiceLoader.load(AlarmSender.class);
        final Iterator<AlarmSender> iterator = load.iterator();
        while (iterator.hasNext()) {
            final AlarmSender next = iterator.next();
            registerSender(next.getClass().getName(),
                    next
            );
        }
    }

    public static void registerSender(String name, AlarmSender alarmSender) {
        LOGGER.info("Registering sender, name:{}", name);
        SENDERS.put(name, alarmSender);
    }

    public static void setDefaultSender(AlarmSender defaultSender) {
        LOGGER.info("Setting default sender, {}", defaultSender.getClass().getName());
        if (AlarmUtils.defaultSender != null) {
            LOGGER.warn("Default alarm sender exists! before:{}, after:{}",
                    AlarmUtils.defaultSender.getClass().getName(),
                    defaultSender.getClass().getName()
            );
        }
        AlarmUtils.defaultSender = defaultSender;
        registerSender(defaultSender.getClass().getName(), defaultSender);
    }

    public static void sendNotify(String message) {
        AlarmSender alarmSender = getDefaultAlarmSender();
        if (alarmSender == null) {
            LOGGER.error("Not found any alarm sender, message:{}", message);
        } else {
            alarmSender.sendNotify(message);
        }
    }

    @Nullable
    public static AlarmSender getDefaultAlarmSender() {
        AlarmSender alarmSender = null;
        if (AlarmUtils.defaultSender != null) {
            alarmSender = AlarmUtils.defaultSender;
        } else {
            final Iterator<Map.Entry<String, AlarmSender>> iterator = SENDERS.entrySet().iterator();
            if (iterator.hasNext()) {
                alarmSender = iterator.next().getValue();
            }
        }
        return alarmSender;
    }
}
