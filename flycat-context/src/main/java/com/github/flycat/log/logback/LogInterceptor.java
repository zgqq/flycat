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
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LogInterceptor extends TurboFilter {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);
    private static ThreadLocal<StopwatchWrapper> stopwatchLocal = new ThreadLocal<>();
    public static Map<String, String> slowLog = new ConcurrentHashMap<>();
    public static volatile boolean disableTrace;

    @Override
    public FilterReply decide(Marker marker, Logger logger,
                              Level level, String s, Object[] objects, Throwable throwable) {
        String name = logger.getName();
        if (ErrorLogFileLogger.ERROR.equals(name)) {
            beforeLog(marker, logger, level, s, objects, throwable);
            return FilterReply.NEUTRAL;
        }

        if (throwable == null && objects != null && objects.length > 0 ) {
            Object lastArg = objects[objects.length - 1];
            if (lastArg instanceof Throwable) {
                throwable = (Throwable) lastArg;
            }
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
//                beforeLog(marker, logger, level, s, objects, throwable);
                return FilterReply.DENY;
            }
            return FilterReply.NEUTRAL;
        } else {
            if (level.levelInt == Level.ERROR_INT) {
                onError(logger, s, throwable, s, objects);
            }
            beforeLog(marker, logger, level, s, objects, throwable);
            return FilterReply.NEUTRAL;
        }
    }

    private static List<String> ignoredLoggers = Lists.newArrayList("org.springframework");

    protected void onError(Logger logger, String message, Throwable throwable,
                           String mdcMessage, Object[] args) {
        final String name = logger.getName();
        for (String ignoredLogger : ignoredLoggers) {
            if (name.startsWith(ignoredLogger)) {
                LOGGER.debug("Ignored exception, logger:{}, message:{}", logger, message);
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

    private void beforeLog(Marker marker, Logger logger,
                           Level level, String message, Object[] objects, Throwable throwable) {
        if (disableTrace == true) {
            return;
        }
        StopwatchWrapper stopwatchWrapper = stopwatchLocal.get();
        if (stopwatchWrapper == null) {
            stopwatchWrapper = new StopwatchWrapper(Stopwatch.createStarted());
            stopwatchWrapper.setLogger(logger.getName());
            stopwatchLocal.set(stopwatchWrapper);
            return;
        }
        Stopwatch stopwatch = stopwatchWrapper.getStopwatch();
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if (elapsed >= 30) {
            String name = Thread.currentThread().getName();
            String prev = slowLog.get(name);
            String chainMessage;
            if (prev == null) {
                chainMessage = logger.getName() + ":" + message + " \n==> " + stopwatchWrapper.getLogger() + ":" + stopwatchWrapper.getMessage() + " cost " + elapsed + "ms";
            } else {
                chainMessage = prev + "\n" + logger.getName() + ":" + message + " \n==> " + stopwatchWrapper.getLogger() + ":" + stopwatchWrapper.getMessage() + " cost " + elapsed + "ms";
            }
            slowLog.put(name, chainMessage);
        }

        stopwatchWrapper = new StopwatchWrapper(Stopwatch.createStarted());
        stopwatchWrapper.setLogger(logger.getName());
        stopwatchWrapper.setLogger(logger.getName());
        stopwatchWrapper.setMessage(message);
        stopwatchLocal.set(stopwatchWrapper);
    }

    public static class StopwatchWrapper {
        private final Stopwatch stopwatch;
        private String logger;
        private String message;

        public StopwatchWrapper(Stopwatch stopwatch) {
            this.stopwatch = stopwatch;
        }

        public Stopwatch getStopwatch() {
            return stopwatch;
        }

        public String getLogger() {
            return logger;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setLogger(String logger) {
            this.logger = logger;
        }
    }
}
