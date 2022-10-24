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
import com.github.flycat.context.ApplicationContext;
import com.github.flycat.context.ContextUtils;
import com.github.flycat.spi.cache.CacheOperation;
import com.github.flycat.spi.cache.DistributedCacheService;
import com.github.flycat.spi.cache.ExecuteResult;
import com.github.flycat.spi.cache.InMemoryCacheService;
import com.github.flycat.util.StringUtils;
import com.github.flycat.util.date.DateFormatter;
import com.github.flycat.util.executor.ExecutorUtils;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class AbstractNotificationSender implements NotificationSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNotificationSender.class);
    private static final MetricRegistry REGISTRY = new MetricRegistry();
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(1.0);
    private static ThreadPoolExecutor threadPoolExecutor;
    private CacheOperation cacheOperation;

    static {
        threadPoolExecutor = ExecutorUtils.newExecutor(1, "notifier-executor");
    }

    private ApplicationContext applicationContext;
    private volatile boolean asyncSend = false;

    protected AbstractNotificationSender() {
        this.cacheOperation = null;
    }

    protected AbstractNotificationSender(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
        if (message.isPreventRepeat()) {
            cacheOperation = getCacheOperation();
            if (cacheOperation == null) {
                throw new RuntimeException("Not found cache operation impl");
            }
            String messageMd5 = StringUtils.md5(message.getContent());
            ExecuteResult<Long> objectExecuteResult = null;
            if (cacheOperation instanceof DistributedCacheService) {
                objectExecuteResult = ((DistributedCacheService) cacheOperation).executeOnceAction("com.github.flycat.spi.notifier.AbstractNotificationSender.send",
                        messageMd5, Long.class, () -> {
                            sendMessage(message);
                            return System.currentTimeMillis();
                        }, message.getRepeatIntervalSeconds(
                        ));
            } else {
                objectExecuteResult = ((InMemoryCacheService) cacheOperation).executeOnceAction("com.github.flycat.spi.notifier.AbstractNotificationSender.send",
                        messageMd5, () -> {
                            sendMessage(message);
                            return System.currentTimeMillis();
                        }, message.getRepeatIntervalSeconds(
                        ));
            }
            if (!objectExecuteResult.isExecuted()) {
                LOGGER.info("The message was not notified, due to prevent repeat ");
            }
        } else {
            sendMessage(message);
        }
    }

    private void sendMessage(Message message) {
        if (isAsyncSend()) {
            threadPoolExecutor.execute(() -> buildMessageAndSend(message));
        } else {
            buildMessageAndSend(message);
        }
    }

    private CacheOperation getCacheOperation() {
        if (cacheOperation != null) {
            return cacheOperation;
        }
        CacheOperation cache = null;
        if (applicationContext != null) {
            Iterator<CacheOperation> beans = applicationContext.getBeansIterator(CacheOperation.class);
            while (beans.hasNext()) {
                CacheOperation next = beans.next();
                cache = next;
                if (next instanceof DistributedCacheService) {
                    break;
                }
            }
        }
        return cache;
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

        messageBuilder.append("Notification:" + message.getContent() + "\n");

        if (message.hasFormat(MessageFormat.WITH_SERVER_IP)) {
            messageBuilder.append("ServerIP: " + inetAddress + "\n");
        }

        if (message.hasFormat(MessageFormat.WITH_APP_NAME)) {
            messageBuilder.append("ServerName: " + applicationName + "\n");
        }

        if (message.hasFormat(MessageFormat.WITH_NOTIFICATION_TIME)) {
            final String createTime = DateFormatter.YYYY_MM_DD_HH_MM_SS.format(message.getCreateTime());
            messageBuilder.append("CreateTime: " + createTime + "\n");
        }

        message.setDecoratedContent(messageBuilder.toString());
        doSend(message);
    }

    public void setCacheOperation(CacheOperation cacheOperation) {
        this.cacheOperation = cacheOperation;
    }

    public abstract void doSend(Message message);

    public void setAsyncSend(boolean asyncSend) {
        this.asyncSend = asyncSend;
    }

    public boolean isAsyncSend() {
        return this.asyncSend;
    }
}
