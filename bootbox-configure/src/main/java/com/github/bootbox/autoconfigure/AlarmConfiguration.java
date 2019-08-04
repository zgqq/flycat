package com.github.bootbox.autoconfigure;

import com.github.bootbox.alarm.AlarmSender;
import com.github.bootbox.alarm.LogAlarmSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlarmConfiguration {
    @Bean
    @ConditionalOnMissingBean(AlarmSender.class)
    public AlarmSender alarmSender() {
        return new LogAlarmSender();
    }
}
