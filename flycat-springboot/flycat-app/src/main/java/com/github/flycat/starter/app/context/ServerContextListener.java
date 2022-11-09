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
package com.github.flycat.starter.app.context;

import com.github.flycat.context.*;
import com.github.flycat.context.util.ConfigurationUtils;
import com.github.flycat.spi.notifier.NotifierUtils;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class ServerContextListener implements ContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerContextListener.class);
    private volatile Stopwatch stopwatch;


    @Override
    public void beforeRun(RunContext runContext) {
        stopwatch = Stopwatch.createStarted();
        ContextFreeConfiguration contextFreeConfiguration = ContextUtils.createContextFreeConfiguration();
        boolean booleanValue = contextFreeConfiguration.getBooleanValue("flycat.notify.enabled", true);
        String defaultProductionEnvString = ContextUtils.getDefaultProductionEnvString();
        String context = contextFreeConfiguration.getStringValue("flycat.notify.startup.env", defaultProductionEnvString);
        boolean inEnv = ContextUtils.currentEnvIn(ContextUtils.parseEnvString(context));
        String currentProfile = ContextUtils.getCurrentProfile();
        LOGGER.info("Trying to notify starting, notifyEnabled:{}, inEnv:{}, currentProfile:{}, defaultProductions:{}," +
                " configs:{}", booleanValue, inEnv, currentProfile, defaultProductionEnvString, context);

        String appVersion = System.getProperty("app.version");
        String gitDiff = System.getProperty("app.git.diff");
        StringBuilder deployInfo = ContextUtils.getDeployInfo();
        String message = "Server[" + contextFreeConfiguration.getApplicationName() + "] starting\n" +
                "Version: " + appVersion + "\n" + gitDiff + "\n"+ deployInfo;
        LOGGER.info(message);
        if (booleanValue && inEnv) {
            NotifierUtils.sendNotification(message);
        }
    }


    @Override
    public void afterRun(ApplicationContext applicationContext) {
        ApplicationConfiguration applicationConfiguration = applicationContext.getApplicationConfiguration();
        boolean booleanValue = applicationConfiguration.getBooleanValue("flycat.notify.enabled", true);
        ContextFreeConfiguration contextFreeConfiguration = ContextUtils.createContextFreeConfiguration();
        String defaultProductionEnvString = ContextUtils.getDefaultProductionEnvString();
        String context = contextFreeConfiguration.getStringValue("flycat.notify.startup.env", defaultProductionEnvString);
        String currentProfile = ContextUtils.getCurrentProfile();
        boolean inEnv = ContextUtils.currentEnvIn(ContextUtils.parseEnvString(context));
        LOGGER.info("Trying to notify started, notifyEnabled:{}, inEnv:{}, currentProfile:{}, defaultProductions:{}," +
                " configs:{}", booleanValue, inEnv, currentProfile, defaultProductionEnvString, context);

        String applicationName = applicationConfiguration.getApplicationName();
        String message = "Server[" + applicationName + "] started";

        if (stopwatch != null) {
            stopwatch.stop();
            message = message + "\nStartup cost: " + stopwatch;
        }


        message = message + "\n" + ContextUtils.getDeployDetail();
        LOGGER.info(message);
        if (booleanValue && inEnv) {
            NotifierUtils.sendNotification(message);
        }
    }
}
