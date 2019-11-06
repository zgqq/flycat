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
