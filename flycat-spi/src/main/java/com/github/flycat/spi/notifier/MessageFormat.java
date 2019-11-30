package com.github.flycat.spi.notifier;

public interface MessageFormat {
    int WITH_NOTIFICATION_TIME = 1;
    int WITH_SERVER_IP = 1 << 1;
    int WITH_APP_NAME = 1 << 2;
}
