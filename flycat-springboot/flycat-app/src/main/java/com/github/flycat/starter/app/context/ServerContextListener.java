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

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.context.ApplicationContext;
import com.github.flycat.context.ContextListener;
import com.github.flycat.context.RunContext;
import com.github.flycat.spi.alarm.AlarmUtils;

public class ServerContextListener implements ContextListener {

    @Override
    public void beforeRun(RunContext runContext) {
        AlarmUtils.sendNotify("Starting server");
    }

    @Override
    public void afterRun(ApplicationContext applicationContext) {
        ApplicationConfiguration applicationConfiguration = applicationContext.getApplicationConfiguration();
        String applicationName = applicationConfiguration.getApplicationName();
        AlarmUtils.sendNotify("Server[" + applicationName + "] started");
    }
}