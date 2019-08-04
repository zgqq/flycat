package com.github.bootbox.alarm;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAlarmSender implements AlarmSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAlarmSender.class);
    private static final MetricRegistry registry = new MetricRegistry();

    @Override
    public void sendNotify(String message) {
        final Meter meter = registry.meter(message);
        meter.mark();
        if (meter.getOneMinuteRate() < 0.4) {
            doSendNotify(message);
        } else {
            LOGGER.warn("Alarm too frequently, aborted, message:{}", message);
        }
    }

    public abstract void doSendNotify(String message);
}
