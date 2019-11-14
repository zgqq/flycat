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

import com.github.flycat.module.Module;
import com.github.flycat.module.ModuleManager;
import com.github.flycat.util.CollectionUtils;
import com.github.flycat.util.StringUtils;
import com.github.flycat.util.properties.ServerEnvUtils;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.ServiceLoader;

public class ContextManager {

    static List<ContextListener> contextListeners;

    static {
        ServiceLoader<ContextListener> loader = ServiceLoader.load(ContextListener.class);
        contextListeners = CollectionUtils.iteratorToList(loader.iterator());
    }

    public static void beforeRun(RunContext runContext) {
        for (ContextListener contextListener : contextListeners) {
            contextListener.beforeRun(runContext);
        }
        if (!ContextUtils.isLocalProfile()) {
            final String logDir = ServerEnvUtils.getProperty("logging.path");
            if (StringUtils.isNotBlank(logDir)) {
                try {
                    System.setOut(new PrintStream(new BufferedOutputStream(
                            new FileOutputStream(logDir + "console.out.log")),
                            true));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Unable to console file ", e);
                }
            }
        }
        Class<? extends Module>[] modules = runContext.getModules();
        ModuleManager.load(modules);
    }

    public static void afterRun(ApplicationContext applicationContext) {
        for (ContextListener contextListener : contextListeners) {
            contextListener.afterRun(applicationContext);
        }
    }
}
