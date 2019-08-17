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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAlarmSender implements AlarmSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAlarmSender.class);
    private static final MetricRegistry REGISTRY = new MetricRegistry();

    @Override
    public void sendNotify(String message) {
        final Meter meter = REGISTRY.meter("log." + message);
        meter.mark();
        if (meter.getOneMinuteRate() < 0.4) {
            doSendNotify(message);
        } else {
            LOGGER.warn("Alarm too frequently, aborted, message:{}", message);
        }
    }

    public abstract void doSendNotify(String message);
}
