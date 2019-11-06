package com.github.flycat.log;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorLogFileLogger {
    public static final String ERROR = "all-error";
    private static final Logger LOGGER = LoggerFactory.getLogger(ERROR);

    @Subscribe
    public void onError(LogErrorEvent logErrorEvent) {
        LOGGER.error(logErrorEvent.getMdcMessage(),
                logErrorEvent.getArgs(), logErrorEvent.getThrowable());
    }
}

