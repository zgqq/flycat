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
package com.github.flycat.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.github.flycat.event.EventManager;
import com.github.flycat.log.ErrorLogFileLogger;
import com.github.flycat.log.LogErrorEvent;
import com.github.flycat.log.MDCUtils;
import com.github.flycat.util.CommonUtils;
import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.util.List;
import java.util.Map;

public class LogInterceptor extends TurboFilter {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public FilterReply decide(Marker marker, Logger logger,
                              Level level, String s, Object[] objects, Throwable throwable) {
        String name = logger.getName();
        if (ErrorLogFileLogger.ERROR.equals(name)) {
            return FilterReply.NEUTRAL;
        }

        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        if (contextMap != null && !contextMap.isEmpty()
                && s != null) {
            if (s != null && !s.startsWith(MDCUtils.LOG_MDC)) {
                final String message = getMessage(s, contextMap, level);
                final String fqcn = Logger.FQCN;
                if (level.levelInt == Level.ERROR_INT) {
                    onError(logger, s, throwable, message, objects);
                }
                final org.slf4j.event.Level level4j = org.slf4j.event.Level.valueOf(level.levelStr);
                logger.log(marker, fqcn, level4j.toInt(), message, objects, throwable);
                return FilterReply.DENY;
            }
            return FilterReply.NEUTRAL;
        } else {
            if (level.levelInt == Level.ERROR_INT) {
                onError(logger, s, throwable, s, objects);
            }
            return FilterReply.NEUTRAL;
        }
    }

    private static List<String> ignoredLoggers = Lists.newArrayList("org.springframework");

    protected void onError(Logger logger, String message, Throwable throwable,
                           String mdcMessage, Object[] args) {
        final String name = logger.getName();
        for (String ignoredLogger : ignoredLoggers) {
            if (name.startsWith(ignoredLogger)) {
                LOGGER.info("Ignored exception, logger:{}, message:{}", logger, message);
                return;
            }
        }
        LoggingEvent loggingEvent = new LoggingEvent(null, logger, Level.ERROR, mdcMessage, throwable, args);
        String formattedMessage = loggingEvent.getFormattedMessage();
        EventManager.post(new LogErrorEvent(logger, message, throwable, formattedMessage, args));
    }

    public String getMessage(String originMessage, Map<String, String> contextMap, Level level) {
        final String httpUri = contextMap.get(MDCUtils.HTTP_URI);
        String prefix = MDCUtils.LOG_MDC + ": ";
        if (httpUri != null) {
            final String reqId = contextMap.get(MDCUtils.REQ_ID);
            if (level == Level.ERROR) {
                final String httpURL = contextMap.get(MDCUtils.HTTP_URL);
                String decodeURL = CommonUtils.decodeURL(httpURL);
                final String httpMethod = contextMap.get(MDCUtils.HTTP_METHOD);
                final String httpAgent = contextMap.get(MDCUtils.HTTP_AGENT);
                prefix += "[reqId:" + reqId + ", url:" + decodeURL + ", method:" + httpMethod + ", " +
                        "agent:" + httpAgent + "]";
            } else {
                String requestURI = CommonUtils.decodeURL(httpUri);
                prefix += "[reqId:" + reqId + ", uri:" + requestURI + "]";
            }
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
        return prefix + " " + originMessage + ", context[" + postfix + "]";
    }
}
