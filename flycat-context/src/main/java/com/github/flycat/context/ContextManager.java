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
package com.github.flycat.context;

import com.github.flycat.log.logback.LogInterceptor;
import com.github.flycat.module.Module;
import com.github.flycat.module.ModuleManager;
import com.github.flycat.util.CollectionUtils;
import com.github.flycat.util.StringUtils;
import com.github.flycat.util.date.DateFormatter;
import com.github.flycat.util.io.FormatPrintStream;
import com.github.flycat.context.util.ServerEnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;

public class ContextManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextManager.class);

    static List<ContextListener> contextListeners;

    static {
        ServiceLoader<ContextListener> loader = ServiceLoader.load(ContextListener.class);
        contextListeners = CollectionUtils.iteratorToList(loader.iterator());
    }

    public static void beforeRun(RunContext runContext) {
        for (ContextListener contextListener : contextListeners) {
            contextListener.beforeRun(runContext);
        }
        Class<? extends Module>[] modules = runContext.getModules();
        ModuleManager.load(modules);
    }

    private static void redirectOutputStream() {
        if (!ContextUtils.isLocalProfile()) {
            final String logDir = ServerEnvUtils.getProperty("logging.file.path");
            if (StringUtils.isNotBlank(logDir)) {
                try {
                    String format = DateFormatter.YYYYMMDD_HHMMSS.format(new Date());
                    String logPath = logDir + "console.out." + format + ".log";
                    System.setOut(new PrintStream(new BufferedOutputStream(
                            new FileOutputStream(logPath)),
                            true));
                    System.setErr(new PrintStream(new BufferedOutputStream(
                            new FileOutputStream(logPath)),
                            true));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Unable to console file ", e);
                }
            }
        }
        System.setOut(new FormatPrintStream(System.out));
        System.setErr(new FormatPrintStream(System.err));
    }

    public static void afterRun(ApplicationContext applicationContext) {
        for (ContextListener contextListener : contextListeners) {
            contextListener.afterRun(applicationContext);
        }
        LogInterceptor.disableTrace = true;
        LOGGER.info("Slow log, content:{}", LogInterceptor.slowLog.get("main"));
        redirectOutputStream();
    }
}
