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

import com.codahale.metrics.MetricRegistry;
import com.github.flycat.context.ContextUtils;
import com.github.flycat.util.StringUtils;
import com.github.flycat.util.date.DateFormatter;
import com.github.flycat.util.executor.ExecutorUtils;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class AbstractNotificationSender implements NotificationSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNotificationSender.class);
    private static final MetricRegistry REGISTRY = new MetricRegistry();
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(1.0);
    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = ExecutorUtils.newExecutor(1, "notifier-executor");
    }

    @Override
    public void send(Message message) {
        final String content = message.getContent();
        if (StringUtils.isBlank(content)) {
            return;
        }
        final Date createTime = message.getCreateTime();
        if (createTime == null) {
            message.setCreateTime(new Date());
        }

        threadPoolExecutor.execute(() -> buildMessageAndSend(message));
    }

//    private void sendLimitedNotify(String message) {
//        final boolean require = RATE_LIMITER.tryAcquire();
//        if (require) {
//            final Meter meter = REGISTRY.meter("log." + message);
//            meter.mark();
//            if (meter.getOneMinuteRate() < 0.4) {
//                buildMessageAndSend(message);
//            } else {
//                LOGGER.warn("Alarm too frequently, aborted, message:{}", message);
//            }
//        } else {
//            LOGGER.info("Unable to get token, abort alarm, message:{}", message);
//        }
//    }

    private void buildMessageAndSend(Message message) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
        }
        String applicationName = "unknown";
        try {
            applicationName = ContextUtils.getApplicationName();
        } catch (Exception e) {
            LOGGER.warn("Unable to get applicationName", e);
        }

        StringBuilder messageBuilder = new StringBuilder();

        if (message.hasFormat(MessageFormat.WITH_SERVER_IP)) {
            messageBuilder.append("ServerIP:" + inetAddress + "\n");
        }

        if (message.hasFormat(MessageFormat.WITH_APP_NAME)) {
            messageBuilder.append("ServerName:" + applicationName + "\n");
        }

        if (message.hasFormat(MessageFormat.WITH_NOTIFICATION_TIME)) {
            final String createTime = DateFormatter.YYYY_MM_DD_HH_MM_SS.format(message.getCreateTime());
            messageBuilder.append("CreateTime:" + createTime + "\n");
        }

        messageBuilder.append("Notification:" + message.getContent());

        message.setDecoratedContent(message.toString());
        doSend(message);
    }

    public abstract void doSend(Message message);
}
