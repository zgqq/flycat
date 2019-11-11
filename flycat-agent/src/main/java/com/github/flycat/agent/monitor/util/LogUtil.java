package com.github.flycat.agent.monitor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);

    public static Logger getArthasLogger() {
        return LOGGER;
    }
}
