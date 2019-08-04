package com.github.bootbox.autoconfigure;

import com.github.bootbox.sms.SmsSender;
import com.github.bootbox.sms.qcloud.QcloudSmsSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(QcloudSmsSender.class)
@ConditionalOnProperty(name = {"sms.qcloud.appid",

})
public class QcloudSmsConfiguration {

    @Bean
    public SmsSender smsSender(@Value("${sms.qcloud.appid}") int appid,
                               @Value("${sms.qcloud.appkey}") String appkey) {
        return new QcloudSmsSender(appid, appkey);
    }
}
