package com.github.flycat.spi.impl.notifier;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.spi.notifier.AbstractNotificationSender;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class DefaultNotificationSender extends AbstractNotificationSender {

    private final ApplicationConfiguration applicationConfiguration;
    private final MailNotificationSender mailNotificationSender;

    public DefaultNotificationSender() {
        this(null);
    }

    @Inject
    public DefaultNotificationSender(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        this.mailNotificationSender = new MailNotificationSender(this.applicationConfiguration);
    }

    @Override
    public void doSend(String message) {
        this.mailNotificationSender.doSend(message);
    }
}
