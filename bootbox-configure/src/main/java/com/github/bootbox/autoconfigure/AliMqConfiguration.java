package com.github.bootbox.autoconfigure;

import com.aliyun.mns.client.CloudAccount;
import com.github.bootbox.queue.QueueFactory;
import com.github.bootbox.queue.ali.AliConnectConfig;
import com.github.bootbox.queue.ali.CloudAccountHolder;
import com.github.bootbox.queue.ali.QueueFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(QueueFactory.class)
public class AliMqConfiguration {

    @Bean
    @ConditionalOnProperty(value = "mq.ali.enable", havingValue = "true")
    @ConditionalOnClass(CloudAccount.class)
    public AliConnectConfig createConnectConfig() {
        return new AliConnectConfig();
    }

    @Bean
    @ConditionalOnBean(AliConnectConfig.class)
    public CloudAccountHolder cloudAccountHolder(@Autowired AliConnectConfig connectConfig) {
        return new CloudAccountHolder(connectConfig);
    }

    @Bean
    @ConditionalOnBean({AliConnectConfig.class, CloudAccountHolder.class})
    public QueueFactory createQueueFactory(@Autowired CloudAccountHolder cloudAccountHolder,
                                           @Autowired AliConnectConfig connectConfig) {
        return new QueueFactoryImpl(cloudAccountHolder, connectConfig.getTopicName(),
                connectConfig.getEnv());
    }
}
