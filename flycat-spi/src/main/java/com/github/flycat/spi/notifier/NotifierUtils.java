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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class NotifierUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifierUtils.class);
    private static final Map<String, NotificationSender> SENDERS = new ConcurrentHashMap<>();
    private static NotificationSender defaultSender;

    static {
        final ServiceLoader<NotificationSender> load = ServiceLoader.load(NotificationSender.class);
        final Iterator<NotificationSender> iterator = load.iterator();
        while (iterator.hasNext()) {
            final NotificationSender next = iterator.next();
            registerSender(next.getClass().getName(),
                    next
            );
        }
    }

    public static void registerSender(String name, NotificationSender alarmSender) {
        LOGGER.info("Registering sender, name:{}", name);
        SENDERS.put(name, alarmSender);
    }

    public static void setDefaultSender(NotificationSender defaultSender) {
        LOGGER.info("Setting default sender, {}", defaultSender.getClass().getName());
        if (NotifierUtils.defaultSender != null) {
            LOGGER.warn("Default alarm sender exists! before:{}, after:{}",
                    NotifierUtils.defaultSender.getClass().getName(),
                    defaultSender.getClass().getName()
            );
        }
        NotifierUtils.defaultSender = defaultSender;
        registerSender(defaultSender.getClass().getName(), defaultSender);
    }

    public static void sendNotification(String message) {
        NotificationSender defaultNotificationSender = getDefaultNotificationSender();
        if (defaultNotificationSender == null) {
            LOGGER.warn("Not found any alarm sender, message:{}", message);
        } else {
            defaultNotificationSender.send(message);
        }
    }

    @Nullable
    public static NotificationSender getDefaultNotificationSender() {
        NotificationSender notificationSender = null;
        if (NotifierUtils.defaultSender != null) {
            notificationSender = NotifierUtils.defaultSender;
        } else {
            final Iterator<Map.Entry<String, NotificationSender>> iterator = SENDERS.entrySet().iterator();
            if (iterator.hasNext()) {
                notificationSender = iterator.next().getValue();
            }
        }
        return notificationSender;
    }
}
