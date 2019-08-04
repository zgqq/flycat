package com.github.bootbox.autoconfigure;

import com.github.bootbox.wechat.WechatLoginHandler;
import com.github.bootbox.wechat.WechatLoginInterceptor;
import com.github.bootbox.wechat.mp.MiniProgramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

        @Bean
        @ConditionalOnBean(WechatLoginHandler.class)
        public WechatLoginInterceptor wechatLoginInterceptor(WechatLoginHandler wechatLoginHandler) {
            LOGGER.info("Creating wechat login interceptor");
            return new WechatLoginInterceptor(wechatLoginHandler);
        }
    }
}
