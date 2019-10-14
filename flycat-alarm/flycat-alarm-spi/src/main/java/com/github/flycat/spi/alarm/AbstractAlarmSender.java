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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.github.flycat.context.ContextUtils;
import com.github.flycat.util.StringUtils;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class AbstractAlarmSender implements AlarmSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAlarmSender.class);
    private static final MetricRegistry REGISTRY = new MetricRegistry();
    private static final RateLimiter rateLimiter = RateLimiter.create(1.0);

    @Override
    public void sendNotify(String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        final boolean require = rateLimiter.tryAcquire();
        if (require) {
            final Meter meter = REGISTRY.meter("log." + message);
            meter.mark();
            if (meter.getOneMinuteRate() < 0.4) {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                }
                final String applicationName = ContextUtils.getApplicationName();
                message = "machine info:" + inetAddress + ", app name:" + applicationName + ", error:" + message;
                doSendNotify(message);
            } else {
                LOGGER.warn("Alarm too frequently, aborted, message:{}", message);
            }
        } else {
            LOGGER.info("Unable to get token, abort alarm, message:{}", message);
        }
    }

    public abstract void doSendNotify(String message);
}
