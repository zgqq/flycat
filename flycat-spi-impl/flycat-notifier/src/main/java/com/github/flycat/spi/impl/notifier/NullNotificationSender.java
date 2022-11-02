package com.github.flycat.spi.impl.notifier;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.context.ApplicationContext;
import com.github.flycat.spi.notifier.AbstractNotificationSender;
import com.github.flycat.spi.notifier.Message;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullNotificationSender extends AbstractNotificationSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(NullNotificationSender.class);

    @Inject
    public NullNotificationSender(ApplicationConfiguration applicationConfiguration, ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public void doSend(Message message) {
        LOGGER.info("Not send message, message:{}", message);
    }
}
