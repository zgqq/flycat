package com.github.bootbox.util.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.util.Map;

public class LogInterceptor extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger,
                              Level level, String s, Object[] objects, Throwable throwable) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        if (contextMap != null && !contextMap.isEmpty()
                && s != null) {
            if (s != null && !s.startsWith(MDCUtils.LOG_MDC)) {
                final String message = getMessage(s, contextMap);
                final String fqcn = Logger.FQCN;
                if (level == Level.ERROR) {
                    onError(logger, s, throwable);
                }
                final org.slf4j.event.Level level4j = org.slf4j.event.Level.valueOf(level.levelStr);
                logger.log(marker, fqcn, level4j.toInt(), message, objects, throwable);
                return FilterReply.DENY;
            }
            return FilterReply.NEUTRAL;
        } else {
            if (level == Level.ERROR) {
                onError(logger, s, throwable);
            }
            return FilterReply.NEUTRAL;
        }
    }

    protected void onError(Logger logger, String s, Throwable throwable) { }

    public String getMessage(String s, Map<String, String> contextMap) {
        final String httpUri = contextMap.get(MDCUtils.HTTP_URI);
        String prefix = MDCUtils.LOG_MDC + ": ";
        if (httpUri != null) {
            String requestURI = httpUri;
            final String httpMethod = contextMap.get(MDCUtils.HTTP_METHOD);
            final String httpAgent = contextMap.get(MDCUtils.HTTP_AGENT);
            prefix += "[uri:" + requestURI + ", method:" + httpMethod + ", " +
                    "agent:" + httpAgent + "]";
        }

        String postfix = "";
        for (Map.Entry<String, String> entry : contextMap.entrySet()) {
            if (!MDCUtils.MDC_CONSTANTS.contains(entry.getKey())
                    && !entry.getKey().startsWith("req.")
            ) {
                postfix += " " + entry.getKey() + ":" + entry.getValue() + ",";
            }
        }

        if (postfix.endsWith(",")) {
            postfix = postfix.substring(0, postfix.length() - 1);
        }
        return prefix + " " + s + ", context[" + postfix + "]";
    }
}
