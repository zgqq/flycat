package com.github.bootbox.web.log;

import ch.qos.logback.classic.Logger;
import com.github.bootbox.web.exception.BusinessException;

public class WebLogInterceptor extends com.github.bootbox.util.log.LogInterceptor {

    @Override
    protected boolean shouldSendAlarm(Logger logger, String s, Throwable throwable, Object[] objects) {
        return !(throwable instanceof BusinessException
                ||
                (throwable != null && throwable.getCause() instanceof BusinessException));
    }
}
