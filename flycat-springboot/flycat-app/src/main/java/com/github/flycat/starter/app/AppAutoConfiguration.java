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
package com.github.flycat.starter.app;

import ch.qos.logback.classic.Level;
import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.log.ErrorLogFileLogger;
import com.github.flycat.log.logback.LoggerCreator;
import com.github.flycat.starter.app.web.WebAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(WebAutoConfiguration.class)
public class AppAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "logging.file.path")
    public ErrorLogFileLogger errorLogFileLogger(ApplicationConfiguration applicationConfiguration) {
        String logPath = applicationConfiguration.getString("logging.file.path");
        LoggerCreator.create(logPath, ErrorLogFileLogger.ERROR,
                ErrorLogFileLogger.ERROR, Level.INFO
        );
        return new ErrorLogFileLogger();
    }
}
