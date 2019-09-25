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
package com.github.flycat.platform.springboot;

import com.github.flycat.wechat.mp.MiniProgramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WechatConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatConfiguration.class);

    @Configuration
    @ConditionalOnProperty(name = "wechat.mp.enable", havingValue = "true")
    public static class MiniProgramConfiguration {
        @Bean
        public MiniProgramClient miniProgramClient(@Value("${wechat.mp.appid}") String appid,
                                                   @Value("${wechat.mp.secret}") String secret) {
            LOGGER.info("Creating wechat mp client, appid:{}, secret:{}", appid, secret);
            return new MiniProgramClient(appid, secret);
        }

//        @Bean
//        @ConditionalOnBean(WechatLoginHandler.class)
//        public WechatLoginInterceptor wechatLoginInterceptor(WechatLoginHandler wechatLoginHandler) {
//            LOGGER.info("Creating wechat login interceptor");
//            return new WechatLoginInterceptor(wechatLoginHandler);
//        }
    }
}
