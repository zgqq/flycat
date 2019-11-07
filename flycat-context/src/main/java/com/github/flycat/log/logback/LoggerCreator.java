package com.github.flycat.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created by zgq
 * Date: 2018-05-08
 * Time: 4:09 PM
 */
public class LoggerCreator {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoggerCreator.class);

    public static synchronized void create(
            String logPath,
            String logger, String fileName, Level level) {
        LOGGER.info("Creating logger {}, filename {}", logger, fileName);

//    <appender name="ACTIVITY_PUSH" class="ch.qos.logback.core.rolling.RollingFileAppender">
//        <File>${logDir}activity_push.log</File>
//        <Encoding>UTF-8</Encoding>
//        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
//            <FileNamePattern>${logDir}activity_push-%d{yyyy-MM-dd}-%i.log.gz</FileNamePattern>
//            <MaxHistory>10</MaxHistory>
//            <TimeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
//                <MaxFileSize>100MB</MaxFileSize>
//            </TimeBasedFileNamingAndTriggeringPolicy>
//        </rollingPolicy>
//        <layout class="ch.qos.logback.classic.PatternLayout">
//            <Pattern>%d{yy-MM-dd HH:mm:ss.SSS} - %msg%n</Pattern>
//        </layout>
//    </appender>

        String filePrefix = logPath + fileName;

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger logger1 = loggerContext.exists(logger);
        if (logger1 != null) {
            return;
        }

        RollingFileAppender rfAppender = new RollingFileAppender();
        rfAppender.setContext(loggerContext);
        String file = filePrefix + ".log";
        rfAppender.setFile(file);
        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setMaxHistory(10);
        // rolling policies need to know their parent
        // it's one of the rare cases, where a sub-component knows about its parent
        rollingPolicy.setParent(rfAppender);
        rollingPolicy.setFileNamePattern(filePrefix + "-%d{yyyy-MM-dd}-%i.log.gz");
        rollingPolicy.start();

        SizeAndTimeBasedFNATP triggeringPolicy = new SizeAndTimeBasedFNATP();
        triggeringPolicy.setContext(loggerContext);
        triggeringPolicy.setMaxFileSize(FileSize.valueOf("100MB"));
        triggeringPolicy.setTimeBasedRollingPolicy(rollingPolicy);
        triggeringPolicy.start();

        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(triggeringPolicy);
        rollingPolicy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setCharset(Charset.defaultCharset());
        encoder.setPattern("%d{yy-MM-dd HH:mm:ss.SSS} - %msg%n");
        encoder.start();

        rfAppender.setEncoder(encoder);
        rfAppender.setRollingPolicy(rollingPolicy);
        rfAppender.setTriggeringPolicy(triggeringPolicy);
        rfAppender.start();

        // attach the rolling file appender to the logger of your choice
        Logger logbackLogger = loggerContext.getLogger(logger);
        logbackLogger.addAppender(rfAppender);
        logbackLogger.setAdditive(false);
        logbackLogger.setLevel(level);
        logbackLogger.info("Created {} logger, file:{}", logger, fileName);

        // OPTIONAL: print logback internal status messages
        StatusPrinter.print(loggerContext);
    }

    public static void main(String[] args) {
        LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();
        logEncoder.setContext(logCtx);
        logEncoder.setPattern("%-12date{YYYY-MM-dd HH:mm:ss.SSS} %-5level - %msg%n");
        logEncoder.start();
        ConsoleAppender logConsoleAppender = new ConsoleAppender();
        logConsoleAppender.setContext(logCtx);
        logConsoleAppender.setName("console");
        logConsoleAppender.setEncoder(logEncoder);
        logConsoleAppender.start();
        logEncoder = new PatternLayoutEncoder();
        logEncoder.setContext(logCtx);
        logEncoder.setPattern("%-12date{YYYY-MM-dd HH:mm:ss.SSS} %-5level - %msg%n");
        logEncoder.start();
        RollingFileAppender logFileAppender = new RollingFileAppender();
        logFileAppender.setContext(logCtx);
        logFileAppender.setName("logFile");
        logFileAppender.setEncoder(logEncoder);
        logFileAppender.setAppend(true);
        logFileAppender.setFile("logs/logfile.log");
        TimeBasedRollingPolicy logFilePolicy = new TimeBasedRollingPolicy();
        logFilePolicy.setContext(logCtx);
        logFilePolicy.setParent(logFileAppender);
        logFilePolicy.setFileNamePattern("logs/logfile-%d{yyyy-MM-dd_HH}.log");
        logFilePolicy.setMaxHistory(7);
        logFilePolicy.start();
        logFileAppender.setRollingPolicy(logFilePolicy);
        logFileAppender.start();
        Logger log = logCtx.getLogger("Main");
        log.addAppender(logConsoleAppender);
        log.addAppender(logFileAppender);
    }
}
