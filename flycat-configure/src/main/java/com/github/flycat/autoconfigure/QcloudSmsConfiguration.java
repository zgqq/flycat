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

//import com.github.flycat.spi.sms.SmsService;
//import com.github.flycat.spi.sms.qcloud.QcloudSmsSender;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConditionalOnClass(QcloudSmsSender.class)
//@ConditionalOnProperty(name = {"sms.qcloud.appid",
//
//})
//public class QcloudSmsConfiguration {
//
//    @Bean
//    public SmsService smsSender(@Value("${sms.qcloud.appid}") int appid,
//                                @Value("${sms.qcloud.appkey}") String appkey) {
//        return new QcloudSmsSender(appid, appkey);
//    }
//}
