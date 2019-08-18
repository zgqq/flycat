/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.autoconfigure;

//import com.aliyun.mns.client.CloudAccount;
//import com.github.flycat.spi.queue.QueueFactory;
//import com.github.flycat.spi.impl.queue.AliConnectConfig;
//import com.github.flycat.spi.impl.queue.CloudAccountHolder;
//import com.github.flycat.spi.impl.queue.QueueFactoryImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConditionalOnMissingBean(QueueFactory.class)
//public class AliMqConfiguration {
//
//    @Bean
//    @ConditionalOnProperty(value = "mq.ali.enable", havingValue = "true")
//    @ConditionalOnClass(CloudAccount.class)
//    public AliConnectConfig createConnectConfig() {
//        return new AliConnectConfig();
//    }
//
//    @Bean
//    @ConditionalOnBean(AliConnectConfig.class)
//    public CloudAccountHolder cloudAccountHolder(@Autowired AliConnectConfig connectConfig) {
//        return new CloudAccountHolder(connectConfig);
//    }
//
//    @Bean
//    @ConditionalOnBean({AliConnectConfig.class, CloudAccountHolder.class})
//    public QueueFactory createQueueFactory(@Autowired CloudAccountHolder cloudAccountHolder,
//                                           @Autowired AliConnectConfig connectConfig) {
//        return new QueueFactoryImpl(cloudAccountHolder, connectConfig.getTopicName(),
//                connectConfig.getEnv());
//    }
//}
